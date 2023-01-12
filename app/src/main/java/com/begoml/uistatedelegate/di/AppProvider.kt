package com.begoml.uistatedelegate.di

import androidx.compose.runtime.compositionLocalOf
import com.begoml.core.ApiService
import com.begoml.core.AuthRepository

interface AppProvider {

    fun provideAuthRepository(): AuthRepository
}

val LocalAppProvider = compositionLocalOf<AppProvider> { error("No app provider found!") }
