package org.smartregister.fct.engine

import org.hl7.fhir.r4.context.IWorkerContext
import org.hl7.fhir.r4.context.SimpleWorkerContext
import org.hl7.fhir.r4.model.Parameters
import org.hl7.fhir.r4.utils.FHIRPathEngine
import org.hl7.fhir.r4.utils.StructureMapUtilities
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.database.Database
import org.smartregister.fct.engine.data.helper.TransformSupportServices
import org.smartregister.fct.engine.data.manager.AppSettingManager
import org.smartregister.fct.engine.domain.usecase.GetAppSetting
import org.smartregister.fct.engine.domain.usecase.UpdateAppSetting
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.logger.FCTLogger
import sqldelight.AppSettingsDaoQueries

class EngineModuleSetup : ModuleSetup {

    private val engineModule = module(createdAtStart = true) {
        single<AppSettingsDaoQueries> {
            Database.getDatabase().appSettingsDaoQueries
        }
        single<AppSettingManager> {
            AppSettingManager(
                GetAppSetting(get()),
                UpdateAppSetting(get())
            )
        }
        single<FHIRPathEngine> {
            FHIRPathEngine(get<TransformSupportServices>().simpleWorkerContext)
        }
        single<TransformSupportServices> { TransformSupportServices(get())  }
        single<FilesystemPackageCacheManager> { FilesystemPackageCacheManager(true) }
        single<SimpleWorkerContext> {
            SimpleWorkerContext.fromPackage(
                get<FilesystemPackageCacheManager>().loadPackage(
                    "hl7.fhir.r4.core",
                    "4.0.1"
                )
            ).apply {
                setExpansionProfile(Parameters())
                isCanRunWithoutTerminology = true
            }
        }
    }

    override suspend fun setup() {
        FCTLogger.d("Loading... Engine Module")
        GlobalContext.get().loadModules(listOf(engineModule))
        FCTLogger.d("Engine Module Loaded")
    }
}