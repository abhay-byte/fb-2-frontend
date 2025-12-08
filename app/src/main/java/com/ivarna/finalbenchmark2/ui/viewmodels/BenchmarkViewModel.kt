package com.ivarna.finalbenchmark2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.ivarna.finalbenchmark2.BenchmarkForegroundService
import com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkEvent
import com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkManager  // Keeping for compatibility
import com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkResult
import com.ivarna.finalbenchmark2.cpuBenchmark.CpuAffinityManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.withTimeout
import android.util.Log
import com.google.gson.Gson
import android.app.Application
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update // Required for thread-safe updates
import kotlinx.coroutines.Job
import android.app.ActivityManager

// Import SystemStats from SystemModels
import com.ivarna.finalbenchmark2.ui.models.SystemStats
import com.ivarna.finalbenchmark2.utils.CpuUtilizationUtils
import com.ivarna.finalbenchmark2.utils.PowerUtils
import com.ivarna.finalbenchmark2.utils.TemperatureUtils

// Test state tracking
data class TestState(
    val name: String,
    val status: TestStatus,
    val result: com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkResult? = null
)

enum class TestStatus {
    PENDING,
    RUNNING,
    COMPLETED
}

// Updated BenchmarkUiState to hold granular state
data class BenchmarkUiState(
    val currentTestName: String = "",
    val completedTests: List<com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkResult> = emptyList(),
    val progress: Float = 0f,
    val isSingleCoreFinished: Boolean = false,
    val systemStats: SystemStats = SystemStats(),
    val isRunning: Boolean = false,
    val benchmarkResults: BenchmarkResults? = null,
    val error: String? = null,
    val allTestStates: List<TestState> = emptyList()
)

// Old data class kept for compatibility
data class BenchmarkProgress(
    val currentBenchmark: String = "",
    val progress: Int = 0,
    val completedBenchmarks: Int = 0,
    val totalBenchmarks: Int = 0
)

data class BenchmarkResults(
    val individualScores: List<BenchmarkResult>,
    val singleCoreScore: Double,
    val multiCoreScore: Double,
    val coreRatio: Double,
    val finalWeightedScore: Double,
    val normalizedScore: Double,
    val detailedResults: List<BenchmarkResult> = emptyList() // Added for detailed view
)

// Keep the old BenchmarkState for compatibility if needed
sealed class BenchmarkState {
    object Idle : BenchmarkState()
    data class Running(val progress: BenchmarkProgress) : BenchmarkState()
    data class Completed(val results: BenchmarkResults) : BenchmarkState()
    data class Error(val message: String) : BenchmarkState()
}

