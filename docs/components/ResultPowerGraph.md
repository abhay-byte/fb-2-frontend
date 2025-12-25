# ResultPowerGraph

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A static analysis graph for Power Consumption results.
- **Summary Stats:** Shows Average, Min, and Max power draw.
- **Axis Logic:** Follows the same inverted Y-axis logic as the live graph (Charging = Top, Discharging = Bottom).
- **Visuals:** Uses the same color coding for charge/discharge states.

## Customization
| Parameter | Type | Description |
| :--- | :--- | :--- |
| `dataPoints` | `List<PowerDataPoint>` | Complete session dataset. |
| `totalDurationMs` | `Long` | Optional total duration override. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
ResultPowerGraph(
    dataPoints = powerResultData
)
```
