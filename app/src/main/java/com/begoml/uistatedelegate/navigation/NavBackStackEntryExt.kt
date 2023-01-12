package com.begoml.scopingstatescompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

@Composable
fun NavBackStackEntry.rememberBackStackEntry(
    navController: NavController,
    route: String,
): NavBackStackEntry {
    return remember(this) { navController.getBackStackEntry(route) }
}
