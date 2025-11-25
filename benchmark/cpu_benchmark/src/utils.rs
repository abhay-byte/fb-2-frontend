//! Utility functions for CPU benchmark operations

use std::time::{Duration, Instant};
use crate::types::{BenchmarkConfig, DeviceTier, WorkloadParams};

/// Get workload parameters based on device tier
pub fn get_workload_params(tier: &DeviceTier) -> WorkloadParams {
    match tier {
        DeviceTier::Slow => WorkloadParams {
            prime_range: 1_000_000,
            fibonacci_n_range: (30, 38),
            matrix_size: 500,
            hash_data_size_mb: 25,
            string_count: 250_000,
            ray_tracing_resolution: (256, 256),
            ray_tracing_depth: 2,
            compression_data_size_mb: 25,
            monte_carlo_samples: 25_000_000,
            json_data_size_mb: 2,
            nqueens_size: 12,
        },
        DeviceTier::Mid => WorkloadParams {
            prime_range: 6_000_000, // Reduced from 10_000_000
            fibonacci_n_range: (32, 38),  // Reduced from (35, 42)
            matrix_size: 600,  // Reduced from 800
            hash_data_size_mb: 40,  // Reduced from 60
            string_count: 500_000,  // Reduced from 750_000
            ray_tracing_resolution: (300, 300),  // Reduced from (400, 400) - fixed typo
            ray_tracing_depth: 3,  // Same
            compression_data_size_mb: 25,  // Reduced from 40
            monte_carlo_samples: 40_000_000,  // Reduced from 60_000_000
            json_data_size_mb: 4, // Reduced from 6
            nqueens_size: 13,  // Reduced from 14
        },
        DeviceTier::Flagship => WorkloadParams {
            prime_range: 12_000_000,  // Reduced from 20_000_000
            fibonacci_n_range: (35, 40),  // Reduced from (40, 47) to (35, 40)
            matrix_size: 900,  // Reduced from 1200
            hash_data_size_mb: 100,  // Reduced from 150
            string_count: 1_000_000,  // Reduced from 1_500_000
            ray_tracing_resolution: (450, 450),  // Reduced from (600, 600)
            ray_tracing_depth: 4,  // Reduced from 5
            compression_data_size_mb: 50,  // Reduced from 75
            monte_carlo_samples: 80_000_000,  // Reduced from 150_000_000 (was incorrectly set to 150_000)
            json_data_size_mb: 10,  // Reduced from 15
            nqueens_size: 15,  // Reduced from 16
        },
    }
}

/// Run a benchmark function and return execution time
pub fn run_benchmark<F>(mut f: F) -> Duration 
where 
    F: FnMut(),
{
    let start = Instant::now();
    f();
    start.elapsed()
}

/// Run a benchmark function multiple times and return average execution time
pub fn run_benchmark_multiple<F>(f: F, iterations: usize) -> Duration 
where 
    F: FnMut() -> (),
{
    let mut total_duration = Duration::new(0, 0);
    let mut f = f;
    
    for _ in 0..iterations {
        let start = Instant::now();
        f();
        total_duration += start.elapsed();
    }
    
    Duration::from_nanos(total_duration.as_nanos() as u64 / iterations as u64)
}

/// Calculate operations per second based on execution time and operation count
pub fn calculate_ops_per_second(operation_count: u64, execution_time: Duration) -> f64 {
    if execution_time.is_zero() {
        return f64::INFINITY;
    }
    
    let execution_time_secs = execution_time.as_secs_f64();
    operation_count as f64 / execution_time_secs
}

/// Verify that a number is prime
pub fn is_prime(n: u64) -> bool {
    if n <= 1 {
        return false;
    }
    if n <= 3 {
        return true;
    }
    if n % 2 == 0 || n % 3 == 0 {
        return false;
    }
    
    let mut i = 5;
    while i * i <= n {
        if n % i == 0 || n % (i + 2) == 0 {
            return false;
        }
        i += 6;
    }
    
    true
}

/// Generate random string of specified length
pub fn generate_random_string(length: usize) -> String {
    use rand::Rng;
    const CHARSET: &[u8] = b"ABCDEFGHIJKLMNOPQRSTUVWXYZ\
                            abcdefghijklmnopqrstuvwxyz\
                            0123456789";
    
    let mut rng = rand::thread_rng();
    (0..length)
        .map(|_| {
            let idx = rng.gen_range(0..CHARSET.len());
            CHARSET[idx] as char
        })
        .collect()
}

/// Validate benchmark config and adjust if needed
pub fn validate_config(config: &mut BenchmarkConfig) {
    if config.iterations == 0 {
        config.iterations = 3;
    }
    
    if config.warmup_count == 0 {
        config.warmup_count = 3;
    }
}