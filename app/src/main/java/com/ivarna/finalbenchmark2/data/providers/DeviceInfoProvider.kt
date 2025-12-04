package com.ivarna.finalbenchmark2.data.providers

import android.content.Context
import com.ivarna.finalbenchmark2.domain.model.ItemValue
import com.ivarna.finalbenchmark2.utils.DeviceInfoCollector

class DeviceInfoProvider {
    
    suspend fun getData(context: Context): List<ItemValue> {
        val deviceInfo = DeviceInfoCollector.getDeviceInfo(context)
        return buildList {
            // Device section
            add(ItemValue.Text("Device", ""))
            add(ItemValue.Text("Model", "${deviceInfo.manufacturer} ${deviceInfo.deviceModel}"))
            add(ItemValue.Text("Board", deviceInfo.board))
            add(ItemValue.Text("SoC", deviceInfo.socName))
            add(ItemValue.Text("Architecture", deviceInfo.cpuArchitecture))
            
            // CPU section
            add(ItemValue.Text("CPU", ""))
            add(ItemValue.Text("Total Cores", deviceInfo.totalCores.toString()))
            add(ItemValue.Text("Big Cores", deviceInfo.bigCores.toString()))
            add(ItemValue.Text("Small Cores", deviceInfo.smallCores.toString()))
            add(ItemValue.Text("Cluster Topology", deviceInfo.clusterTopology))
            
            // Add CPU frequencies
            deviceInfo.cpuFrequencies.forEach { (core, freq) ->
                add(ItemValue.Text("Core ${core} Frequency", freq))
            }
            
            // GPU section
            add(ItemValue.Text("GPU", ""))
            add(ItemValue.Text("Model", deviceInfo.gpuModel))
            add(ItemValue.Text("Vendor", deviceInfo.gpuVendor))
            
            // Memory section
            add(ItemValue.Text("Memory", ""))
            add(ItemValue.Text("Total RAM", formatBytes(deviceInfo.totalRam)))
            add(ItemValue.Text("Available RAM", formatBytes(deviceInfo.availableRam)))
            
            // Storage section
            add(ItemValue.Text("Storage", ""))
            add(ItemValue.Text("Total Storage", formatBytes(deviceInfo.totalStorage)))
            add(ItemValue.Text("Free Storage", formatBytes(deviceInfo.freeStorage)))
            
            // System section
            add(ItemValue.Text("System", ""))
            add(ItemValue.Text("Android Version", "${deviceInfo.androidVersion} (API ${deviceInfo.apiLevel})"))
            add(ItemValue.Text("Kernel Version", deviceInfo.kernelVersion))
            
            // Battery section (if available)
            if (deviceInfo.batteryTemperature != null) {
                add(ItemValue.Text("Battery", ""))
                add(ItemValue.Text("Temperature", "${deviceInfo.batteryTemperature}°C"))
                if (deviceInfo.batteryCapacity != null) {
                    add(ItemValue.Text("Capacity", "${deviceInfo.batteryCapacity}%"))
                }
            }
        }
    }
    
    private fun formatBytes(bytes: Long): String {
        val unit = 1024
        if (bytes < unit) return "$bytes B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1] + "B"
        return String.format("%.1f %s", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }
}