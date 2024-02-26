package com.amrg.blechat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amrg.blechat.ui.screens.chat.ChatScreen
import com.amrg.blechat.ui.screens.permissions.PermissionsScreen
import com.amrg.blechat.ui.screens.scan.ScanScreen

@Composable
fun NavGraph(startDestination: String, navController: NavHostController) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screen.PermissionsScreen.route) {
            PermissionsScreen(navController = navController)
        }
        composable(route = Screen.ScanScreen.route) {
            ScanScreen(navController = navController)
        }
        composable(route = Screen.ChatScreen.route) {
            ChatScreen(navController = navController)
        }
    }
}