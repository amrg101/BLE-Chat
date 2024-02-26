package com.amrg.blechat.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.amrg.blechat.utils.Constants.REQUEST_ENABLE_BT
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

fun after(delay: Long, action: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({ action() }, delay)
}

fun Context.showMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun killApp() {
    exitProcess(0)
}

@SuppressLint("MissingPermission")
fun Activity.enableBluetooth() {
    runCatching {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    }
}

fun getCurrentTime(): String {
    val rightNow = LocalDateTime.now()
    return rightNow.format(DateTimeFormatter.ofPattern("h:mm a"))
}