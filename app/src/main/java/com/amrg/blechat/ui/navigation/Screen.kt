package com.amrg.blechat.ui.navigation

sealed class Screen(val route: String) {
    data object PermissionsScreen : Screen("/permissions_screen")
    data object ScanScreen : Screen("/scan_screen")
    data object ChatScreen : Screen("/chat_screen")
}