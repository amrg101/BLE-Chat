package com.amrg.blechat.utils

import java.util.UUID

object Constants {

    val SERVICE_UUID: UUID by lazy { UUID.fromString("0000b81d-0000-1000-8000-00805f9b34fb") }

    val MESSAGE_UUID: UUID by lazy { UUID.fromString("7db3e235-3608-41f3-a03c-955fcbd2ea4b") }

    const val REQUEST_ENABLE_BT = 101
}