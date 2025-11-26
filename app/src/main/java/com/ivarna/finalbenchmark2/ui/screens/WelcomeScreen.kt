package com.ivarna.finalbenchmark2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun WelcomeScreen(
    onStartBenchmark: () -> Unit
) {
    val context = LocalContext.current
    val deviceInfo = remember { DeviceInfoCollector.getDeviceInfo(context) }
    
    FinalBenchmark2Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // App name
                Text(
                    text = "FinalBenchmark2",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Device Info Section
                DeviceInfoSection(deviceInfo = deviceInfo)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Description
                Text(
                    text = "A comprehensive CPU benchmarking application that tests your device's processing power with various computational tasks.",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                // Start Benchmark Button
                Button(
                    onClick = onStartBenchmark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Start Benchmark",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceInfoSection(deviceInfo: com.ivarna.finalbenchmark2.utils.DeviceInfo) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Header with expand/collapse functionality
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Device Specifications",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (expanded) androidx.compose.material.icons.Icons.Default.KeyboardArrowUp else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.size(24.dp)
                )
            }
            
            // Collapsible content
            AnimatedVisibility(
                visible = expanded,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    // Device Model and Manufacturer
                    InfoRow("Model", deviceInfo.deviceModel)
                    InfoRow("Manufacturer", deviceInfo.manufacturer)
                    InfoRow("Board", deviceInfo.board)
                    InfoRow("SoC", deviceInfo.socName)
                    InfoRow("Architecture", deviceInfo.cpuArchitecture)
                    
                    // CPU Information
                    Text(
                        text = "CPU Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
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
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                    deviceInfo.cpuFrequencies.forEach { (core, freq) ->
                        InfoRow("Core $core", freq)
                    }
                    
                    // GPU Information
                    Text(
                        text = "GPU Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                    InfoRow("Model", deviceInfo.gpuModel)
                    InfoRow("Vendor", deviceInfo.gpuVendor)
                    
                    // Memory Information
                    Text(
                        text = "Memory Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                    InfoRow("Total RAM", formatBytes(deviceInfo.totalRam))
                    InfoRow("Available RAM", formatBytes(deviceInfo.availableRam))
                    
                    // Storage Information
                    Text(
                        text = "Storage Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                    InfoRow("Total Storage", formatBytes(deviceInfo.totalStorage))
                    InfoRow("Free Storage", formatBytes(deviceInfo.freeStorage))
                    
                    // System Information
                    Text(
                        text = "System Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
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
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}

fun formatBytes(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    return if (unitIndex == 0) {
        "${size.toLong()} ${units[unitIndex]}"
    } else {
        "${String.format("%.2f", size)} ${units[unitIndex]}"
    }
}