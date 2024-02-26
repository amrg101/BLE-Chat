package com.amrg.blechat.data.bluetooth.di

import android.content.Context
import com.amrg.blechat.data.bluetooth.BluetoothServiceManager
import com.amrg.blechat.data.bluetooth.advertiser.BleAdvertiser
import com.amrg.blechat.data.bluetooth.scanner.BleScanner
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetoothManager(@ApplicationContext context: Context) =
        BluetoothServiceManager(context)

    @Provides
    @Singleton
    fun provideBleScanner(bluetoothServiceManager: BluetoothServiceManager) =
        BleScanner(bluetoothServiceManager)

    @Provides
    @Singleton
    fun provideBleAdvertiser(bluetoothServiceManager: BluetoothServiceManager) =
        BleAdvertiser(bluetoothServiceManager)
}