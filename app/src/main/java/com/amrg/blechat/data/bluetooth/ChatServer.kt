package com.amrg.blechat.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.os.Looper
import com.amrg.blechat.data.bluetooth.advertiser.BleAdvertiser
import com.amrg.blechat.data.bluetooth.mapper.toByteArray
import com.amrg.blechat.data.bluetooth.mapper.toMessage
import com.amrg.blechat.domain.DeviceConnectionState
import com.amrg.blechat.domain.Message
import com.amrg.blechat.utils.Constants.MESSAGE_UUID
import com.amrg.blechat.utils.Constants.SERVICE_UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import timber.log.Timber

@SuppressLint("MissingPermission")
class ChatServer(
    private val context: Context,
    private val bluetoothServiceManager: BluetoothServiceManager,
    private val bleAdvertiser: BleAdvertiser
) {

    companion object {
        private const val GATT_MAX_MTU_SIZE = 517
    }

    private var gattServer: BluetoothGattServer? = null
    private var gattClient: BluetoothGatt? = null

    private val _deviceConnectionState: MutableStateFlow<DeviceConnectionState> =
        MutableStateFlow(DeviceConnectionState.Disconnected)
    val deviceConnectionState = _deviceConnectionState.asStateFlow()

    val coroutineScope = CoroutineScope(Dispatchers.IO) + Job()

    private val _message: MutableSharedFlow<Message> = MutableSharedFlow()
    val message = _message.asSharedFlow()

    var currentConnectedDevice: BluetoothDevice? = null

    private var messageCharacteristic: BluetoothGattCharacteristic? = null

    fun start() {
        setupGattServer()
        bleAdvertiser.start()
    }

    fun stop() {
        _deviceConnectionState.value = DeviceConnectionState.Disconnected
        currentConnectedDevice = null
        gattClient = null
        gattServer = null
        bleAdvertiser.stop()
        coroutineScope.cancel()
    }

    fun setRemoteDevice(device: BluetoothDevice) {
        currentConnectedDevice = device
        _deviceConnectionState.value = DeviceConnectionState.Connected(device)
        connectDevice(device)
    }

    @Suppress("DEPRECATION")
    suspend fun sendMessage(message: Message) {
        messageCharacteristic?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gattClient?.writeCharacteristic(
                    it,
                    message.toByteArray(),
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                )
            } else {
                it.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
                it.value = message.toByteArray()
                gattClient?.writeCharacteristic(it)
            }
            _message.emit(message)
        }
    }

    private fun setupGattServer() {
        Timber.d("setupGattServer")
        gattServer = bluetoothServiceManager.bluetoothManager.openGattServer(
            context,
            gattServerCallback
        ).apply {
            addService(getGattService())
        }
    }

    private fun getGattService(): BluetoothGattService {
        val service = BluetoothGattService(SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)
        val messageCharacteristic = BluetoothGattCharacteristic(
            MESSAGE_UUID,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        service.addCharacteristic(messageCharacteristic)
        return service
    }

    private fun connectDevice(device: BluetoothDevice) {
        gattClient = device.connectGatt(context, false, gattClientCallback)
    }

    private val gattServerCallback: BluetoothGattServerCallback =
        object : BluetoothGattServerCallback() {
            override fun onConnectionStateChange(
                device: BluetoothDevice,
                status: Int,
                newState: Int
            ) {
                super.onConnectionStateChange(device, status, newState)
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    setRemoteDevice(device)
                } else {
                    _deviceConnectionState.value = DeviceConnectionState.Disconnected
                }
            }

            override fun onCharacteristicWriteRequest(
                device: BluetoothDevice?,
                requestId: Int,
                characteristic: BluetoothGattCharacteristic,
                preparedWrite: Boolean,
                responseNeeded: Boolean,
                offset: Int,
                value: ByteArray
            ) {
                super.onCharacteristicWriteRequest(
                    device,
                    requestId,
                    characteristic,
                    preparedWrite,
                    responseNeeded,
                    offset,
                    value
                )
                if (characteristic.uuid == MESSAGE_UUID) {
                    gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
                    val message = value.toMessage(false)
                    coroutineScope.launch { _message.emit(message) }
                }
            }
        }

    private val gattClientCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                gattClient = gatt
                gattClient?.apply {
                    requestMtu(GATT_MAX_MTU_SIZE)
                }
            } else {
                _deviceConnectionState.value = DeviceConnectionState.Disconnected
                gatt?.close()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gattClient = gatt
                val service = gatt.getService(SERVICE_UUID)
                if (service != null)
                    messageCharacteristic = service.getCharacteristic(MESSAGE_UUID)
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // it's highly recommend calling discoverServices() from the main/UI thread to prevent a rare threading issue from causing a deadlock situation where the app can be left waiting for the onServicesDiscovered() callback that somehow got dropped
                android.os.Handler(Looper.getMainLooper()).post {
                    gattClient?.discoverServices()
                }
            }
        }
    }
}