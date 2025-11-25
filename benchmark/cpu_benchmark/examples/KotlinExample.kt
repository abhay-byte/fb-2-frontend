/*
 * Kotlin example for using the CPU benchmark FFI
 *
 * This example demonstrates how to call the Rust CPU benchmark library from Kotlin
 * using JNI. The Rust library needs to be compiled as a dynamic library for Android.
 */

import java.lang.foreign.*
import java.lang.foreign.MemorySegment.NULL
import java.lang.foreign.ValueLayout.*
import java.nio.charset.StandardCharsets

class CpuBenchmarkKotlinExample {
    // Define the linker and library
    private val linker = Linker.nativeLinker()
    private val libPath = "cpu_benchmark" // Library name without 'lib' prefix and extension
    
    // Define the CBenchmarkResult structure
    private val benchmarkResultLayout = 
        MemoryLayout.structLayout(
            ValueLayout.ADDRESS.withName("name"),           // *mut c_char
            ValueLayout.JAVA_DOUBLE.withName("execution_time_ms"),  // f64
            ValueLayout.JAVA_DOUBLE.withName("ops_per_second"),     // f64
            ValueLayout.JAVA_BOOLEAN.withName("is_valid"),          // bool
            ValueLayout.ADDRESS.withName("metrics_json")    // *mut c_char
        ).withName("CBenchmarkResult")
    
    // Function to run the complete benchmark suite
    fun runBenchmarkSuite(configJson: String): String? {
        val runBenchmarkSuiteMH = linker.downcallHandle(
            linker.defaultLookup().find("$libPath.run_cpu_benchmark_suite").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS),
            Linker.Option.ofContinuationScheduling()
        )
        
        val configJsonSegment = Arena.global().allocate(configJson, StandardCharsets.UTF_8)
        val result = runBenchmarkSuiteMH.invoke(configJsonSegment) as MemorySegment
        
        if (result.address() == 0L) {
            return null
        }
        
        val resultStr = result.getString(0)
        
        // Free the memory allocated by Rust
        freeCString(result)
        
