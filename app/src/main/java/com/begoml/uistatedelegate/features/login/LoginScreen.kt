package com.begoml.uistatedelegate.features.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.begoml.scopingstatescompose.navigation.navigateSingleTopTo
import com.begoml.uistatedelegate.navigation.NavigationDestination
import com.begoml.uistatedelegate.uistate.collectEvent
import com.begoml.uistatedelegate.uistate.collectWithLifecycle

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    lifecycle: LifecycleOwner = LocalLifecycleOwner.current
) {
    val uiState by viewModel.collectWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.collectEvent(lifecycle) { event ->
            when (event) {
                LoginViewModel.Event.GoToHome -> {
                    navController.navigateSingleTopTo(NavigationDestination.Home.destination)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = uiState.title
        )
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colors.primary,
                )
            }
        }
        TextField(
            modifier = Modifier
                .padding(top = 24.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            value = uiState.login,
            onValueChange = viewModel::onLoginChange,
            enabled = uiState.isLoading.not(),
        )
        TextField(
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            enabled = uiState.isLoading.not(),
        )
        Button(
            modifier = Modifier
                .padding(top = 24.dp)
                .fillMaxWidth(),
            onClick = viewModel::onLoginClick,
            enabled = uiState.isLoading.not(),
        ) {
            Text("Login")
        }
    }
}
