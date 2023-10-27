package com.begoml.uistatedelegate.features.delegates.payments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.begoml.core.PaymentHistoryRepository
import com.begoml.core.PaymentRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

interface PaymentDelegate {

    context(ViewModel)
    fun pay(productId: String)

    suspend fun purchaseProduct(productId: String)
}

class PaymentDelegateImpl @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val paymentHistoryRepository: PaymentHistoryRepository,
    private val paymentAnalytics: PaymentAnalytics,
) : PaymentDelegate {

    context(ViewModel)
    override fun pay(productId: String) {
        viewModelScope.launch {
            purchaseProduct(productId)
        }
    }

    override suspend fun purchaseProduct(productId: String) {
        paymentAnalytics.trackStartPaymentEvent()

        paymentRepository.pay(productId)

        paymentAnalytics.trackFinishPaymentEvent()

        paymentHistoryRepository.refresh()
    }
}