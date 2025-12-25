# CpuTemperatureGraph

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A line graph specifically designed for monitoring CPU temperature.
- **Dynamic Range:** Auto-scales Y-axis (Default: 20°C - 80°C if empty).
- **Threshold Coloring:** Text changes color based on temperature (Primary < 60°C < Tertiary < 70°C < Error).
- **Indicators:** Shows real-time Max and Average temperature.

## Data Requirements
| Data Type | Description |
| :--- | :--- |
| `List<TemperatureDataPoint>` | A list of timestamped CPU temperature readings. |

## Customization
| Parameter | Type | Description |
| :--- | :--- | :--- |
| `dataPoints` | `List<TemperatureDataPoint>` | The dataset to render. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
CpuTemperatureGraph(
    dataPoints = cpuTempData,
    modifier = Modifier.fillMaxWidth()
)
```
