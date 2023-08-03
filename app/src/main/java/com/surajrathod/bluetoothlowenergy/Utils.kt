package com.surajrathod.bluetoothlowenergy

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat

object Utils {

    const val REQUEST_ENABLE_BT = 101
    const val BLUETOOTH_PERMISSION_REQUEST_CODE = 102

    fun makeToast(context: Context, msg : String, long : Boolean = false){
        if(long){
            Toast.makeText(context,msg,Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context,msg,Toast.LENGTH_SHORT).show()
        }
    }

    fun checkBluetooth(bluetoothAdapter: BluetoothAdapter?) : Boolean{
        return !(bluetoothAdapter==null || !bluetoothAdapter.isEnabled)
    }

    fun requestUserBluetooth(activity: Activity){
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestBluetoothPermission(activity)
            activity.startActivityForResult(intent, REQUEST_ENABLE_BT)
            return
        }

    }

   fun requestBluetoothPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            ),
            BLUETOOTH_PERMISSION_REQUEST_CODE
        )
    }
}