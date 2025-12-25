# CpuUtilizationGraph

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A specialized graph for tracking CPU load percentage.
- **Fixed Range:** Y-axis is always 0-100%.
- **Time Window:** Displays a fixed 30-second window (labels: 30s, 15s, 0s).
- **Current Load:** Prominent display of the latest utilization value.
- **Styling:** Uses primary color for the line and indicators.

## Data Requirements
| Data Type | Description |
| :--- | :--- |
| `List<CpuDataPoint>` | Timestamped utilization data (0-100 float). |

## Customization
| Parameter | Type | Description |
| :--- | :--- | :--- |
| `dataPoints` | `List<CpuDataPoint>` | The dataset to render. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
CpuUtilizationGraph(
    dataPoints = cpuLoadHistory
)
```
