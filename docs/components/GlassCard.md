# GlassCard

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A stylized Card component implementing a specific "Glassmorphism" look. It applies a subtle gradient background and border to simulate a glass effect.

## Parameters
| Name | Type | Description |
| :--- | :--- | :--- |
| `modifier` | `Modifier` | Layout modifier. |
| `shape` | `Shape` | Shape of the card. Default: `RoundedCornerShape(24.dp)`. |
| `containerColor` | `Color` | Base background color. Default: SurfaceVariant with 0.15 alpha. |
| `borderColor` | `Color` | Border color. Default: OutlineVariant with 0.15 alpha. |
| `onClick` | `() -> Unit` | Optional callback. If provided, the card becomes clickable. |
| `content` | `@Composable () -> Unit` | Content inside the card. |

## Usage
```kotlin
GlassCard(
    modifier = Modifier.padding(8.dp),
    onClick = { /* Do something */ }
) {
    Text("Glassy Look", modifier = Modifier.padding(16.dp))
}
```
