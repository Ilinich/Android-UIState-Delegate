package com.begoml.uistatedelegate.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.core.home.DashboardRepository
import com.begoml.uistatedelegate.features.home.HomeViewModel.State
import com.begoml.uistatedelegate.features.home.HomeViewModel.UiState
import com.begoml.uistatedelegate.features.home.HomeViewModel.Event
import com.begoml.uistatedelegate.features.home.models.DashboardUi
import com.begoml.uistatedelegate.features.home.models.toDashboardUi
import com.begoml.uistatedelegate.features.delegates.ToolbarDelegate
import com.begoml.uistatedelegate.state.CombinedStateDelegate
import com.begoml.uistatedelegate.state.CombinedStateDelegateImpl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class HomeViewModel(
    private val dashboardRepository: DashboardRepository,
    private val toolbarDelegate: ToolbarDelegate,
) : ViewModel(),
    ToolbarDelegate by toolbarDelegate,
    CombinedStateDelegate<UiState, State, Event> by CombinedStateDelegateImpl(
    initialState = State(),
    initialUiState = UiState(),
) {

    data class UiState(
        val isLoading: Boolean = false,
        val items: List<DashboardUi> = emptyList(),

        val filter: String = "",
    )

    data class State(
        val fullItems: List<DashboardUi> = emptyList(),
    )

    sealed interface Event {
        object StartForgotPasswordFeature : Event
        object StartUserFeature : Event
    }

    init {
        collectUpdateUiState(viewModelScope) { state, uiState ->
            val newItems = if (uiState.filter.isBlank()) {
                state.fullItems
            } else {
                state.fullItems.filter { item -> item.title.contains(uiState.filter) }
            }
            uiState.copy(items = newItems)
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            updateUiState { uiState, _ -> uiState.copy(isLoading = true) }
            val items = runCatching { dashboardRepository.getHomeItems() }
                .getOrDefault(emptyList())

            val uiItems = items.map { item -> item.toDashboardUi() }

            updateInternalState { state -> state.copy(fullItems = uiItems) }
        }.invokeOnCompletion {
            asyncUpdateUiState(viewModelScope) { uiState -> uiState.copy(isLoading = false) }
        }
    }

    fun onForgotPasswordClick() {
        viewModelScope.launch { sendEvent(Event.StartForgotPasswordFeature) }
    }

    fun onUserClick() {
        viewModelScope.launch { sendEvent(Event.StartUserFeature) }
    }
}