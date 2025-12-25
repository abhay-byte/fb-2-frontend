# PowerConsumptionGraph

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A complex graph visualizing power consumption in Watts.
- **Inverted Y-Axis Logic:** Displays negative values (Charging) at the TOP and positive values (Discharging) at the BOTTOM relative to a zero line.
- **Color Coding:** 
  - Charging (< 0W): Secondary color (Green/Teal).
  - Discharging (> 0W): Error color (Red).
- **Status Text:** Displays text like "High Charge", "Moderate Discharge" based on thresholds.
- **Dynamic Range:** Auto-scales based on min/max power values.

## Data Requirements
| Data Type | Description |
| :--- | :--- |
| `List<PowerDataPoint>` | Timestamped power readings in Watts. |

## Customization
| Parameter | Type | Description |
| :--- | :--- | :--- |
| `dataPoints` | `List<PowerDataPoint>` | The dataset to render. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
PowerConsumptionGraph(
    dataPoints = powerHistory
)
```
