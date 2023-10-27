package com.begoml.uistatedelegate.features.home

import android.content.Intent
import com.begoml.uistatedelegate.features.home.HomeViewModel.Event
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.begoml.uistatedelegate.forgotpassword.ForgotPasswordActivity
import com.begoml.uistatedelegate.navigation.NavigationDestination
import com.begoml.uistatedelegate.ui.components.AppTopBar
import com.begoml.uistatedelegate.uistate.CollectEventEffect

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel,
) {
    val context = LocalContext.current

    viewModel.CollectEventEffect { event ->
        return@CollectEventEffect when (event) {
            Event.StartForgotPasswordFeature -> {
                context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
            }

            Event.StartUserFeature -> {
                navController.navigate(NavigationDestination.User.destination)
            }
        }
    }

    val uiState by viewModel.uiStateFlow.collectAsState()
    val toolbarUiState by viewModel.toolbarUiState.collectAsState()

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        AppTopBar(
            modifier = Modifier.fillMaxWidth(),
            state = toolbarUiState,
        )
        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = "Home screen"
        )
        Button(onClick = viewModel::onForgotPasswordClick) {
            Text(text = "Go To Forgot Password Flow")
        }
        Button(onClick = viewModel::onUserClick) {
            Text(text = "Go To User Flow")
        }
    }
}
