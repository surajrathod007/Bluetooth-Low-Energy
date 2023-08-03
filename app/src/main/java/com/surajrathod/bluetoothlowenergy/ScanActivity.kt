package com.surajrathod.bluetoothlowenergy

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.surajrathod.bluetoothlowenergy.databinding.ActivityScanBinding

class ScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanBinding

    private val ENABLE_BLUETOOTH_REQUEST_CODE = 1
    private val RUNTIME_PERMISSION_REQUEST_CODE = 2

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private var isScanning = false
        set(value) {
            field = value
            binding.btnScanBLE.text = if (value) "Stop Scan" else "Start Scan"
        }


    private val scanResults = mutableListOf<ScanResult>()
    private val scanResultAdapter: DeviceAdapter by lazy {
        DeviceAdapter(scanResults) {

        }
    }

    override fun onResume() {
        super.onResume()
        if (!bluetoothAdapter.isEnabled) {
            promptEnableBluetooth()
        }
    }

    @SuppressLint("MissingPermission")
    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH_REQUEST_CODE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScanBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setupClickListeners()
        setupRv()
    }

    private fun setupRv() {
        binding.rvDevices.adapter = scanResultAdapter
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val indexQuery = scanResults.indexOfFirst { it.device.address == result.device.address }
            if (indexQuery != -1) { // A scan result already exists with the same address
                scanResults[indexQuery] = result
                scanResultAdapter.notifyItemChanged(indexQuery)
            } else {
                if(!result.device.name.isNullOrEmpty()){
                    scanResults.add(result)
                    scanResultAdapter.notifyItemInserted(scanResults.size - 1)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnScanBLE.setOnClickListener {
            if (isScanning) {
                stopBleScan()
            } else {
                startBleScan()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startBleScan() {
        if (!hasRequiredRuntimePermissions()) {
            requestRelevantRuntimePermissions()
        } else {
            scanResults.clear()
            scanResultAdapter.notifyDataSetChanged()
            bleScanner.startScan(null, scanSettings, scanCallback)
            isScanning = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopBleScan() {
        bleScanner.stopScan(scanCallback)
        isScanning = false
    }

    private fun Activity.requestRelevantRuntimePermissions() {
        if (hasRequiredRuntimePermissions()) {
            return
        }
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                requestLocationPermission()
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                requestBluetoothPermissions()
            }
        }
    }

    private fun requestLocationPermission() {

        val d = AlertDialog.Builder(this)
        d.apply {
            title = "Location permission required"
            setMessage(
                "Starting from Android M (6.0), the system requires apps to be granted " +
                        "location access in order to scan for BLE devices."
            )
            setCancelable(false)
            setPositiveButton("Ok") { d, w ->
                ActivityCompat.requestPermissions(
                    this@ScanActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    RUNTIME_PERMISSION_REQUEST_CODE
                )
            }
            show()
        }


    }

    private fun requestBluetoothPermissions() {

        val d = AlertDialog.Builder(this)
        d.apply {
            title = "Bluetooth permissions required"
            setMessage(
                "Starting from Android 12, the system requires apps to be granted " +
                        "Bluetooth access in order to scan for and connect to BLE devices."
            )
            setCancelable(false)
            setPositiveButton(android.R.string.ok) { d, w ->
                ActivityCompat.requestPermissions(
                    this@ScanActivity,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ),
                    RUNTIME_PERMISSION_REQUEST_CODE
                )
            }
            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ENABLE_BLUETOOTH_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    promptEnableBluetooth()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RUNTIME_PERMISSION_REQUEST_CODE -> {
                val containsPermanentDenial = permissions.zip(grantResults.toTypedArray()).any {
                    it.second == PackageManager.PERMISSION_DENIED &&
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, it.first)
                }
                val containsDenial = grantResults.any { it == PackageManager.PERMISSION_DENIED }
                val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                when {
                    containsPermanentDenial -> {
                        // TODO: Handle permanent denial (e.g., show AlertDialog with justification)
                        // Note: The user will need to navigate to App Settings and manually grant
                        // permissions that were permanently denied
                    }

                    containsDenial -> {
                        requestRelevantRuntimePermissions()
                    }

                    allGranted && hasRequiredRuntimePermissions() -> {
                        startBleScan()
                    }

                    else -> {
                        // Unexpected scenario encountered when handling permissions
                        recreate()
                    }
                }
            }
        }
    }

}