        return resultStr
    }
    
    // Function to run single-core prime generation
    fun runSingleCorePrimeGeneration(paramsJson: String): CBenchmarkResult? {
        val runSingleCorePrimeMH = linker.downcallHandle(
            linker.defaultLookup().find("$libPath.run_single_core_prime_generation").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS),
            Linker.Option.ofContinuationScheduling()
        )
        
        val paramsJsonSegment = Arena.global().allocate(paramsJson, StandardCharsets.UTF_8)
        val result = runSingleCorePrimeMH.invoke(paramsJsonSegment) as MemorySegment
        
        if (result.address() == 0L) {
            return null
        }
        
        val benchmarkResult = parseBenchmarkResult(result)
        
        // Free the result allocated by Rust
        freeBenchmarkResult(result)
        
        return benchmarkResult
    }
    
    // Function to run multi-core prime generation
    fun runMultiCorePrimeGeneration(paramsJson: String): CBenchmarkResult? {
        val runMultiCorePrimeMH = linker.downcallHandle(
            linker.defaultLookup().find("$libPath.run_multi_core_prime_generation").orElseThrow(),
            FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS),
            Linker.Option.ofContinuationScheduling()
        )
        
        val paramsJsonSegment = Arena.global().allocate(paramsJson, StandardCharsets.UTF_8)
        val result = runMultiCorePrimeMH.invoke(paramsJsonSegment) as MemorySegment
        
        if (result.address() == 0L) {
            return null
        }
        
        val benchmarkResult = parseBenchmarkResult(result)
        
        // Free the result allocated by Rust
        freeBenchmarkResult(result)
        
        return benchmarkResult
    }
    
    // Parse the CBenchmarkResult structure from memory
    private fun parseBenchmarkResult(result: MemorySegment): CBenchmarkResult {
        val structView = result.asSlice(0, benchmarkResultLayout.byteSize())
        
        val namePtr = structView.get(ValueLayout.ADDRESS, 0)
        val executionTimeMs = structView.get(JAVA_DOUBLE, 8)
        val opsPerSecond = structView.get(JAVA_DOUBLE, 16)
        val isValid = structView.get(JAVA_BOOLEAN, 24)
        val metricsJsonPtr = structView.get(ValueLayout.ADDRESS, 32)
        
        val name = if (namePtr.address() != 0L) namePtr.getString(0) else ""
        val metricsJson = if (metricsJsonPtr.address() != 0L) metricsJsonPtr.getString(0) else ""
        
        return CBenchmarkResult(
            name = name,
            executionTimeMs = executionTimeMs,
            opsPerSecond = opsPerSecond,
            isValid = isValid,
            metricsJson = metricsJson
        )
    }
    
    // Free a C string allocated by Rust
    private fun freeCString(str: MemorySegment) {
        val freeCStringMH = linker.downcallHandle(
            linker.defaultLookup().find("$libPath.free_c_string").orElseThrow(),
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
            Linker.Option.ofContinuationScheduling()
        )
        
        freeCStringMH.invoke(str)
    }
    
    // Free a CBenchmarkResult allocated by Rust
    private fun freeBenchmarkResult(result: MemorySegment) {
        val freeBenchmarkResultMH = linker.downcallHandle(
            linker.defaultLookup().find("$libPath.free_benchmark_result").orElseThrow(),
            FunctionDescriptor.ofVoid(ValueLayout.ADDRESS),
            Linker.Option.ofContinuationScheduling()
        )
        
        freeBenchmarkResultMH.invoke(result)
    }
    
    // Data class to hold benchmark results
    data class CBenchmarkResult(
        val name: String,
        val executionTimeMs: Double,
        val opsPerSecond: Double,
        val isValid: Boolean,
        val metricsJson: String
    )
    
    // Example usage
    fun exampleUsage() {
        // Configuration for the benchmark
        val configJson = """{
            "iterations": 3,
            "warmup": true,
            "warmup_count": 3,
            "device_tier": "Mid"
        }"""
        
        // Run the complete benchmark suite
        val suiteResult = runBenchmarkSuite(configJson)
        if (suiteResult != null) {
            println("Benchmark suite result: $suiteResult")
        } else {
            println("Failed to run benchmark suite")
        }
        
        // Run individual benchmarks with small parameters for testing
        val paramsJson = """{
            "prime_range": 10000,
            "fibonacci_n_range": [10, 15],
            "matrix_size": 50,
            "hash_data_size_mb": 1,
            "string_count": 1000,
            "ray_tracing_resolution": [64, 64],
            "ray_tracing_depth": 2,
            "compression_data_size_mb": 1,
            "monte_carlo_samples": 10000,
            "json_data_size_mb": 1,
            "nqueens_size": 8
        }"""
        
        // Run single-core prime generation
        val primeResult = runSingleCorePrimeGeneration(paramsJson)
        if (primeResult != null) {
            println("Single-core prime generation result:")
            println("  Name: ${primeResult.name}")
            println("  Execution time (ms): ${primeResult.executionTimeMs}")
            println("  Ops/sec: ${primeResult.opsPerSecond}")
            println("  Valid: ${primeResult.isValid}")
        } else {
            println("Failed to run single-core prime generation benchmark")
        }
        
        // Run multi-core prime generation
        val multiPrimeResult = runMultiCorePrimeGeneration(paramsJson)
        if (multiPrimeResult != null) {
            println("Multi-core prime generation result:")
            println("  Name: ${multiPrimeResult.name}")
            println("  Execution time (ms): ${multiPrimeResult.executionTimeMs}")
            println("  Ops/sec: ${multiPrimeResult.opsPerSecond}")
            println("  Valid: ${multiPrimeResult.isValid}")
        } else {
            println("Failed to run multi-core prime generation benchmark")
        }
    }
}

// Alternative example using JNI (for older Android versions)
class CpuBenchmarkJNIExample {
    // Load the native library
    companion object {
        System.loadLibrary("cpu_benchmark")
    }
    
    // Declare native methods
    external fun runCpuBenchmarkSuite(configJson: String): String?
    external fun runSingleCorePrimeGeneration(paramsJson: String): String?
    external fun runMultiCorePrimeGeneration(paramsJson: String): String?
    external fun freeCString(str: String?)
    
    // Example usage
    fun exampleUsage() {
        // Configuration for the benchmark
        val configJson = """{
            "iterations": 3,
            "warmup": true,
            "warmup_count": 3,
            "device_tier": "Mid"
        }"""
        
        // Run the complete benchmark suite
        val suiteResult = runCpuBenchmarkSuite(configJson)
        if (suiteResult != null) {
            println("Benchmark suite result: $suiteResult")
        } else {
            println("Failed to run benchmark suite")
        }
        
        // Run individual benchmarks with small parameters for testing
        val paramsJson = """{
            "prime_range": 1000,
            "fibonacci_n_range": [10, 15],
            "matrix_size": 50,
            "hash_data_size_mb": 1,
            "string_count": 1000,
            "ray_tracing_resolution": [64, 64],
            "ray_tracing_depth": 2,
            "compression_data_size_mb": 1,
            "monte_carlo_samples": 10000,
            "json_data_size_mb": 1,
            "nqueens_size": 8
        }"""
        
        // Run single-core prime generation
        val primeResult = runSingleCorePrimeGeneration(paramsJson)
        if (primeResult != null) {
            println("Single-core prime generation result: $primeResult")
        } else {
            println("Failed to run single-core prime generation benchmark")
        }
    }
}