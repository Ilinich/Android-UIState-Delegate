package com.begoml.uistatedelegate.features.user

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.begoml.uistatedelegate.ui.components.AppTopBar

@Composable
fun UserScreen(
    viewModel: UserViewModel,
) {
    val toolbarUiState by viewModel.toolbarUiState.collectAsState()

    AppTopBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        state = toolbarUiState,
    )
}