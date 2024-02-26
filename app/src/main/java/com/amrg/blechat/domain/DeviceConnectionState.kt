package com.amrg.blechat.domain

import android.bluetooth.BluetoothDevice

sealed class DeviceConnectionState {
    class Connected(val device: BluetoothDevice) : DeviceConnectionState()
    data object Disconnected : DeviceConnectionState()
}