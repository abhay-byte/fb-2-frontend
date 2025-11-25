/*
 * Android Kotlin example for using the CPU benchmark FFI
 *
 * This example demonstrates how to call the Rust CPU benchmark library from Android Kotlin
 * using the C-compatible FFI functions. The Rust library needs to be compiled as a 
 * dynamic library for Android using the NDK.
 */

import android.util.Log
import java.nio.charset.StandardCharsets

class AndroidCpuBenchmark {
    companion object {
        // Load the native library that contains the Rust FFI functions
        // The library name should match the one specified in your Android build
        init {
            System.loadLibrary("cpu_benchmark")
        }
        
        // Declare the native functions that correspond to the Rust FFI functions
        @JvmStatic
        external fun runCpuBenchmarkSuite(configJson: String): String?
        
        @JvmStatic
        external fun runSingleCorePrimeGeneration(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCorePrimeGeneration(paramsJson: String): String?
        
        @JvmStatic
        external fun runSingleCoreFibonacciRecursive(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCoreFibonacciMemoized(paramsJson: String): String?
        
        @JvmStatic
        external fun runSingleCoreMatrixMultiplication(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCoreMatrixMultiplication(paramsJson: String): String?
        
        @JvmStatic
        external fun runSingleCoreHashComputing(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCoreHashComputing(paramsJson: String): String?
        
        @JvmStatic
        external fun runSingleCoreStringSorting(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCoreStringSorting(paramsJson: String): String?
        
        @JvmStatic
        external fun runSingleCoreRayTracing(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCoreRayTracing(paramsJson: String): String?
        
        @JvmStatic
        external fun runSingleCoreCompression(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCoreCompression(paramsJson: String): String?
        
        @JvmStatic
        external fun runSingleCoreMonteCarloPi(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCoreMonteCarloPi(paramsJson: String): String?
        
        @JvmStatic
        external fun runSingleCoreJsonParsing(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCoreJsonParsing(paramsJson: String): String?
        
        @JvmStatic
        external fun runSingleCoreNqueens(paramsJson: String): String?
        
        @JvmStatic
        external fun runMultiCoreNqueens(paramsJson: String): String?
        
        @JvmStatic
        external fun freeCString(str: String?)
    }
    
    /**
     * Run the complete CPU benchmark suite with the specified configuration
     */
    fun runBenchmarkSuite(config: BenchmarkConfig): String? {
        val configJson = """{
            "iterations": ${config.iterations},
            "warmup": ${config.warmup},
            "warmup_count": ${config.warmupCount},
            "device_tier": "${config.deviceTier}"
        }"""
        
        return try {
            runCpuBenchmarkSuite(configJson)
        } catch (e: UnsatisfiedLinkError) {
            Log.e("AndroidCpuBenchmark", "Native library not loaded", e)
            null
        } catch (e: Exception) {
            Log.e("AndroidCpuBenchmark", "Error running benchmark suite", e)
            null
        }
    }
    
    /**
     * Run a single-core prime generation benchmark
     */
    fun runSingleCorePrimeGeneration(params: WorkloadParams): BenchmarkResult? {
        val paramsJson = createParamsJson(params)
        val resultJson = runSingleCorePrimeGeneration(paramsJson)
        
        return if (resultJson != null) {
            parseBenchmarkResult(resultJson, "Single-Core Prime Generation")
        } else {
            null
        }
    }
    
    /**
     * Run a multi-core prime generation benchmark
     */
    fun runMultiCorePrimeGeneration(params: WorkloadParams): BenchmarkResult? {
        val paramsJson = createParamsJson(params)
        val resultJson = runMultiCorePrimeGeneration(paramsJson)
        
        return if (resultJson != null) {
            parseBenchmarkResult(resultJson, "Multi-Core Prime Generation")
        } else {
            null
        }
    }
    
    /**
     * Helper function to create parameters JSON
     */
    private fun createParamsJson(params: WorkloadParams): String {
        return """{
            "prime_range": ${params.primeRange},
            "fibonacci_n_range": [${params.fibonacciNRange.first}, ${params.fibonacciNRange.second}],
            "matrix_size": ${params.matrixSize},
            "hash_data_size_mb": ${params.hashDataSizeMb},
            "string_count": ${params.stringCount},
            "ray_tracing_resolution": [${params.rayTracingResolution.first}, ${params.rayTracingResolution.second}],
            "ray_tracing_depth": ${params.rayTracingDepth},
            "compression_data_size_mb": ${params.compressionDataSizeMb},
            "monte_carlo_samples": ${params.monteCarloSamples},
            "json_data_size_mb": ${params.jsonDataSizeMb},
            "nqueens_size": ${params.nqueensSize}
        }"""
    }
    
    /**
     * Helper function to parse benchmark result JSON
     * This is a simplified parser - in practice you'd use a JSON library
     */
    private fun parseBenchmarkResult(json: String, testName: String): BenchmarkResult {
        // This is a simplified approach - in practice you'd use a proper JSON parser
        // like org.json or Gson to parse the result
        return BenchmarkResult(
            name = testName,
            executionTimeMs = 0.0, // Extract from JSON in real implementation
            opsPerSecond = 0.0,    // Extract from JSON in real implementation
            isValid = true         // Extract from JSON in real implementation
        )
    }
    
    /**
     * Data class for benchmark configuration
     */
    data class BenchmarkConfig(
        val iterations: Int = 3,
        val warmup: Boolean = true,
        val warmupCount: Int = 3,
        val deviceTier: String = "Mid" // "Slow", "Mid", or "Flagship"
    )
    
    /**
     * Data class for workload parameters
     */
    data class WorkloadParams(
        val primeRange: Int = 10000,
        val fibonacciNRange: Pair<Int, Int> = Pair(10, 15),
        val matrixSize: Int = 50,
        val hashDataSizeMb: Int = 1,
        val stringCount: Int = 1000,
        val rayTracingResolution: Pair<Int, Int> = Pair(64, 64),
        val rayTracingDepth: Int = 2,
        val compressionDataSizeMb: Int = 1,
        val monteCarloSamples: Int = 10000,
        val jsonDataSizeMb: Int = 1,
        val nqueensSize: Int = 8
    )
    
    /**
     * Data class for benchmark results
     */
    data class BenchmarkResult(
        val name: String,
        val executionTimeMs: Double,
        val opsPerSecond: Double,
        val isValid: Boolean
    )
    
    /**
     * Example usage of the benchmark functions
     */
    fun exampleUsage() {
        // Create a configuration for the benchmark
        val config = BenchmarkConfig(
            iterations = 3,
            warmup = true,
            warmupCount = 3,
            deviceTier = "Mid"
        )
        
        // Run the complete benchmark suite
        val suiteResult = runBenchmarkSuite(config)
        if (suiteResult != null) {
            Log.d("AndroidCpuBenchmark", "Benchmark suite result: $suiteResult")
        } else {
            Log.e("AndroidCpuBenchmark", "Failed to run benchmark suite")
        }
        
        // Create parameters for individual benchmarks
        val params = WorkloadParams(
            primeRange = 10000,
            matrixSize = 100,
            stringCount = 5000,
            monteCarloSamples = 50000
        )
        
        // Run single-core prime generation
        val primeResult = runSingleCorePrimeGeneration(params)
        if (primeResult != null) {
            Log.d("AndroidCpuBenchmark", "Single-core prime generation result: $primeResult")
        } else {
            Log.e("AndroidCpuBenchmark", "Failed to run single-core prime generation")
        }
        
        // Run multi-core prime generation
        val multiPrimeResult = runMultiCorePrimeGeneration(params)
        if (multiPrimeResult != null) {
            Log.d("AndroidCpuBenchmark", "Multi-core prime generation result: $multiPrimeResult")
        } else {
            Log.e("AndroidCpuBenchmark", "Failed to run multi-core prime generation")
        }
    }
}