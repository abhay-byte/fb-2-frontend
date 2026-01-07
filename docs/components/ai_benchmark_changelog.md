# AI Benchmark Changelog

## [v1.2.0] - AI Optimization & Scalability Update

### Optimizations
- **Whisper ASR Speedup**:
  - **Issue**: Benchmark was taking ~110s on mid-range devices, creating a poor user experience.
  - **Fix**: Reduced default iterations from `5` to `1` and removed warmup for this heavy model.
  - **Result**: Benchmark now completes in ~15-20s while maintaining accuracy validity.

### Bug Fixes
- **USE QA Failure**:
  - **Issue**: `Universal Sentence Encoder QA` failed with `Unresolved custom op: TFSentencepieceTokenizeOp`.
  - **Fix 1**: Added `org.tensorflow:tensorflow-lite-select-tf-ops:2.16.1` dependency to `build.gradle.kts`.
  - **Fix 2**: Updated `AiBenchmarkManager.runUseQa` to pass `String` array inputs instead of dummy Integer tensors, matching the model's signature.

### Architecture Changes
- **Scalable Workload System**:
  - **New**: Introduced `AiWorkloadParams` data class in `BenchmarkEvent.kt`.
  - **New**: implementing `getAiWorkloadParams(tier)` in `AiBenchmarkManager.kt`.
  - **Refactor**: Updated all `AiBenchmarkManager` execution methods to accept `warmupIterations` and `benchmarkIterations`.
  - **Benefit**: AI benchmarks now respect the "Workload Intensity" setting from the Home Screen (Test/Slow/Mid/Flagship profiles), similar to CPU benchmarks.

### UI Integration
- Verified that the Home Screen dropdown correctly passes `slow`, `mid`, or `flagship` tiers to the benchmark engine, enabling user control over AI test duration.
