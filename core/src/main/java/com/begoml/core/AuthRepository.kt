package com.begoml.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    provideApiService: ApiService,
) {

    suspend fun login(login: String, password: String) {
        withContext(Dispatchers.IO) {
            delay(250)
        }
    }

    suspend fun forgotPassword(login: String) {
        withContext(Dispatchers.IO) {
            delay(250)
        }
    }
}
