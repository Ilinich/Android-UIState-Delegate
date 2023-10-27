package com.begoml.uistatedelegate.features.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.uistatedelegate.features.delegates.payments.PaymentDelegate
import kotlinx.coroutines.launch
import com.begoml.uistatedelegate.features.shop.ShopViewModel.UiState
import com.begoml.uistatedelegate.state.UiStateDelegate
import com.begoml.uistatedelegate.state.UiStateDelegateImpl
import kotlinx.coroutines.CoroutineExceptionHandler

class ShopViewModel(
    paymentDelegate: PaymentDelegate
) : ViewModel(),
    PaymentDelegate by paymentDelegate,
    UiStateDelegate<UiState, Unit> by UiStateDelegateImpl(UiState()) {

    data class UiState(
        val isLoading: Boolean = false,
    )

    private fun purchase(productId: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            updateUiState { state -> state.copy(isLoading = true) }
            purchaseProduct(productId)
        }.invokeOnCompletion { asyncUpdateUiState(viewModelScope) { state -> state.copy(isLoading = false) } }
    }
}