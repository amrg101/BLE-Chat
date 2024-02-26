package com.amrg.blechat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.amrg.blechat.ui.navigation.NavGraph
import com.amrg.blechat.ui.navigation.Screen
import com.amrg.blechat.ui.screens.permissions.PermissionsViewModel
import com.amrg.blechat.ui.screens.scan.ScanViewModel
import com.amrg.blechat.ui.theme.BTChatTheme
import com.amrg.blechat.utils.Constants.REQUEST_ENABLE_BT
import com.amrg.blechat.utils.PermissionManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val scanViewModel: ScanViewModel by viewModels()
    private val permissionViewModel: PermissionsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BTChatTheme {
                val navController = rememberNavController()
                val startDestination = if (PermissionManager.haveAllPermissions(this)
                    && permissionViewModel.isBluetoothEnabled
                ) {
                    Screen.ScanScreen.route
                } else Screen.PermissionsScreen.route

                Surface {
                    NavGraph(
                        startDestination = startDestination,
                        navController = navController
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                permissionViewModel.bluetoothEnabled.value = true
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanViewModel.apply {
            stopScan()
            stopServer()
        }
    }
}
