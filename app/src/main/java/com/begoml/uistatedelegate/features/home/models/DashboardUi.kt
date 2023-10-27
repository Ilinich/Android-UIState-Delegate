package com.begoml.uistatedelegate.features.home.models

import com.begoml.core.home.models.Dashboard

data class DashboardUi(
    val title: String,
    val description: String,
)

fun Dashboard.toDashboardUi() = DashboardUi(
    title = this.title,
    description = this.description,
)