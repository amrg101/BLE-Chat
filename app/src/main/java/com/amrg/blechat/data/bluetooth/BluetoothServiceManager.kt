package com.amrg.blechat.data.bluetooth

import android.bluetooth.BluetoothManager
import android.content.Context

class BluetoothServiceManager(private val context: Context) {

    val bluetoothManager: BluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }

    private val bluetoothAdapter by lazy {
        bluetoothManager.adapter
    }

    val getBluetoothAdapter =
        bluetoothAdapter ?: throw RuntimeException("Bluetooth is not supported")

    val isBluetoothEnabled = getBluetoothAdapter.isEnabled
}