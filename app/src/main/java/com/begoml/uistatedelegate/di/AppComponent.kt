package com.begoml.uistatedelegate.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [CoreModule::class]
)
interface AppComponent : AppProvider
