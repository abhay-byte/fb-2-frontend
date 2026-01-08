# AI Benchmarking System

## Overview
The AI Benchmarking component in **FinalBenchmark2** employs **LiteRT (formerly TensorFlow Lite)** to evaluate device performance across various machine learning tasks. It utilizes hardware acceleration (NPU, GPU) where available and falls back to CPU (XNNPACK) to ensure compatibility.

## Core Components

### 1. `AiBenchmarkManager`
**Path**: `app/src/main/java/com/ivarna/finalbenchmark2/aiBenchmark/AiBenchmarkManager.kt`

This is the central orchestration class. It handles:
- **Model Loading**: Loading `.tflite` files.
- **Acceleration Selection**: Trying NNAPI (NPU) -> GPU Delegate -> CPU (XNNPACK).
- **Inference Execution**: Running the models with provided inputs.
- **Measurement**: Calculating Throughput (TPS) and Latency (ms).

### 2. `AiWorkloadParams`
**Path**: `app/src/main/java/com/ivarna/finalbenchmark2/cpuBenchmark/BenchmarkEvent.kt` (Shared Data Class)

A centralized configuration class that defines iteration counts and warm-up cycles for each AI model. It supports different tiers:
- **Test**: Minimal iterations (1 run) for quick verification.
- **Slow**: Reduced load for budget devices.
- **Mid**: Standard load.
- **Flagship**: High iteration counts for precision.

### 3. `ModelRepository`
Defines the 10 supported AI models, their download URLs, and filenames.

## Supported Benchmarks

| Benchmark | Model | Task |
|-----------|-------|------|
| **Image Classification** | `MobileNet V3` (Float32) | Computer Vision |
| **Object Detection** | `EfficientDet Lite0` (Quantized) | Configurable Detection |
| **Text Embedding** | `MiniLM-L6` | NLP / Semantic Search |
| **Speech-to-Text (ASR)** | `Whisper Tiny` | Audio Transcription |
| **LLM Inference** | `Gemma 2B` (via GenAI Edge) | Generative AI |
| **Legacy Classification** | `MobileNet V1` | Baseline Vision |
| **Object Detection V2** | `YOLOv8n` | Modern Real-time Detection |

| **Text Classification** | `MobileBERT` | Sentiment/Classification |
| **Noise Suppression** | `DTLN` (Dual-Signal LSTM) | Audio Enhancement |

## Device Tiers & Configuration
To support a wide range of devices (from $100 budget phones to Flagships), we use a Tier-based workload system.

### Configuration Logic (`getAiWorkloadParams`)
Located in `AiBenchmarkManager.kt`, this function returns an `AiWorkloadParams` object based on the detected or selected Device Tier.

- **Warmup**: By default 2 runs. Heavy models (Whisper, LLM) use 0-1 warmup to save time.
- **Iterations**: 
  - *Light Models* (MobileNet): 5-10 iterations.
  - *Heavy Models* (Whisper): 1-2 iterations.

### UI Integration
- **Home Screen**: Users select "Workload Intensity" via dropdown.
  - "Low Accuracy - Fastest" -> `slow` tier
  - "Mid Accuracy - Fast" -> `mid` tier
  - "High Accuracy - Slow" -> `flagship` tier
- **Benchmark Screen**: Passes this tier setting to the benchmark manager, ensuring the correct workload is applied.

## Recent Optimizations (v1.2+)
- **Whisper ASR**: Reduced execution time by ~85% by optimizing iteration count (1 run vs 7 runs).

- **Scalability**: Introduced `AiWorkloadParams` to allow dynamic scaling of test duration without code changes.
