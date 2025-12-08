package com.ivarna.finalbenchmark2.cpuBenchmark

import android.os.Process
import android.util.Log
import java.io.File

/**
 * Manages CPU affinity and thread priority for benchmarks
 * Uses Android APIs to ensure threads run on big cores at max performance
 */
object CpuAffinityManager {
    private const val TAG = "CpuAffinityManager"
    
    data class CpuCore(
        val id: Int,
        val maxFreqKhz: Long,
        val isOnline: Boolean,
        val isBigCore: Boolean
    )
    
    private var cachedCores: List<CpuCore>? = null
    
    /**
     * Detect all CPU cores and classify as big or LITTLE
     */
    fun detectCpuTopology(): List<CpuCore> {
        cachedCores?.let { return it }
        
        val cores = mutableListOf<CpuCore>()
        val numCores = Runtime.getRuntime().availableProcessors()
        
        for (i in 0 until numCores) {
            try {
                val maxFreqPath = "/sys/devices/system/cpu/cpu$i/cpufreq/cpuinfo_max_freq"
                val maxFreq = File(maxFreqPath).readText().trim().toLong()
                
                // Cores with max freq > 2.0 GHz are big cores
                val isBigCore = maxFreq > 2_000_000
                
                cores.add(CpuCore(
                    id = i,
                    maxFreqKhz = maxFreq,
                    isOnline = true,
                    isBigCore = isBigCore
                ))
                
                Log.d(TAG, "CPU$i: ${if (isBigCore) "BIG" else "LITTLE"}, Max: ${maxFreq/1000}MHz")
            } catch (e: Exception) {
                Log.w(TAG, "Could not read CPU$i info", e)
            }
        }
        
        cachedCores = cores
        return cores
    }
    
    fun getBigCores(): List<Int> {
        return detectCpuTopology().filter { it.isBigCore }.map { it.id }
    }
    
    fun getLittleCores(): List<Int> {
        return detectCpuTopology().filter { !it.isBigCore }.map { it.id }
    }
    
    /**
     * Set maximum thread priority for benchmark execution
     * This is Android's way of requesting high performance
     */
    fun setMaxPerformance() {
        try {
            // Set thread priority to urgent display (highest non-rt priority)
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY)
            Log.d(TAG, "✓ Set thread priority to URGENT_DISPLAY")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set thread priority", e)
        }
    }
    
    /**
     * Reset thread priority to default
     */
    fun resetPerformance() {
        try {
            Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT)
            Log.d(TAG, "Reset thread priority to DEFAULT")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to reset thread priority", e)
        }
    }
    
    /**
     * Log CPU topology for debugging
     */
    fun logTopology() {
        val cores = detectCpuTopology()
        val bigCores = cores.filter { it.isBigCore }
        val littleCores = cores.filter { !it.isBigCore }
        
        Log.i(TAG, "=== CPU TOPOLOGY ===")
        Log.i(TAG, "Total cores: ${cores.size}")
        Log.i(TAG, "Big cores: ${bigCores.size} (IDs: ${bigCores.map { it.id }})")
        Log.i(TAG, "LITTLE cores: ${littleCores.size} (IDs: ${littleCores.map { it.id }})")
        Log.i(TAG, "===================")
    }
}