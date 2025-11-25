//! FFI tests for CPU benchmark functions
//!
//! These tests verify that the FFI functions work correctly and can be called
//! from external code, simulating how Kotlin and Python would interact with
//! the Rust library.

use cpu_benchmark::ffi::*;
use cpu_benchmark::types::WorkloadParams;
use std::ffi::CString;
use std::os::raw::c_char;

#[test]
fn test_create_default_config() {
    let config = unsafe { create_default_config() };
    assert!(!config.is_null());
    
    let c_config = unsafe { &*config };
    assert_eq!(c_config.iterations, 3);
    assert_eq!(c_config.warmup, true);
    assert_eq!(c_config.warmup_count, 3);
    
    // Free the config
    unsafe { free_benchmark_config(config) };
}

#[test]
fn test_run_single_core_prime_generation() {
    // Create small workload parameters for testing
    let params = WorkloadParams {
        prime_range: 1000, // Small range for testing
        fibonacci_n_range: (10, 15),
        matrix_size: 10,
        hash_data_size_mb: 1,
        string_count: 100,
        ray_tracing_resolution: (64, 64),
        ray_tracing_depth: 2,
        compression_data_size_mb: 1,
        monte_carlo_samples: 10000,
        json_data_size_mb: 1,
        nqueens_size: 8,
    };
    
    let params_json = serde_json::to_string(&params).unwrap();
    let params_cstring = CString::new(params_json).unwrap();
    
    let result = unsafe { 
        run_single_core_prime_generation(params_cstring.as_ptr() as *const c_char) 
    };
    
    assert!(!result.is_null());
    
    let c_result = unsafe { &*result };
    assert!(!c_result.name.is_null());
    assert!(c_result.execution_time_ms >= 0.0);
    assert!(c_result.ops_per_second >= 0.0);
    
    // Free the result
    unsafe { free_benchmark_result(result) };
}

#[test]
fn test_run_multi_core_prime_generation() {
    // Create small workload parameters for testing
    let params = WorkloadParams {
        prime_range: 1000,  // Small range for testing
        fibonacci_n_range: (10, 15),
        matrix_size: 10,
        hash_data_size_mb: 1,
        string_count: 100,
        ray_tracing_resolution: (64, 64),
        ray_tracing_depth: 2,
        compression_data_size_mb: 1,
        monte_carlo_samples: 1000,
        json_data_size_mb: 1,
        nqueens_size: 8,
    };
    
    let params_json = serde_json::to_string(&params).unwrap();
    let params_cstring = CString::new(params_json).unwrap();
    
    let result = unsafe { 
        run_multi_core_prime_generation(params_cstring.as_ptr() as *const c_char) 
    };
    
    assert!(!result.is_null());
    
    let c_result = unsafe { &*result };
    assert!(!c_result.name.is_null());
    assert!(c_result.execution_time_ms >= 0.0);
    assert!(c_result.ops_per_second >= 0.0);
    
    // Free the result
    unsafe { free_benchmark_result(result) };
}

#[test]
fn test_run_single_core_fibonacci_recursive() {
    // Create small workload parameters for testing
    let params = WorkloadParams {
        prime_range: 1000,
        fibonacci_n_range: (10, 15),  // Small range for testing
        matrix_size: 10,
        hash_data_size_mb: 1,
        string_count: 100,
        ray_tracing_resolution: (64, 64),
        ray_tracing_depth: 2,
        compression_data_size_mb: 1,
        monte_carlo_samples: 10000,
        json_data_size_mb: 1,
        nqueens_size: 8,
    };
    
    let params_json = serde_json::to_string(&params).unwrap();
    let params_cstring = CString::new(params_json).unwrap();
    
    let result = unsafe { 
        run_single_core_fibonacci_recursive(params_cstring.as_ptr() as *const c_char) 
    };
    
    assert!(!result.is_null());
    
    let c_result = unsafe { &*result };
    assert!(!c_result.name.is_null());
    assert!(c_result.execution_time_ms >= 0.0);
    assert!(c_result.ops_per_second >= 0.0);
    
    // Free the result
    unsafe { free_benchmark_result(result) };
}

