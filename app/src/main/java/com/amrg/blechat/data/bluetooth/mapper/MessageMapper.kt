package com.amrg.blechat.data.bluetooth.mapper

import com.amrg.blechat.domain.Message

fun Message.toByteArray(): ByteArray {
    return this.text.toByteArray(Charsets.UTF_8)
}

fun ByteArray.toMessage(isLocal: Boolean): Message {
    val text = this.toString(Charsets.UTF_8)
    return if (isLocal) Message.Local(text) else Message.Remote(text)
}