//! Integration tests for CPU benchmark algorithms

use cpu_benchmark::algorithms::*;
use cpu_benchmark::types::{BenchmarkResult, DeviceTier, WorkloadParams};

// Helper function to create small workload parameters for testing
fn small_workload_params() -> WorkloadParams {
    WorkloadParams {
        prime_range: 1000,  // Small range for testing
        fibonacci_n_range: (10, 15),  // Small Fibonacci range
        matrix_size: 10,  // Small matrix
        hash_data_size_mb: 1,  // 1MB for testing
        string_count: 100,  // Small string count
        ray_tracing_resolution: (64, 64),  // Small resolution
        ray_tracing_depth: 2,  // Small depth
        compression_data_size_mb: 1, // 1MB for testing
        monte_carlo_samples: 10000,  // Small sample count
        json_data_size_mb: 1,  // 1MB for testing
        nqueens_size: 8, // Small board size
    }
}

#[test]
fn test_single_core_prime_generation() {
    let params = small_workload_params();
    let result = single_core_prime_generation(&params);
    
    assert_eq!(result.name, "Single-Core Prime Generation");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core prime generation test passed: {:?}", result.metrics);
}

#[test]
fn test_single_core_fibonacci_recursive() {
    let params = small_workload_params();
    let result = single_core_fibonacci_recursive(&params);
    
    assert_eq!(result.name, "Single-Core Fibonacci Recursive");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core Fibonacci test passed: {:?}", result.metrics);
}

#[test]
fn test_single_core_matrix_multiplication() {
    let params = small_workload_params();
    let result = single_core_matrix_multiplication(&params);
    
    assert_eq!(result.name, "Single-Core Matrix Multiplication");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core matrix multiplication test passed: {:?}", result.metrics);
}

#[test]
fn test_single_core_hash_computing() {
    let params = small_workload_params();
    let result = single_core_hash_computing(&params);
    
    assert_eq!(result.name, "Single-Core Hash Computing");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core hash computing test passed: {:?}", result.metrics);
}

#[test]
fn test_single_core_string_sorting() {
    let params = small_workload_params();
    let result = single_core_string_sorting(&params);
    
    assert_eq!(result.name, "Single-Core String Sorting");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core string sorting test passed: {:?}", result.metrics);
}

#[test]
fn test_single_core_ray_tracing() {
    let params = small_workload_params();
    let result = single_core_ray_tracing(&params);
    
    assert_eq!(result.name, "Single-Core Ray Tracing");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core ray tracing test passed: {:?}", result.metrics);
}

#[test]
fn test_single_core_compression() {
    let params = small_workload_params();
    let result = single_core_compression(&params);
    
    assert_eq!(result.name, "Single-Core Compression");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core compression test passed: {:?}", result.metrics);
}

#[test]
fn test_single_core_monte_carlo_pi() {
    let params = small_workload_params();
    let result = single_core_monte_carlo_pi(&params);
    
    assert_eq!(result.name, "Single-Core Monte Carlo π");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core Monte Carlo π test passed: {:?}", result.metrics);
}

#[test]
fn test_single_core_json_parsing() {
    let params = small_workload_params();
    let result = single_core_json_parsing(&params);
    
    assert_eq!(result.name, "Single-Core JSON Parsing");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core JSON parsing test passed: {:?}", result.metrics);
}

#[test]
fn test_single_core_nqueens() {
    let params = small_workload_params();
    let result = single_core_nqueens(&params);
    
    assert_eq!(result.name, "Single-Core N-Queens");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Single-core N-Queens test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_prime_generation() {
    let params = small_workload_params();
    let result = multi_core_prime_generation(&params);
    
    assert_eq!(result.name, "Multi-Core Prime Generation");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core prime generation test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_fibonacci_memoized() {
    let params = small_workload_params();
    let result = multi_core_fibonacci_memoized(&params);
    
    assert_eq!(result.name, "Multi-Core Fibonacci Memoized");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core Fibonacci test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_matrix_multiplication() {
    let params = small_workload_params();
    let result = multi_core_matrix_multiplication(&params);
    
    assert_eq!(result.name, "Multi-Core Matrix Multiplication");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core matrix multiplication test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_hash_computing() {
    let params = small_workload_params();
    let result = multi_core_hash_computing(&params);
    
    assert_eq!(result.name, "Multi-Core Hash Computing");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core hash computing test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_string_sorting() {
    let params = small_workload_params();
    let result = multi_core_string_sorting(&params);
    
    assert_eq!(result.name, "Multi-Core String Sorting");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core string sorting test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_ray_tracing() {
    let params = small_workload_params();
    let result = multi_core_ray_tracing(&params);
    
    assert_eq!(result.name, "Multi-Core Ray Tracing");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core ray tracing test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_compression() {
    let params = small_workload_params();
    let result = multi_core_compression(&params);
    
    assert_eq!(result.name, "Multi-Core Compression");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core compression test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_monte_carlo_pi() {
    let params = small_workload_params();
    let result = multi_core_monte_carlo_pi(&params);
    
    assert_eq!(result.name, "Multi-Core Monte Carlo π");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core Monte Carlo π test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_json_parsing() {
    let params = small_workload_params();
    let result = multi_core_json_parsing(&params);
    
    assert_eq!(result.name, "Multi-Core JSON Parsing");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core JSON parsing test passed: {:?}", result.metrics);
}

#[test]
fn test_multi_core_nqueens() {
    let params = small_workload_params();
    let result = multi_core_nqueens(&params);
    
    assert_eq!(result.name, "Multi-Core N-Queens");
    assert!(result.execution_time.as_nanos() > 0);
    assert!(result.ops_per_second >= 0.0);
    assert!(result.is_valid);
    println!("Multi-core N-Queens test passed: {:?}", result.metrics);
}

#[test]
fn test_workload_params_correctness() {
    // Test that workload parameters are correctly set for different device tiers
    let slow_params = cpu_benchmark::utils::get_workload_params(&DeviceTier::Slow);
    let mid_params = cpu_benchmark::utils::get_workload_params(&DeviceTier::Mid);
    let flagship_params = cpu_benchmark::utils::get_workload_params(&DeviceTier::Flagship);
    
    // Verify that flagship has larger workloads than mid, which has larger than slow
    assert!(flagship_params.prime_range > mid_params.prime_range);
    assert!(mid_params.prime_range > slow_params.prime_range);
    
    assert!(flagship_params.matrix_size > mid_params.matrix_size);
    assert!(mid_params.matrix_size > slow_params.matrix_size);
    
    println!("Workload parameters test passed");
}