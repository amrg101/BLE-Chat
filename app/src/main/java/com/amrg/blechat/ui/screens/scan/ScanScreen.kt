package com.amrg.blechat.ui.screens.scan

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amrg.blechat.R
import com.amrg.blechat.domain.DeviceConnectionState
import com.amrg.blechat.domain.ScanState
import com.amrg.blechat.ui.navigation.Screen
import com.amrg.blechat.ui.theme.Gray400
import com.amrg.blechat.ui.theme.InterMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    navController: NavController,
    viewModel: ScanViewModel = hiltViewModel()
) {
    val deviceScanningState by viewModel.scanState.collectAsState()
    val deviceConnectionState by viewModel.deviceConnectionState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "BLE Devices Nearby")
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        ) { paddingValues ->
            when (deviceScanningState) {
                is ScanState.ScanResults -> {
                    Column(
                        Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        DevicesList(scanResults = (deviceScanningState as ScanState.ScanResults).devices) { device ->
                            device?.let { viewModel.connectToDevice(it) }
                        }
                        Spacer(modifier = Modifier.padding(8.dp))
                        ActionButtons(
                            startScan = { viewModel.startScan() },
                            stopScan = { viewModel.stopScan() })
                    }
                }

                is ScanState.Loading -> LoadingDevices()
                is ScanState.NoResult -> {
                    Column(
                        Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        NoDevices()
                        Spacer(modifier = Modifier.padding(8.dp))
                        ActionButtons(
                            startScan = { viewModel.startScan() },
                            stopScan = { viewModel.stopScan() })
                    }
                }

                else -> {}
            }
        }
    }

    DisposableEffect(deviceConnectionState) {
        if (deviceConnectionState is DeviceConnectionState.Connected) {
            navController.navigate(Screen.ChatScreen.route)
        }
        onDispose {
            viewModel.stopScan()
        }
    }
}


@Composable
fun LoadingDevices() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "Scanning for devices",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun NoDevices() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .size(120.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No devices",
                fontSize = 18.sp,
                fontFamily = InterMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ActionButtons(startScan: () -> Unit, stopScan: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = startScan) {
            Text("Start Scan")
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = stopScan) {
            Text("StopScan")
        }
    }
}

@Composable
fun DevicesList(
    scanResults: List<BluetoothDevice>,
    onClick: (BluetoothDevice?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
    ) {
        itemsIndexed(scanResults.toList()) { _, bluetoothDevice ->
            DeviceItem(device = bluetoothDevice) {
                it?.let(onClick)
                onClick(it)
            }
        }
    }
}

@Composable
@SuppressLint("MissingPermission")
fun DeviceItem(
    device: BluetoothDevice,
    onClick: (BluetoothDevice?) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(top = 5.dp, bottom = 5.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(164.dp),
        border = BorderStroke(1.dp, Gray400),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(Modifier.padding(8.dp)) {
            Icon(
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterVertically)
                    .size(36.dp),
                painter = painterResource(R.drawable.ic_bt_user),
                contentDescription = "bluetooth_user"
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = device.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
                Text(
                    text = device.address,
                    fontWeight = FontWeight.Light,
                    fontSize = 13.sp,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier

                    .align(Alignment.CenterVertically),
                onClick = { onClick(device) }) {
                Text(
                    text = "Connect",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
