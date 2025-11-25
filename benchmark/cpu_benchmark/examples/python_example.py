#!/usr/bin/env python3
"""
Python example for using the CPU benchmark FFI

This example demonstrates how to call the Rust CPU benchmark library from Python
using ctypes. The Rust library needs to be compiled as a dynamic library first.
"""

import ctypes
import json
import os
from ctypes import c_char_p, c_double, c_bool, c_void_p, c_size_t, POINTER

def main():
    # Load the Rust library
    # The library name varies by platform
    if os.name == 'nt':  # Windows
        lib_name = "cpu_benchmark.dll"
    elif os.name == 'posix':  # Unix-like (Linux, macOS)
        lib_name = "libcpu_benchmark.so"  # Linux
        # On macOS, it would be "libcpu_benchmark.dylib"
    
    # Try to find the library in common locations
    lib_path = None
    possible_paths = [
        f"./target/release/{lib_name}",
        f"./target/debug/{lib_name}",
        f"./{lib_name}",
    ]
    
    for path in possible_paths:
        if os.path.exists(path):
            lib_path = path
            break
    
    if not lib_path:
        print(f"Error: Could not find {lib_name}")
        print("Make sure to build the Rust library first with: cargo build --release")
        return
    
    print(f"Loading library: {lib_path}")
    lib = ctypes.CDLL(lib_path)
    
    # Define the function signatures
    # Function to run the complete benchmark suite
    lib.run_cpu_benchmark_suite.argtypes = [c_char_p]
    lib.run_cpu_benchmark_suite.restype = c_char_p
    
    # Function to free C strings
    lib.free_c_string.argtypes = [c_char_p]
    lib.free_c_string.restype = None
    
    # Create a configuration for the benchmark
    config = {
        "iterations": 3,
        "warmup": True,
        "warmup_count": 3,
        "device_tier": "Mid"  # Options: "Slow", "Mid", "Flagship"
    }
    
    # Run the complete benchmark suite
    config_json = json.dumps(config).encode('utf-8')
    result_ptr = lib.run_cpu_benchmark_suite(config_json)
    
    if result_ptr:
        result_str = ctypes.string_at(result_ptr).decode('utf-8')
        lib.free_c_string(result_ptr)  # Free the memory allocated by Rust
        
        print("Benchmark results:")
        print(json.dumps(json.loads(result_str), indent=2))
    else:
        print("Error: Failed to run benchmark suite")
    
    # Example of running individual benchmarks
    # Define FFI function signatures for individual benchmarks
    lib.run_single_core_prime_generation.argtypes = [c_char_p]
    lib.run_single_core_prime_generation.restype = c_void_p
    
    lib.run_single_core_fibonacci_recursive.argtypes = [c_char_p]
    lib.run_single_core_fibonacci_recursive.restype = c_void_p
    
    # Define the CBenchmarkResult structure in Python
    class CBenchmarkResult(ctypes.Structure):
        _fields_ = [
            ("name", c_char_p),
            ("execution_time_ms", c_double),
            ("ops_per_second", c_double),
            ("is_valid", c_bool),
            ("metrics_json", c_char_p),
        ]
    
    # Free function for CBenchmarkResult
    lib.free_benchmark_result.argtypes = [POINTER(CBenchmarkResult)]
    lib.free_benchmark_result.restype = None
    
    # Run a single-core prime generation benchmark with small parameters
    params = {
        "prime_range": 10000,  # Smaller range for testing
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
    }
    
    params_json = json.dumps(params).encode('utf-8')
    
    # Run single-core prime generation
    result_ptr = lib.run_single_core_prime_generation(params_json)
    if result_ptr:
        result = ctypes.cast(result_ptr, POINTER(CBenchmarkResult)).contents
        print(f"\nSingle-core prime generation result:")
        print(f"  Name: {result.name.decode('utf-8')}")
        print(f"  Execution time (ms): {result.execution_time_ms}")
        print(f"  Ops/sec: {result.ops_per_second}")
        print(f"  Valid: {result.is_valid}")
        print(f"  Metrics: {result.metrics_json.decode('utf-8')}")
        
        # Free the result
        lib.free_benchmark_result(result_ptr)
    else:
        print("Error: Failed to run single-core prime generation benchmark")
    
    # Run single-core Fibonacci
    result_ptr = lib.run_single_core_fibonacci_recursive(params_json)
    if result_ptr:
        result = ctypes.cast(result_ptr, POINTER(CBenchmarkResult)).contents
        print(f"\nSingle-core Fibonacci result:")
        print(f"  Name: {result.name.decode('utf-8')}")
        print(f"  Execution time (ms): {result.execution_time_ms}")
        print(f"  Ops/sec: {result.ops_per_second}")
        print(f"  Valid: {result.is_valid}")
        
        # Free the result
        lib.free_benchmark_result(result_ptr)
    else:
        print("Error: Failed to run single-core Fibonacci benchmark")

if __name__ == "__main__":
    main()