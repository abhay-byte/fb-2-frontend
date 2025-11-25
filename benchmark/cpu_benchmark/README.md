# CPU Benchmark Core Engine

A high-performance CPU benchmarking library written in Rust with FFI bindings for cross-language integration.

## Overview

This library implements a comprehensive suite of CPU benchmarks designed to evaluate various aspects of processor performance across different architectures and platforms. It includes both single-core and multi-core tests that exercise different computational patterns and workloads.

## Features

- **Multiple Benchmark Algorithms**: Prime generation, Fibonacci, matrix multiplication, hash computing, string sorting, ray tracing, compression, Monte Carlo simulation, JSON parsing, and N-Queens
- **Cross-Language FFI**: Compatible with Kotlin (Android), Python, and other languages via C-compatible interfaces
- **Scalable Workloads**: Automatically adjusts to device tier (Slow/Mid/Flagship) for appropriate testing
- **Thread Models**: Single-core, multi-core, and saturation testing modes
- **Performance Metrics**: Execution time, operations per second, and correctness validation

## Installation

Add this to your `Cargo.toml`:

```toml
[dependencies]
cpu_benchmark = { path = "./path/to/cpu_benchmark" }
```

## How to Run

### Building the Project

To build the CPU benchmark library and executable:

```bash
# Build in debug mode
cargo build

# Build in release mode
cargo build --release
```

### Running Benchmarks

Execute the benchmark suite:

```bash
# Run with default settings (Mid tier)
cargo run

# Run for specific device tier
cargo run -- slow    # For slow tier devices
cargo run -- mid     # For mid tier devices (default)
cargo run -- flagship  # For flagship tier devices
```

### Running Tests

To run the comprehensive test suite:

```bash
# Run all tests
cargo test

# Run specific test
cargo test test_single_core_prime_generation

# Run FFI-specific tests
cargo test ffi_test
```

### Building for Different Targets

#### For x86_64 Linux:
```bash
cargo build --target x86_64-unknown-linux-gnu
```

#### For ARM Linux:
```bash
cargo build --target arm-unknown-linux-gnueabihf
```

#### For Android (ARM):
```bash
cargo build --target aarch64-linux-android
```

#### For Windows (x86_64):
```bash
cargo build --target x86_64-pc-windows-msvc
```

### Using the FFI Interface

The library provides C-compatible functions for integration with other languages:

```rust
use cpu_benchmark::ffi::*;

// Create a default configuration
let config = create_default_config();

// Run the complete benchmark suite
let config_json = r#"{
    "iterations": 3,
    "warmup": true,
    "warmup_count": 3,
    "device_tier": "Mid"
}"#;

let result = run_cpu_benchmark_suite(config_json.as_ptr() as *const c_char);
// Process result...

// Remember to free the allocated memory
free_c_string(result);
```

### Examples

See the `examples/` directory for usage examples in different languages:

- `python_example.py` - Python integration using ctypes
- `AndroidCpuBenchmark.kt` - Kotlin/Android integration example

## Architecture

The library is organized into several modules:

- `src/algorithms.rs` - Core benchmark implementations
- `src/ffi.rs` - Foreign Function Interface bindings
- `src/types.rs` - Type definitions and structures
- `src/utils.rs` - Utility functions and helpers

## Testing

The project includes comprehensive tests covering:
- Individual benchmark algorithms
- FFI functionality
- Cross-platform compatibility
- Memory management validation

## License

MIT License - See LICENSE file for details.