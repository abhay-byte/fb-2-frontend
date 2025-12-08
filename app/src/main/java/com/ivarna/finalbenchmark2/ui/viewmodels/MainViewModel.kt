package com.ivarna.finalbenchmark2.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ivarna.finalbenchmark2.cpuBenchmark.CpuTopologyDetector
import com.ivarna.finalbenchmark2.utils.RootUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Enum to represent different root states
enum class RootStatus {
    NO_ROOT,           // Device is not rooted
    ROOT_AVAILABLE,    // Device is rooted but commands don't work
    ROOT_WORKING       // Device is rooted and commands work
}

// Enum to represent performance optimization states
enum class PerformanceOptimizationStatus {
    NOT_SUPPORTED,     // Device doesn't support the optimization
    DISABLED,          // Optimization is available but disabled
    ENABLED,           // Optimization is active
    READY              // Optimization is initialized but not yet acquired (for wake lock)
}

data class PerformanceOptimizations(
    val sustainedPerformanceMode: PerformanceOptimizationStatus = PerformanceOptimizationStatus.DISABLED,
    val wakeLockStatus: PerformanceOptimizationStatus = PerformanceOptimizationStatus.ENABLED, // Ready state
    val screenAlwaysOnStatus: PerformanceOptimizationStatus = PerformanceOptimizationStatus.ENABLED,
    val highPriorityThreading: PerformanceOptimizationStatus = PerformanceOptimizationStatus.DISABLED,
    val performanceHintApi: PerformanceOptimizationStatus = PerformanceOptimizationStatus.DISABLED,
    val cpuAffinityControl: PerformanceOptimizationStatus = PerformanceOptimizationStatus.DISABLED,
    val foregroundService: PerformanceOptimizationStatus = PerformanceOptimizationStatus.DISABLED,
    val cpuGovernorHints: PerformanceOptimizationStatus = PerformanceOptimizationStatus.DISABLED
)

// New data class for real-time optimization status
data class OptimizationStatus(
    val isSustainedPerformanceMode: Boolean = false,
    val isHighPriorityActive: Boolean = false, // Foreground Service Status
    val bigCoreCount: Int = 0,
    val littleCoreCount: Int = 0,
    val isAffinityEnabled: Boolean = false, // True if bigCoreCount > 0
    val activeOptimizationCount: Int = 0,
    val totalOptimizationCount: Int = 6
)

class MainViewModel : ViewModel() {
    
    private val _rootState = MutableStateFlow(RootStatus.NO_ROOT)
    val rootState: StateFlow<RootStatus> = _rootState.asStateFlow()
    
    private val _performanceOptimizations = MutableStateFlow(PerformanceOptimizations())
    val performanceOptimizations: StateFlow<PerformanceOptimizations> = _performanceOptimizations.asStateFlow()
    
    private val _optimizationStatus = MutableStateFlow(OptimizationStatus())
    val optimizationStatus: StateFlow<OptimizationStatus> = _optimizationStatus.asStateFlow()
    
    // CPU Topology Detector instance
    private val cpuTopologyDetector = CpuTopologyDetector()

    init {
        checkRootAccess()
        loadOptimizationStatus()
    }

    private fun checkRootAccess() {
        viewModelScope.launch(Dispatchers.IO) {
            // This runs only ONCE when the app starts
            Log.d("MainViewModel", "Starting root access check...")
            val isRoot = RootUtils.isDeviceRooted()
            Log.d("MainViewModel", "Device rooted check result: $isRoot")
            var canExecuteRoot = false
            if (isRoot) {
                Log.d("MainViewModel", "Checking if root commands work...")
                canExecuteRoot = RootUtils.canExecuteRootCommand()
                Log.d("MainViewModel", "Root command execution check result: $canExecuteRoot")
            } else {
                Log.d("MainViewModel", "Skipping root command check since device is not rooted")
            }
            
            val result = when {
                isRoot && canExecuteRoot -> RootStatus.ROOT_WORKING
                isRoot && !canExecuteRoot -> RootStatus.ROOT_AVAILABLE
                else -> RootStatus.NO_ROOT
            }
            
            Log.d("MainViewModel", "Final root access result: $result")
            _rootState.value = result
        }
    }

