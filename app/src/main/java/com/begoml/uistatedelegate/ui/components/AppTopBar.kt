package com.begoml.uistatedelegate.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.begoml.uistatedelegate.features.delegates.ToolbarUiState

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    state: ToolbarUiState,
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = state.title,
        )
        Text(
            modifier = Modifier.padding(top = 2.dp),
            text = "${state.levelLabel} ${state.levelProgress}",
        )
    }
}