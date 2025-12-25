# BatteryTemperatureGraph

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A line graph visualizing battery temperature over time. Key features:
- Dynamic Y-axis scaling based on min/max temperature.
- Real-time "Max" and "Average" temperature indicators.
- Color-coded current temperature (Secondary, Tertiary, Error based on threshold).
- Custom Y-axis grid lines and X-axis time labels.
- Glassmorphic card container.

## Data Requirements
| Data Type | Description |
| :--- | :--- |
| `List<TemperatureDataPoint>` | A list of timestamped temperature readings. |

## Customization
| Parameter | Type | Description |
| :--- | :--- | :--- |
| `dataPoints` | `List<TemperatureDataPoint>` | The dataset to render. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
BatteryTemperatureGraph(
    dataPoints = viewModel.batteryHistoryState.value,
    modifier = Modifier.padding(16.dp)
)
```
