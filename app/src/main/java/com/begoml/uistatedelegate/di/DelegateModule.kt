package com.begoml.uistatedelegate.di

import com.begoml.core.PaymentHistoryRepository
import com.begoml.core.PaymentRepository
import com.begoml.core.UserRepository
import com.begoml.uistatedelegate.features.delegates.ToolbarDelegate
import com.begoml.uistatedelegate.features.delegates.ToolbarDelegateImpl
import com.begoml.uistatedelegate.features.delegates.payments.PaymentAnalytics
import com.begoml.uistatedelegate.features.delegates.payments.PaymentDelegate
import com.begoml.uistatedelegate.features.delegates.payments.PaymentDelegateImpl
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
class DelegateModule {

    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO) }

    @Singleton
    @Provides
    fun provideToolbarDelegate(
        userRepository: UserRepository,
    ): ToolbarDelegate = ToolbarDelegateImpl(
        coroutineScope = coroutineScope,
        userRepository = userRepository,
    )

    @Provides
    fun providePaymentDelegate(
        paymentAnalytics: PaymentAnalytics,
        paymentHistoryRepository: PaymentHistoryRepository,
        paymentRepository: PaymentRepository,
    ): PaymentDelegate = PaymentDelegateImpl(
        paymentAnalytics = paymentAnalytics,
        paymentHistoryRepository = paymentHistoryRepository,
        paymentRepository = paymentRepository,
    )
}
