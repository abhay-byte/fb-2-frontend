package com.ivarna.finalbenchmark2.ui.viewmodels

import android.app.ActivityManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.finalbenchmark2.ui.components.MemoryDataPoint
import com.ivarna.finalbenchmark2.ui.components.MemoryStats
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class MemoryUtilizationViewModel : ViewModel() {
    
    private val _memoryHistory = MutableStateFlow<List<MemoryDataPoint>>(emptyList())
    val memoryHistory: StateFlow<List<MemoryDataPoint>> = _memoryHistory.asStateFlow()
    
    private val _currentMemoryStats = MutableStateFlow<MemoryStats?>(null)
    val currentMemoryStats: StateFlow<MemoryStats?> = _currentMemoryStats.asStateFlow()
    
    private var isMonitoringStarted = false
    
    fun initialize(context: Context) {
        if (!isMonitoringStarted) {
            isMonitoringStarted = true
            startMemoryMonitoring(context)
        }
    }
    
    private fun startMemoryMonitoring(context: Context) {
        viewModelScope.launch {
            while (true) {
                try {
                    // Get current memory stats
                    val memoryStats = getMemoryUsage(context)
                    _currentMemoryStats.value = memoryStats
                    
                    // Add to history
                    val now = System.currentTimeMillis()
                    val newHistory = _memoryHistory.value.toMutableList()
                    newHistory.add(MemoryDataPoint(now, memoryStats.usagePercent.toFloat()))
                    
                    // Remove data points older than 30 seconds
                    val cutoffTime = now - 30_000L
                    newHistory.removeAll { it.timestamp < cutoffTime }
                    
                    // Limit total points to prevent memory issues
                    if (newHistory.size > 60) {
                        newHistory.removeAt(0)
                    }
                    
                    _memoryHistory.value = newHistory
                } catch (e: Exception) {
                    // Handle any errors in memory monitoring gracefully
                    e.printStackTrace()
                }
                
                delay(1000) // Update every 1000ms (1 second)
            }
        }
    }
    
    private fun getMemoryUsage(context: Context): MemoryStats {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val total = memoryInfo.totalMem
        val available = memoryInfo.availMem
        val used = total - available
        val percent = ((used.toDouble() / total.toDouble()) * 100).toInt()

        return MemoryStats(used, total, percent.coerceIn(0, 100))
    }
}