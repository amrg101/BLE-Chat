package com.amrg.blechat.ui.screens.permissions

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.amrg.blechat.ui.navigation.Screen
import com.amrg.blechat.utils.PermissionManager
import com.amrg.blechat.utils.PermissionManager.ALL_BLE_PERMISSIONS
import com.amrg.blechat.utils.enableBluetooth
import com.amrg.blechat.utils.showMessage

@Composable
fun PermissionsScreen(
    navController: NavController,
    viewModel: PermissionsViewModel = viewModel(LocalContext.current as ComponentActivity)
) {
    val context = LocalContext.current

    var isPermissionsGranted by remember {
        mutableStateOf(
            PermissionManager.haveAllPermissions(
                context
            )
        )
    }
    var isBluetoothEnabled by remember { mutableStateOf(viewModel.isBluetoothEnabled) }

    val bluetoothEnabled = viewModel.bluetoothEnabled.collectAsState()

    ActionButtons(isPermissionsGranted, isBluetoothEnabled) {
        isPermissionsGranted = true
    }
    if (bluetoothEnabled.value) isBluetoothEnabled = true

    LaunchedEffect(key1 = isPermissionsGranted, key2 = isBluetoothEnabled) {
        if (isPermissionsGranted && isBluetoothEnabled) {
            navController.navigate(Screen.ScanScreen.route) {
                popUpTo(Screen.PermissionsScreen.route) {
                    inclusive = true
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    isPermissionsGranted: Boolean,
    isBluetoothEnabled: Boolean,
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current as Activity

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { granted ->
        if (granted.values.all { it }) {
            onPermissionGranted()
        } else {
            context.showMessage("Please grant all permissions so you can use the app.")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isPermissionsGranted.not()) {
            Button(onClick = { launcher.launch(ALL_BLE_PERMISSIONS) }) {
                Text("Grant Permissions")
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
        if (isBluetoothEnabled.not()) {
            Button(onClick = { context.enableBluetooth() }) {
                Text("Enable Bluetooth")
            }
        }
    }
}