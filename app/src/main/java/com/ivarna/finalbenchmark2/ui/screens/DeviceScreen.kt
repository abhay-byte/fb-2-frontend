package com.ivarna.finalbenchmark2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.finalbenchmark2.ui.theme.FinalBenchmark2Theme
import com.ivarna.finalbenchmark2.utils.DeviceInfoCollector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen() {
    val context = LocalContext.current
    val deviceInfo = DeviceInfoCollector.getDeviceInfo(context)
    
    FinalBenchmark2Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                // Header
                Text(
                    text = "Device Information",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
                
                // Device Model and Manufacturer
                DeviceInfoCard("Device Information") {
                    InfoRow("Model", deviceInfo.deviceModel)
                    InfoRow("Manufacturer", deviceInfo.manufacturer)
                    InfoRow("Board", deviceInfo.board)
                    InfoRow("SoC", deviceInfo.socName)
                    InfoRow("Architecture", deviceInfo.cpuArchitecture)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // CPU Information
                DeviceInfoCard("CPU Information") {
                    InfoRow("Total Cores", deviceInfo.totalCores.toString())
                    InfoRow("Big Cores", deviceInfo.bigCores.toString())
                    InfoRow("Small Cores", deviceInfo.smallCores.toString())
                    InfoRow("Cluster Topology", deviceInfo.clusterTopology)
                    
                    // CPU Frequencies
                    Text(
                        text = "CPU Frequencies",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    deviceInfo.cpuFrequencies.forEach { (core, freq) ->
                        InfoRow("Core $core", freq)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // GPU Information
                DeviceInfoCard("GPU Information") {
                    InfoRow("Model", deviceInfo.gpuModel)
                    InfoRow("Vendor", deviceInfo.gpuVendor)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Memory Information
                DeviceInfoCard("Memory Information") {
                    InfoRow("Total RAM", formatBytes(deviceInfo.totalRam))
                    InfoRow("Available RAM", formatBytes(deviceInfo.availableRam))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Storage Information
                DeviceInfoCard("Storage Information") {
                    InfoRow("Total Storage", formatBytes(deviceInfo.totalStorage))
                    InfoRow("Free Storage", formatBytes(deviceInfo.freeStorage))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // System Information
                DeviceInfoCard("System Information") {
                    InfoRow("Android Version", "${deviceInfo.androidVersion} (API ${deviceInfo.apiLevel})")
                    InfoRow("Kernel Version", deviceInfo.kernelVersion)
                    InfoRow("Thermal Status", deviceInfo.thermalStatus ?: "Not available")
                    InfoRow("Battery Temperature", deviceInfo.batteryTemperature?.let { "${String.format("%.2f", it)}°C" } ?: "Not available")
                    InfoRow("Battery Capacity", deviceInfo.batteryCapacity?.let { "${it.toInt()}%" } ?: "Not available")
                }
            }
        }
    }
}

@Composable
fun DeviceInfoCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}