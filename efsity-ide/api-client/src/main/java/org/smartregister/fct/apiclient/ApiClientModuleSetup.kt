package org.smartregister.fct.apiclient

import com.google.gson.FormattingStyle
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.core.context.GlobalContext
import org.koin.dsl.module
import org.smartregister.fct.apiclient.data.datasource.KtorApiClientDataSource
import org.smartregister.fct.apiclient.data.repository.ApiClientRepository
import org.smartregister.fct.apiclient.domain.datasource.ApiClientDataSource
import org.smartregister.fct.apiclient.domain.usecase.ApiRequest
import org.smartregister.fct.apiclient.domain.usecase.AuthenticateClient
import org.smartregister.fct.apiclient.domain.usecase.UploadResource
import org.smartregister.fct.engine.setup.ModuleSetup
import org.smartregister.fct.logger.FCTLogger

class ApiClientModuleSetup : ModuleSetup {

    private val apiClientModule = module(createdAtStart = true) {
        single<Gson> {
            GsonBuilder()
                .disableHtmlEscaping()
                .setFormattingStyle(FormattingStyle.PRETTY.withIndent(" ".repeat(4)))
                .create()
        }
        single<ApiClientDataSource> {
            KtorApiClientDataSource(get())
        }
        single<ApiClientRepository> {
            ApiClientRepository(get())
        }
        factory<AuthenticateClient> { AuthenticateClient() }
        factory<UploadResource> { UploadResource() }
        factory<ApiRequest> { ApiRequest() }
    }

    override suspend fun setup() {
        FCTLogger.d("Loading... Api Client Module")
        GlobalContext.get().loadModules(listOf(apiClientModule))
        FCTLogger.d("Api Client Module Loaded")
    }
}