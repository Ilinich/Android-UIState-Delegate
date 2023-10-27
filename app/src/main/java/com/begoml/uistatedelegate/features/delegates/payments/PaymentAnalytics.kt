package com.begoml.uistatedelegate.features.delegates.payments

import javax.inject.Inject

class PaymentAnalytics @Inject constructor() {

    suspend fun trackStartPaymentEvent() {}

    suspend fun trackFinishPaymentEvent() {}
}