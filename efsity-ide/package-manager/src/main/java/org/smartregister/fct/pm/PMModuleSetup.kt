package org.smartregister.fct.pm

import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.database.Database
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.logger.FCTLogger
import org.smartregister.fct.pm.data.datasource.SqlDelightPackageDataSource
import org.smartregister.fct.pm.data.repository.SqlDelightPackageRepository
import org.smartregister.fct.pm.domain.datasource.PackageDataSource
import org.smartregister.fct.pm.domain.repository.PackageRepository
import org.smartregister.fct.pm.domain.usecase.DeletePackage
import org.smartregister.fct.pm.domain.usecase.GetAllPackages
import org.smartregister.fct.pm.domain.usecase.GetSavedPackages
import org.smartregister.fct.pm.domain.usecase.SaveNewPackage
import org.smartregister.fct.pm.domain.usecase.UpdatePackage

class PMModuleSetup : ModuleSetup {

    private val pmModule = module(createdAtStart = true) {
        single<PackageDataSource> { SqlDelightPackageDataSource(Database.getDatabase().packageInfoDaoQueries) }
        single<PackageRepository> { SqlDelightPackageRepository(get()) }
        single<GetAllPackages> { GetAllPackages(get()) }
        single<GetSavedPackages> { GetSavedPackages(get()) }
        single<SaveNewPackage> { SaveNewPackage(get()) }
        single<UpdatePackage> { UpdatePackage(get()) }
        single<DeletePackage> { DeletePackage(get()) }
    }

    override suspend fun setup() {
        FCTLogger.d("Loading... Package Manager Module")
        GlobalContext.get().loadModules(listOf(pmModule))
        FCTLogger.d("Package Manager Module Loaded")
    }
}