#[test]
fn test_run_multi_core_fibonacci_memoized() {
    // Create small workload parameters for testing
    let params = WorkloadParams {
        prime_range: 100,
        fibonacci_n_range: (10, 15),  // Small range for testing
        matrix_size: 10,
        hash_data_size_mb: 1,
        string_count: 100,
        ray_tracing_resolution: (64, 64),
        ray_tracing_depth: 2,
        compression_data_size_mb: 1,
        monte_carlo_samples: 10000,
        json_data_size_mb: 1,
        nqueens_size: 8,
    };
    
    let params_json = serde_json::to_string(&params).unwrap();
    let params_cstring = CString::new(params_json).unwrap();
    
    let result = unsafe { 
        run_multi_core_fibonacci_memoized(params_cstring.as_ptr() as *const c_char) 
    };
    
    assert!(!result.is_null());
    
    let c_result = unsafe { &*result };
    assert!(!c_result.name.is_null());
    assert!(c_result.execution_time_ms >= 0.0);
    assert!(c_result.ops_per_second >= 0.0);
    
    // Free the result
    unsafe { free_benchmark_result(result) };
}

#[test]
fn test_run_single_core_matrix_multiplication() {
    // Create small workload parameters for testing
    let params = WorkloadParams {
        prime_range: 1000,
        fibonacci_n_range: (10, 15),
        matrix_size: 10,  // Small matrix for testing
        hash_data_size_mb: 1,
        string_count: 100,
        ray_tracing_resolution: (64, 64),
        ray_tracing_depth: 2,
        compression_data_size_mb: 1,
        monte_carlo_samples: 100,
        json_data_size_mb: 1,
        nqueens_size: 8,
    };
    
    let params_json = serde_json::to_string(&params).unwrap();
    let params_cstring = CString::new(params_json).unwrap();
    
    let result = unsafe { 
        run_single_core_matrix_multiplication(params_cstring.as_ptr() as *const c_char) 
    };
    
    assert!(!result.is_null());
    
    let c_result = unsafe { &*result };
    assert!(!c_result.name.is_null());
    assert!(c_result.execution_time_ms >= 0.0);
    assert!(c_result.ops_per_second >= 0.0);
    
    // Free the result
    unsafe { free_benchmark_result(result) };
}

#[test]
fn test_run_multi_core_matrix_multiplication() {
    // Create small workload parameters for testing
    let params = WorkloadParams {
        prime_range: 1000,
        fibonacci_n_range: (10, 15),
        matrix_size: 10, // Small matrix for testing
        hash_data_size_mb: 1,
        string_count: 100,
        ray_tracing_resolution: (64, 64),
        ray_tracing_depth: 2,
        compression_data_size_mb: 1,
        monte_carlo_samples: 10000,
        json_data_size_mb: 1,
        nqueens_size: 8,
    };
    
    let params_json = serde_json::to_string(&params).unwrap();
    let params_cstring = CString::new(params_json).unwrap();
    
    let result = unsafe { 
        run_multi_core_matrix_multiplication(params_cstring.as_ptr() as *const c_char) 
    };
    
    assert!(!result.is_null());
    
    let c_result = unsafe { &*result };
    assert!(!c_result.name.is_null());
    assert!(c_result.execution_time_ms >= 0.0);
    assert!(c_result.ops_per_second >= 0.0);
    
    // Free the result
    unsafe { free_benchmark_result(result) };
}

#[test]
fn test_free_c_string() {
    let test_str = "Hello, World!";
    let cstring = CString::new(test_str).unwrap();
    
    // This test just ensures that the function exists and can be called
    // without crashing (which would indicate memory corruption)
    unsafe { 
        free_c_string(cstring.into_raw());
    }
}