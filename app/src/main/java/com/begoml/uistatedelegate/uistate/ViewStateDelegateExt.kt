package com.begoml.uistatedelegate.uistate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun <R> ViewStateDelegate<R, *>.collectUiState() = this.uiState.collectAsState(initial = this.stateValue)

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun <R> ViewStateDelegate<R, *>.collectWithLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
) = this.uiState.collectAsStateWithLifecycle(
    initialValue = this.stateValue,
    minActiveState = minActiveState,
)
