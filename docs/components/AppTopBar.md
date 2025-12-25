# AppTopBar

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A center-aligned top app bar used in the application. It typically displays a transparent background and an action button (Settings).

## Parameters
| Name | Type | Description |
| :--- | :--- | :--- |
| `onSettingsClick` | `() -> Unit` | Callback invoked when the settings icon is clicked. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
Scaffold(
    topBar = {
        AppTopBar(
            onSettingsClick = { navigateToSettings() }
        )
    }
) { padding ->
    // Content
}
```
