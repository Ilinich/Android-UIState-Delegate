package com.begoml.uistatedelegate.navigation

sealed class NavigationDestination(open val destination: String) {

    object Login : NavigationDestination("login")

    object Home : NavigationDestination("home")
}
