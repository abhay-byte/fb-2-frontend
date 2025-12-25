# TemperatureDataPoint

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A data class for tracking device temperature over time. Typically used for CPU or Battery thermal monitoring.

## Properties
| Name | Type | Description |
| :--- | :--- | :--- |
| `timestamp` | `Long` | Unix timestamp in milliseconds. |
| `temperature` | `Float` | Temperature value in Celsius. |

## Usage
```kotlin
val tempPoint = TemperatureDataPoint(
    timestamp = System.currentTimeMillis(),
    temperature = 42.0f // 42°C
)
```