    /**
     * Load and update optimization status with real-time data
     */
    private fun loadOptimizationStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Detect CPU topology
                val cpuTopology = cpuTopologyDetector.detectCpuTopology()
                val bigCores = cpuTopology.filter { it.isBigCore && it.isOnline }
                val littleCores = cpuTopology.filter { !it.isBigCore && it.isOnline }
                
                val bigCoreCount = bigCores.size
                val littleCoreCount = littleCores.size
                val isAffinityEnabled = bigCoreCount > 0
                
                // Calculate active optimizations
                var activeCount = 0
                
                // For now, we'll simulate some optimization statuses
                // In a real implementation, these would come from actual service checks
                val isSustainedPerformanceMode = false // Would check actual sustained performance mode
                val isHighPriorityActive = false // Would check if foreground service is running
                
                // Count active optimizations
                if (isSustainedPerformanceMode) activeCount++
                if (isHighPriorityActive) activeCount++
                if (isAffinityEnabled) activeCount++ // CPU Affinity is active if we have big cores
                // Add more as we implement real checks
                
                val optimizationStatus = OptimizationStatus(
                    isSustainedPerformanceMode = isSustainedPerformanceMode,
                    isHighPriorityActive = isHighPriorityActive,
                    bigCoreCount = bigCoreCount,
                    littleCoreCount = littleCoreCount,
                    isAffinityEnabled = isAffinityEnabled,
                    activeOptimizationCount = activeCount,
                    totalOptimizationCount = 6
                )
                
                _optimizationStatus.value = optimizationStatus
                
                Log.d("MainViewModel", "Loaded optimization status: Big cores=$bigCoreCount, Little cores=$littleCoreCount, Active=$activeCount/6")
                
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading optimization status", e)
                // Set default values on error
                _optimizationStatus.value = OptimizationStatus()
            }
        }
    }
    
    fun updateSustainedPerformanceModeStatus(status: PerformanceOptimizationStatus) {
        _performanceOptimizations.value = _performanceOptimizations.value.copy(
            sustainedPerformanceMode = status
        )
    }
    
    fun updateWakeLockStatus(status: PerformanceOptimizationStatus) {
        _performanceOptimizations.value = _performanceOptimizations.value.copy(
            wakeLockStatus = status
        )
    }
    
    fun updateScreenAlwaysOnStatus(status: PerformanceOptimizationStatus) {
        _performanceOptimizations.value = _performanceOptimizations.value.copy(
            screenAlwaysOnStatus = status
        )
    }
    
    fun updateHighPriorityThreadingStatus(status: PerformanceOptimizationStatus) {
        _performanceOptimizations.value = _performanceOptimizations.value.copy(
            highPriorityThreading = status
        )
    }
    
    fun updatePerformanceHintApiStatus(status: PerformanceOptimizationStatus) {
        _performanceOptimizations.value = _performanceOptimizations.value.copy(
            performanceHintApi = status
        )
    }
    
    fun updateCpuAffinityControlStatus(status: PerformanceOptimizationStatus) {
        _performanceOptimizations.value = _performanceOptimizations.value.copy(
            cpuAffinityControl = status
        )
    }
    
    fun updateForegroundServiceStatus(status: PerformanceOptimizationStatus) {
        _performanceOptimizations.value = _performanceOptimizations.value.copy(
            foregroundService = status
        )
    }
    
    fun updateCpuGovernorHintsStatus(status: PerformanceOptimizationStatus) {
        _performanceOptimizations.value = _performanceOptimizations.value.copy(
            cpuGovernorHints = status
        )
    }
    
    fun acquireWakeLock() {
        // This will be handled by the MainActivity
    }
    
    fun releaseWakeLock() {
        // This will be handled by the MainActivity
    }
}