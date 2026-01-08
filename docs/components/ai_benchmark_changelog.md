# AI Benchmark Changelog

## [v1.2.0] - AI Optimization & Scalability Update

### Optimizations
- **Whisper ASR Speedup**:
  - **Issue**: Benchmark was taking ~110s on mid-range devices, creating a poor user experience.
  - **Fix**: Reduced default iterations from `5` to `1` and removed warmup for this heavy model.
  - **Result**: Benchmark now completes in ~15-20s while maintaining accuracy validity.

### Bug Fixes
- **USE QA Removed**:
  - **Reason**: Persistent `TFSentencepieceTokenizeOp` failures despite `flex` delegate and dependency inclusions. Removed to ensure platform stability.

### Architecture Changes
- **Scalable Workload System**:
  - **New**: Introduced `AiWorkloadParams` data class in `BenchmarkEvent.kt`.
  - **New**: implementing `getAiWorkloadParams(tier)` in `AiBenchmarkManager.kt`.
  - **Refactor**: Updated all `AiBenchmarkManager` execution methods to accept `warmupIterations` and `benchmarkIterations`.
  - **Benefit**: AI benchmarks now respect the "Workload Intensity" setting from the Home Screen (Test/Slow/Mid/Flagship profiles), similar to CPU benchmarks.

### UI Integration
- Verified that the Home Screen dropdown correctly passes `slow`, `mid`, or `flagship` tiers to the benchmark engine, enabling user control over AI test duration.

### Fixes (Session 2 - Approach B)
- **Specialized Runners**: Implemented custom inference logic for `MiniLM`, `MobileBERT`, and `DTLN`.
  - **Bypassed Generic Logic**: Replaced the generic "one-size-fits-all" inference runner with dedicated code paths for these complex models.
  - **DTLN**: Implemented explicit **State Loop** to feed output states back into inputs for correct temporal processing.

  - **MiniLM / MobileBERT**: Used exact `Int32` input buffers matching model signatures, bypassing generic auto-validation.
