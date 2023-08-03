package com.surajrathod.bluetoothlowenergy

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.surajrathod.bluetoothlowenergy.databinding.ItemDevicesLayoutBinding

class DeviceAdapter(val list: MutableList<ScanResult>, val onClick: (BluetoothDevice) -> Unit = {}) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>(){

    class DeviceViewHolder(val binding: ItemDevicesLayoutBinding) : ViewHolder(binding.root){
        val txtDeviceName = binding.txtDeviceName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(
            ItemDevicesLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val d = list[position]
        holder.txtDeviceName.text = d.device.name
    }
}