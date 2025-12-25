# ContextualPermissionRequest

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A wrapper composable that handles runtime permission requests. usage.
- If the permission IS granted, it renders the `content`.
- If the permission is NOT granted, it renders a UI card explaining why the permission is needed and a button to request it.

## Parameters
| Name | Type | Description |
| :--- | :--- | :--- |
| `permission` | `String` | The Android Manifest permission string (e.g. `Manifest.permission.CAMERA`). |
| `rationaleText` | `String` | Text displayed to the user explaining why the permission is required. |
| `modifier` | `Modifier` | Layout modifier. |
| `content` | `@Composable () -> Unit` | The content to show when permission is granted. |

## Specialized Variants
The file also provides convenience wrappers for common permissions:
- `CameraPermissionRequest`
- `PhoneStatePermissionRequest`
- `BodySensorsPermissionRequest`
- `BluetoothPermissionRequest` (Handles API level differences)

## Usage
```kotlin
CameraPermissionRequest {
    // This content is only shown if Camera permission is granted
    CameraPreview()
}
```
