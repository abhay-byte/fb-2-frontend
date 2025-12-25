# GpuUtilizationGraph

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
Visualizes GPU load over a 30-second window.
- **Root Detection:** Includes built-in logic to check for Root access (`RootAccessManager`).
- **Conditional UI:**
  - Shows "Checking root access..." spinner.
  - Shows "Root Required" error state if access is denied.
  - Shows the Graph if access is granted.
- **Graph:** Identical visual style to `CpuUtilizationGraph` (0-100% Y-axis).

## Parameters
| Parameter | Type | Description |
| :--- | :--- | :--- |
| `dataPoints` | `List<GpuDataPoint>` | Dataset to render. |
| `requiresRoot` | `Boolean` | Flag to trigger root check logic. Default: `false`. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
GpuUtilizationGraph(
    dataPoints = gpuLoadData,
    requiresRoot = true
)
```