class BenchmarkViewModel(
    private val historyRepository: com.ivarna.finalbenchmark2.data.repository.HistoryRepository? = null,
    private val application: Application
) : ViewModel() {
    private val _benchmarkState = MutableStateFlow<BenchmarkState>(BenchmarkState.Idle)
    val benchmarkState: StateFlow<BenchmarkState> = _benchmarkState
    
    // New state flow for granular benchmark UI state
    private val _uiState = MutableStateFlow(BenchmarkUiState())
    val uiState: StateFlow<BenchmarkUiState> = _uiState
    
    private val benchmarkManager = com.ivarna.finalbenchmark2.cpuBenchmark.KotlinBenchmarkManager()
    private val cpuUtils = CpuUtilizationUtils(application)
    private val powerUtils = PowerUtils(application)
    private val tempUtils = TemperatureUtils(application)
    
    // Job for system monitoring to prevent multiple instances
    private var monitorJob: Job? = null
    
    // Guard to prevent double-execution on screen rotation
    private var isBenchmarkRunning = false

    init {
        // Start the system monitoring loop
        startSystemMonitoring()
    }

    private fun startSystemMonitoring() {
        // Safety: Cancel any previous job to be 100% sure
        monitorJob?.cancel()
        
        val activityManager = application.getSystemService(ActivityManager::class.java)
        
        monitorJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) { // Changed from 'isActive' to 'true' for continuous monitoring
                // LOG 1: Monitor wakes up
                val currentSizeBefore = _uiState.value.completedTests.size
                Log.d("BENCH_DEBUG", "[Monitor] Waking up. Current List Size: $currentSizeBefore")

                // Calculate memory usage percentage
                val memoryLoad = try {
                    val memoryInfo = ActivityManager.MemoryInfo()
                    activityManager.getMemoryInfo(memoryInfo)
                    val totalMem = memoryInfo.totalMem
                    val availMem = memoryInfo.availMem
                    if (totalMem > 0) {
                        ((totalMem - availMem).toFloat() / totalMem.toFloat()) * 100f
                    } else {
                        0f
                    }
                } catch (e: Exception) {
                    Log.e("BenchmarkViewModel", "Error getting memory info: ${e.message}")
                    0f
                }

                val stats = SystemStats(
                    cpuLoad = cpuUtils.getCpuUtilizationPercentage(),
                    power = powerUtils.getPowerConsumptionInfo().power,
                    temp = tempUtils.getCpuTemperature(),
                    memoryLoad = memoryLoad
                )
                
                // CRITICAL MOMENT: Updating State
                _uiState.update { currentState ->
                    // LOG 2: Inside the atomic update block for Monitor
                    if (currentState.completedTests.size != currentSizeBefore) {
                        Log.d("BENCH_DEBUG", "[Monitor] !!! RACE CONDITION DETECTED !!! I saw size $currentSizeBefore, but inside update it is ${currentState.completedTests.size}")
                    }
                    currentState.copy(systemStats = stats)
                }
                
                delay(1000)
            }
        }
    }
    
    fun startBenchmark(preset: String = "Auto") {
        // FIX: Reset state immediately to prevent stale navigation
        _benchmarkState.value = BenchmarkState.Idle
        
        // Prevent restarting if already running or finished
        if (isBenchmarkRunning) return
        isBenchmarkRunning = true
        
        viewModelScope.launch {
            try {
                // Start foreground service to maintain high priority during benchmarks
                BenchmarkForegroundService.start(application)
                
                // Log CPU topology using the new CpuAffinityManager
                CpuAffinityManager.logTopology()
                val bigCores = CpuAffinityManager.getBigCores()
                val littleCores = CpuAffinityManager.getLittleCores()
                
                Log.i("BenchmarkViewModel", "Detected CPU topology: ${bigCores.size} big cores (${bigCores}), ${littleCores.size} little cores (${littleCores})")
                
                // With the pure Kotlin implementation, we rely on Android's thread priority system
                // instead of native CPU affinity control
                Log.i("BenchmarkViewModel", "Using Android thread priority for performance optimization")
                
                // Initialize the new UI state
                _uiState.update { currentState ->
                    BenchmarkUiState(
                        currentTestName = "Initializing...",
                        completedTests = emptyList(),
                        progress = 0f,
                        isSingleCoreFinished = false,
                        isRunning = true,
                        error = null,
                        allTestStates = emptyList() // Will be populated by the manager
                    )
                }
                
                _benchmarkState.value = BenchmarkState.Running(
                    BenchmarkProgress(
                        currentBenchmark = "Initializing...",
                        progress = 0,
                        completedBenchmarks = 0,
                        totalBenchmarks = 20 // 10 single-core + 10 multi-core
                    )
                )
                
                // Launch a coroutine to handle benchmark events
                val eventJob = launch {
                    benchmarkManager.benchmarkEvents.collect { event ->
                        when (event.state) {
                            "STARTED" -> {
                                _uiState.update { currentState ->
                                    val updatedTestStates = if (currentState.allTestStates.any { it.name == event.testName }) {
                                        currentState.allTestStates.map { state ->
                                            if (state.name == event.testName) state.copy(status = TestStatus.RUNNING) else state
                                        }
                                    } else {
                                        currentState.allTestStates + TestState(
                                            name = event.testName,
                                            status = TestStatus.RUNNING,
                                            result = null
                                        )
                                    }
                                    
                                    currentState.copy(
                                        currentTestName = event.testName,
                                        allTestStates = updatedTestStates
                                    )
                                }
                                
                                // Update the old benchmark state for compatibility
                                val currentUiState = _uiState.value
                                _benchmarkState.value = BenchmarkState.Running(
                                    BenchmarkProgress(
                                        currentBenchmark = event.testName,
                                        progress = (currentUiState.allTestStates.count { it.status == TestStatus.COMPLETED } * 100 / 20).coerceAtMost(100),
                                        completedBenchmarks = currentUiState.allTestStates.count { it.status == TestStatus.COMPLETED },
                                        totalBenchmarks = 20
                                    )
                                )
                            }
                            "COMPLETED" -> {
                                // The actual result is not passed in the event, so we track separately
                                _uiState.update { currentState ->
                                    val updatedTestStates = currentState.allTestStates.map { state ->
                                        if (state.name == event.testName && state.status == TestStatus.RUNNING) {
                                            state.copy(status = TestStatus.COMPLETED)
                                        } else {
                                            state
                                        }
                                    }
                                    
                                    currentState.copy(
                                        progress = (currentState.completedTests.size + 1).toFloat() / 20f,
                                        allTestStates = updatedTestStates
                                    )
                                }
                                
                                // Update the old benchmark state for compatibility
                                val currentUiState = _uiState.value
                                _benchmarkState.value = BenchmarkState.Running(
                                    BenchmarkProgress(
                                        currentBenchmark = event.testName,
                                        progress = ((currentUiState.allTestStates.count { it.status == TestStatus.COMPLETED } + 1) * 100 / 20).coerceAtMost(100),
                                        completedBenchmarks = currentUiState.allTestStates.count { it.status == TestStatus.COMPLETED },
                                        totalBenchmarks = 20
                                    )
                                )
                            }
                        }
                    }
                }
                
                // Wait for the benchmark completion with timeout
                // Create a channel to receive the result
                val resultChannel = kotlinx.coroutines.channels.Channel<String>()
                
                // Launch a coroutine to listen for benchmark completion
                val completionJob = launch {
                    benchmarkManager.benchmarkComplete.collect { summary ->
                        resultChannel.send(summary)
                        // Exit after sending the first result
                        return@collect
                    }
                }
                
                // Start the benchmark execution
                benchmarkManager.runAllBenchmarks("Flagship") // Using flagship mode for consistent results
                
                // Wait for the completion result with a timeout
                val summaryJson = try {
                    withTimeout(180000) { // 3 minute timeout
                        resultChannel.receive()
                    }
                } catch (e: Exception) {
                    Log.e("BenchmarkViewModel", "Timeout waiting for benchmark completion: ${e.message}")
                    // Return a default summary in case of timeout
                    """{
                        "single_core_score": 0.0,
                        "multi_core_score": 0.0,
                        "final_score": 0.0,
                        "normalized_score": 0.0,
                        "rating": "★"
                    }"""
                } finally {
                    resultChannel.close()
                    completionJob.cancel()
                }
                
                // Parse the summary JSON to extract scores
                var singleCoreScore = 0.0
                var multiCoreScore = 0.0
                var finalWeightedScore = 0.0
                var normalizedScore = 0.0
                var coreRatio = 0.0
                
                try {
                    val gson = Gson()
                    val summaryMap = gson.fromJson(summaryJson, Map::class.java)
                    singleCoreScore = (summaryMap["single_core_score"] as Double?) ?: 0.0
                    multiCoreScore = (summaryMap["multi_core_score"] as Double?) ?: 0.0
                    finalWeightedScore = (summaryMap["final_score"] as Double?) ?: 0.0
                    normalizedScore = (summaryMap["normalized_score"] as Double?) ?: 0.0
                    
                    coreRatio = if (singleCoreScore > 0) {
                        multiCoreScore / singleCoreScore
                    } else {
                        0.0
                    }
                    
                    Log.d("BenchmarkViewModel", "Using weighted scoring - Single: $singleCoreScore, Multi: $multiCoreScore")
                } catch (e: Exception) {
                    Log.e("BenchmarkViewModel", "Error parsing summary JSON: ${e.message}", e)
                    // Keep default values (already set to 0.0 above)
                }
                
                // For the detailed results, we need to get them from somewhere
                // Since the new manager doesn't expose individual results directly, we'll create dummy results
                val results = emptyList<com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkResult>()
                
                Log.d("BenchmarkViewModel", "Individual Results: $results")
                Log.d("BenchmarkViewModel", "Single-Core Score: $singleCoreScore")
                Log.d("BenchmarkViewModel", "Multi-Core Score: $multiCoreScore")
                Log.d("BenchmarkViewModel", "Core Ratio: $coreRatio")
                Log.d("BenchmarkViewModel", "Final Weighted Score: $finalWeightedScore")
                Log.d("BenchmarkViewModel", "Normalized Score: $normalizedScore")
                
                val benchmarkResults = BenchmarkResults(
                    individualScores = results,
                    singleCoreScore = singleCoreScore,
                    multiCoreScore = multiCoreScore,
                    coreRatio = coreRatio,
                    finalWeightedScore = finalWeightedScore,
                    normalizedScore = normalizedScore,
                    detailedResults = results
                )
                
                // Update UI state with final results
                _uiState.update { currentState ->
                    currentState.copy(
                        benchmarkResults = benchmarkResults,
                        isRunning = false
                    )
                }
                
                _benchmarkState.value = BenchmarkState.Completed(benchmarkResults)
                
                // Reset the running flag on completion
                isBenchmarkRunning = false
                
                // Stop foreground service after benchmarks complete
                BenchmarkForegroundService.stop(application)
                
                // Save the benchmark results to the database
                if (historyRepository != null) {
                    saveCpuBenchmarkResult(benchmarkResults)
                }
                
                // Cancel the event collection job
                eventJob.cancel()
                
            } catch (e: Exception) {
                Log.e("BenchmarkViewModel", "Error during benchmark execution", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        error = e.message ?: "Unknown error occurred",
                        isRunning = false
                    )
                }
                _benchmarkState.value = BenchmarkState.Error(e.message ?: "Unknown error occurred")
                
                // Stop foreground service in case of error
                BenchmarkForegroundService.stop(application)
                
                // Reset the running flag on error
                isBenchmarkRunning = false
            }
        }
    }
    
    fun saveCpuBenchmarkResult(results: BenchmarkResults) {
        if (historyRepository == null) {
            Log.w("BenchmarkViewModel", "HistoryRepository is null, cannot save results")
            return
        }
        
        viewModelScope.launch {
            try {
                // Serialize detailed results to JSON string
                val gson = Gson()
                val detailedResultsJson = gson.toJson(results.detailedResults)
                
                // Create the main benchmark result entity
                val benchmarkResultEntity = com.ivarna.finalbenchmark2.data.database.entities.BenchmarkResultEntity(
                    type = "CPU",  // Set the type as CPU
                    totalScore = results.finalWeightedScore,
                    timestamp = System.currentTimeMillis(),
                    deviceModel = android.os.Build.MODEL,
                    singleCoreScore = results.singleCoreScore,
                    multiCoreScore = results.multiCoreScore,
                    normalizedScore = results.normalizedScore, // Add normalized score
                    detailedResultsJson = detailedResultsJson // Add detailed results as JSON string
                )
                
                // Extract detailed scores for each benchmark type
                val singleCoreResults = results.detailedResults.filter { !it.name.contains("Multi") }
                val multiCoreResults = results.detailedResults.filter { it.name.contains("Multi") }
                
                // Calculate weighted scores for each benchmark type using BenchmarkManager logic
                val primeNumberScore = calculateWeightedScore(results.detailedResults, "Prime Generation")
                val fibonacciScore = calculateWeightedScore(results.detailedResults, "Fibonacci")
                val matrixMultiplicationScore = calculateWeightedScore(results.detailedResults, "Matrix Multiplication")
                val hashComputingScore = calculateWeightedScore(results.detailedResults, "Hash Computing")
                val stringSortingScore = calculateWeightedScore(results.detailedResults, "String Sorting")
                val rayTracingScore = calculateWeightedScore(results.detailedResults, "Ray Tracing")
                val compressionScore = calculateWeightedScore(results.detailedResults, "Compression")
                val monteCarloScore = calculateWeightedScore(results.detailedResults, "Monte Carlo")
                val jsonParsingScore = calculateWeightedScore(results.detailedResults, "JSON Parsing")
                val nQueensScore = calculateWeightedScore(results.detailedResults, "N-Queens")
                
                // Create the CPU test detail entity with actual scores
                val cpuTestDetailEntity = com.ivarna.finalbenchmark2.data.database.entities.CpuTestDetailEntity(
                    resultId = 0, // Will be set by the repository function
                    primeNumberScore = primeNumberScore,
                    fibonacciScore = fibonacciScore,
                    matrixMultiplicationScore = matrixMultiplicationScore,
                    hashComputingScore = hashComputingScore,
                    stringSortingScore = stringSortingScore,
                    rayTracingScore = rayTracingScore,
                    compressionScore = compressionScore,
                    monteCarloScore = monteCarloScore,
                    jsonParsingScore = jsonParsingScore,
                    nQueensScore = nQueensScore
                )
                
                // Save the benchmark result and details to the database
                historyRepository.saveCpuBenchmark(benchmarkResultEntity, cpuTestDetailEntity)
                
                Log.d("BenchmarkViewModel", "Successfully saved CPU benchmark result to database")
            } catch (e: Exception) {
                Log.e("BenchmarkViewModel", "Error saving benchmark result to database: ${e.message}", e)
            }
        }
    }
    
    private fun calculateWeightedScore(results: List<com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkResult>, benchmarkName: String): Double {
        // Use the same scaling factors as BenchmarkManager
        val scalingFactors = mapOf(
            "Prime Generation" to 0.00001,
            "Fibonacci" to 0.012,
            "Matrix Multiplication" to 0.025,
            "Hash Computing" to 0.01,
            "String Sorting" to 0.015,
            "Ray Tracing" to 0.006,
            "Compression" to 0.07,
            "Monte Carlo" to 0.07,
            "JSON Parsing" to 0.00004,
            "N-Queens" to 0.07
        )
        
        val filteredResults = results.filter { it.name.contains(benchmarkName) }
        if (filteredResults.isEmpty()) {
            return 0.0
        }
        
        // Calculate weighted score
        var totalWeightedScore = 0.0
        for (result in filteredResults) {
            val scalingFactor = scalingFactors[benchmarkName] ?: 0.0001
            totalWeightedScore += result.opsPerSecond * scalingFactor
        }
        
        return totalWeightedScore
    }
    
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // This retrieves the "Application" from the Android System
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                
                // Creates the ViewModel ensuring only ONE exists
                BenchmarkViewModel(application = application)
            }
        }
    }
}