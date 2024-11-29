package org.smartregister.fct.sm.presentation.component

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.Resource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.smartregister.fct.common.presentation.component.ScreenComponent
import org.smartregister.fct.common.util.windowTitle
import org.smartregister.fct.editor.presentation.components.CodeEditorComponent
import org.smartregister.fct.engine.data.enums.FileType
import org.smartregister.fct.engine.util.componentScope
import org.smartregister.fct.engine.util.decodeResourceFromString
import org.smartregister.fct.engine.util.listOfAllFhirResources
import org.smartregister.fct.engine.util.prettyJson
import org.smartregister.fct.engine.util.uuid
import org.smartregister.fct.fm.util.FileUtil
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.sm.data.transformation.SMTransformService
import org.smartregister.fct.sm.domain.model.SMModel
import org.smartregister.fct.sm.domain.usecase.CreateNewSM
import org.smartregister.fct.sm.domain.usecase.DeleteSM
import org.smartregister.fct.sm.domain.usecase.GetAllSM
import org.smartregister.fct.sm.domain.usecase.UpdateSM
import org.smartregister.fct.sm.util.SMConfig


class StructureMapScreenComponent(private val componentContext: ComponentContext) : ScreenComponent,
    KoinComponent, ComponentContext by componentContext {

    private val getAllStructureMaps: GetAllSM by inject()
    private val updateStructureMap: UpdateSM by inject()
    private val createNewStructureMap: CreateNewSM by inject()
    private val deleteStructureMap: DeleteSM by inject()

    internal var allStructureMaps: List<SMModel> = listOf()
        private set

    private val _error = MutableStateFlow<String?>(null)
    internal val error: StateFlow<String?> = _error

    private val _info = MutableStateFlow<String?>(null)
    internal val info: StateFlow<String?> = _info

    private val _showLoader = MutableStateFlow(false)
    internal val showLoader: StateFlow<Boolean> = _showLoader

    private val _showAllStructureMapPanel = MutableStateFlow(false)
    internal val showAllStructureMapPanel: StateFlow<Boolean> = _showAllStructureMapPanel

    private val _structureMapResult = MutableStateFlow<Bundle?>(null)
    internal val structureMapResult: StateFlow<Bundle?> = _structureMapResult

    private val transformService: SMTransformService by inject()

    internal val codeEditorComponent = CodeEditorComponent(this)

    private val _openPath = MutableStateFlow<String?>(null)
    internal val openPath: StateFlow<String?> = _openPath

    private val _sourceFileType = MutableStateFlow("")
    internal val sourceFileType: StateFlow<String> = _sourceFileType

    private val _activeStructureMap = MutableStateFlow<SMModel?>(null)
    internal val activeStructureMap: StateFlow<SMModel?> = _activeStructureMap

    private val _groups = MutableStateFlow(listOf<String>())
    internal val groups: StateFlow<List<String>> = _groups

    private val _outputResources = MutableStateFlow(listOf<String>())
    internal val outputResources: StateFlow<List<String>> = _outputResources

    init {
        listenEditorChanges()
        SMConfig.activeStructureMap.let {
            if (it != null) {
                openStructureMap(it, it.getLastOpenPath())
            } else {
                setWindowTitle(null)
            }
        }
        componentScope.launch {
            getAllStructureMaps().collectLatest {
                allStructureMaps = it
            }
        }
    }

    private fun listenEditorChanges() {
        componentScope.launch {
            codeEditorComponent.textField.collectLatest {
                if (_openPath.value == _activeStructureMap.value?.mapPath) {

                    val groupsResult = async { getTotalGroups(it.text) }
                    val outputResourcesResult = async { getOutputResources(it.text) }

                    val awaitGroup = groupsResult.await()
                    val awaitOutputResource = outputResourcesResult.await()

                    _groups.emit(awaitGroup)
                    _outputResources.emit(awaitOutputResource)
                }
            }
        }
    }

    override fun onDestroy() {
        saveOpenedFile()
        SMConfig.activeStructureMap = _activeStructureMap.value
    }

    internal fun showError(error: String?) {
        componentScope.launch {
            _error.emit(error)
        }
    }

    internal fun showInfo(info: String?) {
        componentScope.launch {
            _info.emit(info)
        }
    }

    private fun showLoader(isShow: Boolean) {
        componentScope.launch {
            _showLoader.emit(isShow)
        }
    }

    internal fun toggleAllStructureMapPanel() {
        componentScope.launch {
            _showAllStructureMapPanel.emit(!_showAllStructureMapPanel.value)
        }
    }

    internal fun createNewStructureMap(name: String) {
        componentScope.launch {

            val smId = uuid()
            val mapPath = SMConfig.getStructureMapFilePath(smId)
            val sourcePath = SMConfig.getSourceFilePath(smId)

            // create default empty structure-map
            FileUtil.writeFile(mapPath)

            // create default patient source
            FileUtil.writeFile(sourcePath)

            // create new structure-map
            val smModel = SMModel(
                id = smId,
                name = name,
                mapPath = mapPath.toFile().path,
                sourcePath = sourcePath.toFile().path
            )

            // insert new structure-map in database
            createNewStructureMap(smModel)

            // open newly created structure-map and save active structure-map
            openStructureMap(smModel, smModel.mapPath)
        }
    }

    private fun saveStructureMap(smModel: SMModel) {
        componentScope.launch {
            updateStructureMap(smModel)
        }
    }

    internal fun openStructureMap(smModel: SMModel, pathToOpen: String) {
        componentScope.launch {

            if (_activeStructureMap.value != null && smModel.id == SMConfig.activeStructureMap?.id) return@launch

            SMConfig.activeStructureMap?.let(::saveStructureMap)

            _activeStructureMap.emit(smModel)
            openPath(pathToOpen)
            setWindowTitle(smModel)
            SMConfig.activeStructureMap = smModel
        }
    }

    internal fun deleteStructureMap(smModel: SMModel) {

        componentScope.launch {

            // delete active structure-map folder
            FileUtil.deleteFolder(SMConfig.getStructureMapDirPath(smModel.id).toPath())

            // delete structure-map from database
            deleteStructureMap(smModel.id)
        }
    }

    internal fun setWorkflowResult(bundle: Bundle?) {
        componentScope.launch {
            _structureMapResult.emit(bundle)
        }
    }

    private fun setWindowTitle(smModel: SMModel?) {
        componentScope.launch {
            val name = if (smModel != null) " - ${smModel.name}" else ""
            windowTitle.emit("Structure Map$name")
        }
    }

    internal fun openPath(path: String) {
        componentScope.launch {

            // save current file
            saveOpenedFile()

            // open path
            _openPath.emit(path)
            val content = FileUtil.readFile(path.toPath())

            codeEditorComponent.setFileType(findFileType(path))
            codeEditorComponent.setText(content)

            // update last open path in config
            _activeStructureMap.value?.updateLastOpenPath(path)

            if (path == _activeStructureMap.value?.sourcePath) {
                updateSourceFileType(content)
            }
        }
    }

    internal fun updateOpenedFileContent(text: String) {
        componentScope.launch {
            try {

                if (_openPath.value == _activeStructureMap.value?.sourcePath) {
                    codeEditorComponent.setText(text.prettyJson())
                    updateSourceFileType(text)
                } else {
                    codeEditorComponent.setText(text)
                }

            } catch (ex: Exception) {
                FCTLogger.e(ex)
                showError(ex.message)
            }
        }
    }

    internal fun execute() {
        componentScope.launch {
            try {

                // save open workflow file
                saveOpenedFile()

                // show loader
                showLoader(true)

                // transformation is too fast add intentional delay
                delay(500)

                val mapFileContent = FileUtil.readFile(_activeStructureMap.value!!.mapPath.toPath())

                val sourceFileContent =
                    FileUtil.readFile(_activeStructureMap.value!!.sourcePath.toPath())

                // check source is valid fhir resource if not empty
                if (sourceFileContent.trim().isNotEmpty()) {
                    sourceFileContent.decodeResourceFromString<Resource>()
                }

                // apply transformation
                val result = transformService.transform(
                    mapFileContent,
                    sourceFileContent.trim().ifEmpty { null })

                // hide loader
                showLoader(false)

                if (result.isSuccess) {
                    _structureMapResult.emit(result.getOrThrow())
                } else {
                    FCTLogger.e(result.exceptionOrNull())
                    showError(result.exceptionOrNull()?.message)
                }

            } catch (ex: Exception) {
                showLoader(false)
                FCTLogger.e(ex)
                showError(ex.message)
            }
        }
    }

    private fun saveOpenedFile() {
        _openPath.value?.let { path ->
            FileUtil.writeFile(path.toPath(), codeEditorComponent.getText())
        }
    }

    private fun getTotalGroups(text: String): List<String> {

        return "(?<=(\\n|\\r|\\s|\\})group\\s)\\w+(?=\\s*\\()"
            .toRegex()
            .findAll(text)
            .map { it.value }
            .toList()
    }

    private fun getOutputResources(text: String): List<String> {
        return "(?<=['\"])\\w+(?=(['\"])\\s*\\)\\s*as)"
            .toRegex()
            .findAll(text)
            .filter { it.value in listOfAllFhirResources }
            .map { it.value }.toList()
    }

    private fun findFileType(path: String): FileType? {
        return when (FileUtil.getFileExtension(path)) {

            "map" -> FileType.StructureMap
            "json" -> FileType.Json
            else -> null
        }
    }

    private fun updateSourceFileType(text: String) {
        componentScope.launch(Dispatchers.IO) {
            try {
                _sourceFileType.emit(text.decodeResourceFromString<Resource>().resourceType.name)
            } catch (ex: Exception) {
                _sourceFileType.emit("")
                FCTLogger.e(ex)
            }
        }
    }
}