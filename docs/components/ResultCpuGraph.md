# ResultCpuGraph

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A static version of the CPU graph intended for post-benchmark results.
- **Difference from Live Graph:** Uses relative time (0s to End) instead of "30s ago".
- **Summary Stats:** Displays Average, Min, and Max CPU usage for the entire session instead of "Current" value.
- **Color Coding:** Max value is colored red if > 90%, otherwise secondary color.

## Customization
| Parameter | Type | Description |
| :--- | :--- | :--- |
| `dataPoints` | `List<CpuDataPoint>` | Complete session dataset. |
| `totalDurationMs` | `Long` | Optional total duration override. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
ResultCpuGraph(
    dataPoints = fullSessionData,
    totalDurationMs = 60000L
)
```
