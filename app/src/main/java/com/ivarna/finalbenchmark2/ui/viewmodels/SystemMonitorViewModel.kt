package com.ivarna.finalbenchmark2.ui.viewmodels

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_SERVICES
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.finalbenchmark2.ui.components.ProcessItem
import com.ivarna.finalbenchmark2.ui.components.SystemInfoSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SystemMonitorViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(SystemInfoSummary())
    val uiState: StateFlow<SystemInfoSummary> = _uiState.asStateFlow()
    
    fun fetchSystemInfo(context: Context) {
        viewModelScope.launch {
            val summary = fetchSystemInfoInternal(context)
            _uiState.value = summary
        }
    }
    
    private suspend fun fetchSystemInfoInternal(context: Context): SystemInfoSummary {
        return withContext(Dispatchers.IO) {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val pm = context.packageManager
            
            // 1. Get Running Processes
            val runningProcesses = am.runningAppProcesses ?: emptyList()
            val pids = runningProcesses.map { it.pid }.toIntArray()
            
            // Get memory info for each process
            val memoryInfos = if (pids.isNotEmpty()) {
                am.getProcessMemoryInfo(pids)
            } else {
                emptyArray()
            }
            
            val processList = runningProcesses.mapIndexed { index, process ->
                val memInfo = if (index < memoryInfos.size) memoryInfos[index] else null
                val ramMb = if (memInfo != null) (memInfo.totalPss / 1024) else 0 // Total PSS is in KB, convert to MB
                
                ProcessItem(
                    name = process.processName,
                    pid = process.pid,
                    ramUsage = ramMb,
                    state = convertImportance(process.importance),
                    packageName = process.processName
                )
            }

            // 2. Get Totals (Requires QUERY_ALL_PACKAGES)
            var totalPackages = 0
            var totalServices = 0
            try {
                val allPackages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(GET_SERVICES.toLong()))
                } else {
                    @Suppress("DEPRECATION")
                    pm.getInstalledPackages(GET_SERVICES)
                }
                
                totalPackages = allPackages.size
                totalServices = allPackages.sumOf { it.services?.size ?: 0 }
            } catch (e: Exception) {
                // Handle permission issues gracefully
                e.printStackTrace()
            }
            
            SystemInfoSummary(
                runningProcesses = runningProcesses.size,
                totalPackages = totalPackages,
                totalServices = totalServices,
                processes = processList.sortedByDescending { it.ramUsage }
            )
        }
    }
    
    private fun convertImportance(importance: Int): String {
        return when (importance) {
            RunningAppProcessInfo.IMPORTANCE_FOREGROUND,
            RunningAppProcessInfo.IMPORTANCE_VISIBLE,
            RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE -> "Foreground"
            RunningAppProcessInfo.IMPORTANCE_SERVICE,
            RunningAppProcessInfo.IMPORTANCE_TOP_SLEEPING -> "Service"
            RunningAppProcessInfo.IMPORTANCE_BACKGROUND,
            RunningAppProcessInfo.IMPORTANCE_CACHED -> "Background"
            else -> "Unknown"
        }
    }
}