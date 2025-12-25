# PowerDataPoint

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A data class recording power consumption metrics at a specific timestamp. Critical for battery and efficiency analysis.

## Properties
| Name | Type | Description |
| :--- | :--- | :--- |
| `timestamp` | `Long` | Unix timestamp in milliseconds. |
| `powerWatts` | `Float` | Instantaneous power consumption in Watts. |

## Usage
```kotlin
val powerPoint = PowerDataPoint(
    timestamp = System.currentTimeMillis(),
    powerWatts = 2.5f // 2.5 Watts
)
```
