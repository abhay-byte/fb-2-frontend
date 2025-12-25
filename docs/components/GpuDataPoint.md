# GpuDataPoint

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A data class representing a single point in time for GPU utilization metrics. Used for visualizing GPU performance over time.

## Properties
| Name | Type | Description |
| :--- | :--- | :--- |
| `timestamp` | `Long` | Unix timestamp in milliseconds when the data was recorded. |
| `utilization` | `Float` | GPU utilization percentage (0-100). |

## Usage
```kotlin
val gpuPoint = GpuDataPoint(
    timestamp = System.currentTimeMillis(),
    utilization = 88.0f
)
```
