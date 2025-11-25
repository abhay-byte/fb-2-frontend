//! Type definitions for CPU benchmark operations

use serde::{Deserialize, Serialize};
use std::time::Duration;

/// Represents the result of a single benchmark test
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct BenchmarkResult {
    /// Name of the benchmark test
    pub name: String,
    /// Execution time for this test run
    pub execution_time: Duration,
    /// Operations per second achieved
    pub ops_per_second: f64,
    /// Correctness check - whether results were valid
    pub is_valid: bool,
    /// Additional metrics specific to the test
    pub metrics: serde_json::Value,
}

/// Configuration for benchmark execution
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct BenchmarkConfig {
    /// Number of iterations to run each test
    pub iterations: usize,
    /// Whether to run warmup iterations
    pub warmup: bool,
    /// Number of warmup iterations
    pub warmup_count: usize,
    /// Device tier (affects workload size)
    pub device_tier: DeviceTier,
}

/// Device tier for scaling workload appropriately
#[derive(Debug, Clone, Serialize, Deserialize)]
pub enum DeviceTier {
    /// Slow tier devices (entry-level smartphones, budget tablets)
    Slow,
    /// Mid tier devices (mid-range smartphones, mainstream tablets)
    Mid,
    /// Flagship tier devices (high-end smartphones, premium tablets)
    Flagship,
}

/// Represents the complete benchmark suite result
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct BenchmarkSuiteResult {
    /// Results for each individual test
    pub test_results: Vec<BenchmarkResult>,
    /// Total execution time for the suite
    pub total_time: Duration,
    /// Overall CPU score
    pub cpu_score: f64,
}

/// Represents an individual benchmark score
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct BenchmarkScore {
    /// Name of the benchmark
    pub name: String,
    /// Operations per second achieved
    pub ops_per_second: f64,
    /// Calculated score based on ops_per_second
    pub score: f64,
}

/// Workload parameters that scale based on device tier
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct WorkloadParams {
    // Prime number generation
    pub prime_range: usize,
    // Fibonacci sequence
    pub fibonacci_n_range: (u32, u32),
    // Matrix multiplication
    pub matrix_size: usize,
    // Hash computing
    pub hash_data_size_mb: usize,
    // String sorting
    pub string_count: usize,
    // Ray tracing
    pub ray_tracing_resolution: (u32, u32),
    pub ray_tracing_depth: u32,
    // Compression
    pub compression_data_size_mb: usize,
    // Monte Carlo
    pub monte_carlo_samples: usize,
    // JSON parsing
    pub json_data_size_mb: usize,
    // N-Queens
    pub nqueens_size: u32,
}