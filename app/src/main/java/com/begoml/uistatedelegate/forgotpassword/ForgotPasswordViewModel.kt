package com.begoml.uistatedelegate.forgotpassword

import android.text.Editable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.begoml.core.AuthRepository
import com.begoml.uistatedelegate.forgotpassword.ForgotPasswordViewModel.Event
import com.begoml.uistatedelegate.forgotpassword.ForgotPasswordViewModel.UiState
import com.begoml.uistatedelegate.uistate.ViewStateDelegate
import com.begoml.uistatedelegate.uistate.ViewStateDelegateImpl
import kotlinx.coroutines.launch

class ForgotPasswordViewModelFactory(
    private val authRepository: AuthRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ForgotPasswordViewModel(authRepository = authRepository) as T
    }
}

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository,
) : ViewModel(),
    ViewStateDelegate<UiState, Event> by ViewStateDelegateImpl(UiState()) {

    data class UiState(
        val isLoading: Boolean = false,
        val title: String = "",
        val login: String = "",
    )

    sealed interface Event {
        object FinishFlow : Event
    }

    init {
        viewModelScope.launch {
            reduce { state -> state.copy(title = "Forgot Password") }
        }
    }

    fun onLoginTextChanged(value: Editable?) {
        viewModelScope.asyncReduce { state -> state.copy(login = value.toString()) }
    }

    fun onForgotPasswordClick() {
        viewModelScope.launch {
            reduce { state -> state.copy(isLoading = true) }
            authRepository.forgotPassword(stateValue.login)
            sendEvent(Event.FinishFlow)
        }.invokeOnCompletion { viewModelScope.asyncReduce { state -> state.copy(isLoading = false) } }
    }
}
