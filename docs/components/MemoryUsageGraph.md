# MemoryUsageGraph

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A line graph tracking RAM usage percentage over a 30-second window.
- **Range:** Fixed 0-100% Y-axis.
- **Visuals:** Primary colored line and data points.
- **Status:** Prominently displays current usage percentage.

## Data Requirements
| Data Type | Description |
| :--- | :--- |
| `List<MemoryDataPoint>` | Timestamped memory utilization data (0-100 float). |

## Customization
| Parameter | Type | Description |
| :--- | :--- | :--- |
| `dataPoints` | `List<MemoryDataPoint>` | The dataset to render. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
MemoryUsageGraph(
    dataPoints = ramHistory
)
```
