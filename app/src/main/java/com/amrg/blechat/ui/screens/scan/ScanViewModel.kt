package com.amrg.blechat.ui.screens.scan

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.ViewModel
import com.amrg.blechat.data.bluetooth.ChatServer
import com.amrg.blechat.data.bluetooth.scanner.BleScanner
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val bleScanner: BleScanner,
    private val chatServer: ChatServer
) : ViewModel() {

    init {
        startServer()
        startScan()
    }

    val scanState
        get() = bleScanner.scanState

    val deviceConnectionState
        get() = chatServer.deviceConnectionState

    fun startServer() = chatServer.start()

    fun stopServer() = chatServer.stop()

    fun startScan() = bleScanner.start()

    fun stopScan() = bleScanner.stop()

    fun connectToDevice(device: BluetoothDevice) = chatServer.setRemoteDevice(device)

    override fun onCleared() {
        super.onCleared()
        stopScan()
    }
}
