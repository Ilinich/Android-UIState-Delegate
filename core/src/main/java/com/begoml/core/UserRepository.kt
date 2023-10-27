package com.begoml.core

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor() {

    fun getUserLevelFlow(): Flow<Float> = flow {
        emit(10.01f)

        delay(500)

        emit(55.1f)
    }
}