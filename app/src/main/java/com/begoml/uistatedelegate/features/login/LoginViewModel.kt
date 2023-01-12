package com.begoml.uistatedelegate.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.core.AuthRepository
import com.begoml.uistatedelegate.features.login.LoginViewModel.Event
import com.begoml.uistatedelegate.features.login.LoginViewModel.UiState
import com.begoml.uistatedelegate.uistate.ViewStateDelegate
import com.begoml.uistatedelegate.uistate.ViewStateDelegateImpl
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel(), ViewStateDelegate<UiState, Event> by ViewStateDelegateImpl(UiState()) {

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
            reduce { state ->
                state.copy(
                    title = "Login screen"
                )
            }
        }
    }

    fun onLoginChange(login: String) {
        viewModelScope.asyncReduce { state -> state.copy(login = login) }
    }

    fun onPasswordChange(password: String) {
        viewModelScope.asyncReduce { state -> state.copy(password = password) }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            reduce { state -> state.copy(isLoading = true) }
            authRepository.login(login = stateValue.login, password = stateValue.password)
            sendEvent(Event.GoToHome)
        }.invokeOnCompletion { viewModelScope.asyncReduce { state -> state.copy(isLoading = false) } }
    }
}
