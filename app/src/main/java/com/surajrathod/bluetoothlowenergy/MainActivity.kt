package com.surajrathod.bluetoothlowenergy

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private var devicesHashMap = mutableMapOf<String,MyBluetoothDevice>()
    private var list = mutableListOf<MyBluetoothDevice>()

    lateinit var myReciver : MyBroadCast
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myReciver = MyBroadCast(this)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(myReciver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(myReciver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Utils.REQUEST_ENABLE_BT){
            if(resultCode == RESULT_OK){
                Utils.makeToast(this,"Thank you for turning on bluetooth")
            }else if(resultCode == RESULT_CANCELED){
                Utils.makeToast(this,"Please turn on bluetooth")
            }
        }
    }
}