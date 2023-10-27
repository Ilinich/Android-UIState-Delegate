package com.begoml.core.home

import com.begoml.core.ApiService
import com.begoml.core.home.models.Dashboard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DashboardRepository @Inject constructor(
    provideApiService: ApiService,
) {

    suspend fun getHomeItems(): List<Dashboard> = withContext(Dispatchers.IO) {
        delay(500)

        buildList {
            repeat(50) {
                add(
                    Dashboard(
                        id = UUID.randomUUID().toString(),
                        title = UUID.randomUUID().toString(),
                        description = UUID.randomUUID().toString(),
                    )
                )
            }
        }
    }
}