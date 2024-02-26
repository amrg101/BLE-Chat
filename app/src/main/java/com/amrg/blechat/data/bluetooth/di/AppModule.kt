package com.amrg.blechat.data.bluetooth.di

import android.content.Context
import com.amrg.blechat.data.bluetooth.BluetoothServiceManager
import com.amrg.blechat.data.bluetooth.ChatServer
import com.amrg.blechat.data.bluetooth.advertiser.BleAdvertiser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideChatServer(
        @ApplicationContext context: Context,
        bluetoothServiceManager: BluetoothServiceManager,
        bleAdvertiser: BleAdvertiser
    ) = ChatServer(context, bluetoothServiceManager, bleAdvertiser)
}