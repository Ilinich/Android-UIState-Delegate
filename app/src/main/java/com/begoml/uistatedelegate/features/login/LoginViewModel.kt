package com.begoml.uistatedelegate.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.core.AuthRepository
import com.begoml.uistatedelegate.features.login.LoginViewModel.Event
import com.begoml.uistatedelegate.features.login.LoginViewModel.UiState
import com.begoml.uistatedelegate.state.UiStateDelegate
import com.begoml.uistatedelegate.state.UiStateDelegateImpl
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel(), UiStateDelegate<UiState, Event> by UiStateDelegateImpl(UiState()) {

    data class UiState(
        val isLoading: Boolean = false,
        val title: String = "",
        val login: String = "",
        val password: String = "",
    )

    sealed interface Event {
        object GoToHome : Event
    }

    init {
        viewModelScope.launch {
            updateUiState { state ->
                state.copy(
                    title = "Login screen"
                )
            }
        }
    }

    fun onLoginChange(login: String) {
        asyncUpdateUiState(viewModelScope) { state -> state.copy(login = login) }
    }

    fun onPasswordChange(password: String) {
        asyncUpdateUiState(viewModelScope) { state -> state.copy(password = password) }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            updateUiState { state -> state.copy(isLoading = true) }
            authRepository.login(login = uiState.login, password = uiState.password)
            sendEvent(Event.GoToHome)
        }.invokeOnCompletion { asyncUpdateUiState(viewModelScope) { state -> state.copy(isLoading = false) } }
    }
}
