package com.begoml.uistatedelegate.di

import androidx.compose.runtime.compositionLocalOf
import com.begoml.core.AuthRepository
import com.begoml.core.home.DashboardRepository
import com.begoml.uistatedelegate.features.delegates.ToolbarDelegate

interface AppProvider {

    fun provideAuthRepository(): AuthRepository

    fun provideDashboardRepository(): DashboardRepository

    fun provideToolbarDelegate(): ToolbarDelegate
}

val LocalAppProvider = compositionLocalOf<AppProvider> { error("No app provider found!") }
