# GpuFrequencyCard

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A card component that displays detailed GPU frequency and status information.
- **State Handling:** Handles Loading, Available, Error, RootRequired, and NotSupported states.
- **Data Display:**
  - Current Frequency (Big, Monospace font).
  - Min/Max Frequency range.
  - Governor name.
  - Source path (e.g. `Root (/sys/...)`).
  - GPU Load percentage (if available).

## Customization
| Parameter | Type | Description |
| :--- | :--- | :--- |
| `viewModel` | `GpuInfoViewModel` | Source of GPU data state. Defaults to `viewModel()`. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
GpuFrequencyCard(
    modifier = Modifier.padding(16.dp)
)
```
