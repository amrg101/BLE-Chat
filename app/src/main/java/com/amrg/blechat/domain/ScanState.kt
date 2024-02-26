package com.amrg.blechat.domain

import android.bluetooth.BluetoothDevice

sealed class ScanState {
    class ScanResults(val devices: List<BluetoothDevice>) : ScanState()
    class Error(val message: String) : ScanState()
    data object Loading : ScanState()
    data object NoResult : ScanState()
}