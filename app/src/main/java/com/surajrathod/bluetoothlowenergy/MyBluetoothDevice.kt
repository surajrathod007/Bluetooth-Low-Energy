package com.surajrathod.bluetoothlowenergy

import android.bluetooth.BluetoothDevice

class MyBluetoothDevice(private val bluetoothDevice: BluetoothDevice, var rssi: Int) {

    fun getAddress() = bluetoothDevice.address
    fun getName() = bluetoothDevice.name

}