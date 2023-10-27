package com.begoml.uistatedelegate.di

import com.begoml.core.ApiService
import com.begoml.core.AuthRepository
import com.begoml.core.home.DashboardRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import javax.inject.Singleton

@Module
class CoreModule {

    @Singleton
    @Provides
    fun provideApiService() = ApiService()

    @Provides
    fun provideAuthRepository(apiService: ApiService) = AuthRepository(apiService)

    @Provides
    @Reusable
    fun provideDashboardRepository(apiService: ApiService) = DashboardRepository(apiService)
}
