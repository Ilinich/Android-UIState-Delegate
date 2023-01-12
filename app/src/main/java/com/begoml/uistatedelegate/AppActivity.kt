package com.begoml.uistatedelegate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.begoml.core.AuthRepository
import com.begoml.uistatedelegate.di.LocalAppProvider
import com.begoml.uistatedelegate.features.common.daggerViewModel
import com.begoml.uistatedelegate.features.home.HomeScreen
import com.begoml.uistatedelegate.features.login.LoginScreen
import com.begoml.uistatedelegate.features.login.LoginViewModel
import com.begoml.uistatedelegate.navigation.NavigationDestination
import com.begoml.uistatedelegate.ui.theme.AppTheme

class AppActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    CompositionLocalProvider(
                        LocalAppProvider provides application.appProvider,
                    ) {
                        val navController = rememberNavController()

                        NavHost(navController, startDestination = NavigationDestination.Login.destination) {
                            composable(NavigationDestination.Login.destination) {
                                val provider = LocalAppProvider.current
                                val viewModel = daggerViewModel {
                                    LoginViewModel(
                                        authRepository = provider.provideAuthRepository()
                                    )
                                }
                                LoginScreen(
                                    navController = navController,
                                    viewModel = viewModel,
                                )
                            }

                            composable(NavigationDestination.Home.destination) {
                                HomeScreen(
                                    navController = navController,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
