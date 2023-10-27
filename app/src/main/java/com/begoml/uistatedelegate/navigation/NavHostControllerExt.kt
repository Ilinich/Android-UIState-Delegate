package com.begoml.uistatedelegate.navigation

import androidx.navigation.NavController

fun NavController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }
