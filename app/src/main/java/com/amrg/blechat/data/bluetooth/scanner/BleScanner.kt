package com.amrg.blechat.data.bluetooth.scanner

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.ParcelUuid
import com.amrg.blechat.data.bluetooth.BluetoothServiceManager
import com.amrg.blechat.domain.ScanState
import com.amrg.blechat.utils.Constants.SERVICE_UUID
import com.amrg.blechat.utils.after
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

@SuppressLint("MissingPermission")
class BleScanner(private val bluetoothServiceManager: BluetoothServiceManager) {

    companion object {
        const val SCANNING_PERIOD = 20000L
    }

    private val scanner: BluetoothLeScanner by lazy {
        bluetoothServiceManager.getBluetoothAdapter.bluetoothLeScanner
    }
    private var scanSettings: ScanSettings = buildScanSettings()
    private var scanFilter: List<ScanFilter> = buildScanFilter()

    val devices = mutableSetOf<BluetoothDevice>()

    private val _scanState =
        MutableStateFlow<ScanState>(ScanState.NoResult)
    val scanState: StateFlow<ScanState>
        get() = _scanState.asStateFlow()

    private var isStarted = false

    fun start() {
        if (isStarted.not()) {
            _scanState.value = ScanState.Loading
            devices.clear()
            after(SCANNING_PERIOD) {
                if (devices.isEmpty()) _scanState.value = ScanState.NoResult
                stop()
            }
            scanner.startScan(scanFilter, scanSettings, deviceScanResultCallback)
            isStarted = true
        }
    }

    fun stop() {
        if (isStarted) {
            scanner.stopScan(deviceScanResultCallback)
            isStarted = false
        }
    }

    private val deviceScanResultCallback = object : ScanCallback() {
        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results?.forEach { scanResult ->
                devices.add(scanResult.device)
            }
            if (devices.isNotEmpty()) _scanState.value = ScanState.ScanResults(devices.toList())
            Timber.d("Devices detected: $devices")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            devices.add(result.device)
            if (devices.isNotEmpty()) _scanState.value = ScanState.ScanResults(devices.toList())
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.e("Scan has failed, error: $errorCode")
        }
    }

    private fun buildScanSettings() = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
        .build()

    private fun buildScanFilter(): List<ScanFilter> {
        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(SERVICE_UUID))
            .build()
        return listOf(scanFilter)
    }

}