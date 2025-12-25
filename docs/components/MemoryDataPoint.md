# MemoryDataPoint

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A data class representing memory usage at a specific point in time. Used for tracking RAM consumption trends.

## Properties
| Name | Type | Description |
| :--- | :--- | :--- |
| `timestamp` | `Long` | Unix timestamp in milliseconds when the data was recorded. |
| `utilization` | `Float` | Memory utilization percentage (0-100). |

## Usage
```kotlin
val memPoint = MemoryDataPoint(
    timestamp = System.currentTimeMillis(),
    utilization = 62.3f
)
```
