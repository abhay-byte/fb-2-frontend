# EmptyStateView

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A placeholder view displayed when there is no data to show (e.g., empty list, no history). It features an icon, a title, and a descriptive message.

## Parameters
| Name | Type | Description |
| :--- | :--- | :--- |
| `icon` | `ImageVector` | The icon to display in the center. |
| `title` | `String` | Main heading text. |
| `message` | `String` | Subtitle or explanation text. |
| `modifier` | `Modifier` | Layout modifier. |
| `iconTint` | `Color` | Color of the icon. Default: Primary with 0.6 alpha. |

## Usage
```kotlin
if (items.isEmpty()) {
    EmptyStateView(
        icon = Icons.Default.Search,
        title = "No Results",
        message = "Try searching for something else."
    )
}
```
