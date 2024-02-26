package com.amrg.blechat.data.bluetooth.advertiser

import android.annotation.SuppressLint
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.ParcelUuid
import com.amrg.blechat.MainApplication.Companion.appContext
import com.amrg.blechat.data.bluetooth.BluetoothServiceManager
import com.amrg.blechat.utils.Constants.SERVICE_UUID
import com.amrg.blechat.utils.after
import com.amrg.blechat.utils.killApp
import com.amrg.blechat.utils.showMessage
import timber.log.Timber
import javax.inject.Inject

@SuppressLint("MissingPermission")
class BleAdvertiser @Inject constructor(
    private val bluetoothServiceManager: BluetoothServiceManager
) {

    private val advertiser: BluetoothLeAdvertiser by lazy {
        bluetoothServiceManager.getBluetoothAdapter.bluetoothLeAdvertiser
    }
    private var advertiseSettings: AdvertiseSettings = buildAdvertiseSettings()
    private var advertiseData: AdvertiseData = buildAdvertiseData()

    private var isStarted = false

    fun start() {
        if (!bluetoothServiceManager.getBluetoothAdapter.isMultipleAdvertisementSupported) {
            appContext.showMessage("LE Advertising is not supported on this device")
            after(2000) { killApp() }
            return
        }

        if (isStarted.not()) {
            advertiser.startAdvertising(advertiseSettings, advertiseData, deviceAdvertiseCallback)
            isStarted = true
        }
    }

    fun stop() {
        if (isStarted) {
            advertiser.stopAdvertising(deviceAdvertiseCallback)
            isStarted = false
        }
    }

    private val deviceAdvertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            super.onStartSuccess(settingsInEffect)
            Timber.d("Device has started advertising successfully")
        }

        override fun onStartFailure(errorCode: Int) {
            super.onStartFailure(errorCode)
            Timber.e("Device has failed advertising, error: $errorCode")
        }
    }

    private fun buildAdvertiseSettings() = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
        .setTimeout(0)
        .build()

    private fun buildAdvertiseData() = AdvertiseData.Builder()
        .addServiceUuid(ParcelUuid(SERVICE_UUID))
        .setIncludeDeviceName(true)
        .build()
}