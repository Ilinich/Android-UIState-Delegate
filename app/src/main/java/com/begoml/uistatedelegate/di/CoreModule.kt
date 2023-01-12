package com.begoml.uistatedelegate.di

import com.begoml.core.ApiService
import com.begoml.core.AuthRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoreModule {

    @Singleton
    @Provides
    fun provideApiService() = ApiService()

    @Provides
    fun provideAuthRepository(apiService: ApiService) = AuthRepository(apiService)
}
