package com.amrg.blechat.ui.screens.permissions

import androidx.lifecycle.ViewModel
import com.amrg.blechat.data.bluetooth.BluetoothServiceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor(
    private val bluetoothServiceManager: BluetoothServiceManager
) : ViewModel() {

    val isBluetoothEnabled
        get() = bluetoothServiceManager.isBluetoothEnabled

    val bluetoothEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
}
