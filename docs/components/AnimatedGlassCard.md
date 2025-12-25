# AnimatedGlassCard

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A composable that wraps a `GlassCard` and adds entrance animations (scaling and fading in) when the component is first composed.

## Parameters
| Name | Type | Description |
| :--- | :--- | :--- |
| `modifier` | `Modifier` | Layout modifier. |
| `shape` | `Shape` | Shape of the card. Default: `RoundedCornerShape(24.dp)`. |
| `containerColor` | `Color` | Background color of the card. Default: SurfaceVariant with 0.15 alpha. |
| `borderColor` | `Color` | Border color. Default: OutlineVariant with 0.15 alpha. |
| `delayMillis` | `Int` | Delay before animation starts (ms). Default: 0. |
| `animationDuration` | `Int` | Duration of the scale animation (ms). Default: 500. |
| `onClick` | `() -> Unit` | Optional callback when card is clicked. |
| `content` | `@Composable () -> Unit` | The content to display inside the card. |

## Usage
```kotlin
AnimatedGlassCard(
    modifier = Modifier.padding(16.dp),
    delayMillis = 200,
    onClick = { /* Handle click */ }
) {
    Text("Hello World")
}
```
