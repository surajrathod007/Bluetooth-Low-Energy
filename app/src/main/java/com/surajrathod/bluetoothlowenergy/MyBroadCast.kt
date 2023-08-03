package com.surajrathod.bluetoothlowenergy

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyBroadCast(private val activityContext: Context) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        action?.let {
            if (it == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> {
                        Utils.makeToast(activityContext, "Bluetooth off")
                    }

                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        Utils.makeToast(activityContext, "Bluetooth turning off")
                    }

                    BluetoothAdapter.STATE_ON -> {
                        Utils.makeToast(activityContext, "Bluetooth on")
                    }

                    BluetoothAdapter.STATE_TURNING_ON -> {
                        Utils.makeToast(activityContext, "Bluetooth turning on")
                    }
                }
            }
        }
    }
}