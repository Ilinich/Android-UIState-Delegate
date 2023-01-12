package com.begoml.uistatedelegate

import android.app.Application
import com.begoml.uistatedelegate.di.AppProvider
import com.begoml.uistatedelegate.di.DaggerAppComponent

class AppApplication : Application() {

    lateinit var appProvider: AppProvider
        private set

    override fun onCreate() {
        super.onCreate()

        appProvider = DaggerAppComponent.builder()
            .build()
    }
}


val Application.appProvider: AppProvider
    get() = (this as AppApplication).appProvider
