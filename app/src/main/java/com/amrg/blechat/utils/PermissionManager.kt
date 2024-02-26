package com.amrg.blechat.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object PermissionManager {
    val ALL_BLE_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }


    fun haveAllPermissions(context: Context) =
        ALL_BLE_PERMISSIONS
            .all { context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
}