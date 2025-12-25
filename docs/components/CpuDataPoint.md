# CpuDataPoint

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A data class representing a single point in time for CPU utilization metrics. Used primarily for plotting CPU graphs.

## Properties
| Name | Type | Description |
| :--- | :--- | :--- |
| `timestamp` | `Long` | Unix timestamp in milliseconds when the data was recorded. |
| `utilization` | `Float` | CPU utilization percentage (0-100). |

## Usage
```kotlin
val dataPoint = CpuDataPoint(
    timestamp = System.currentTimeMillis(),
    utilization = 45.5f
)
```
