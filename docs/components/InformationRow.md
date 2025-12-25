# InformationRow

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A row component used to display key-value pairs, typically in a list of device specifications. It includes a custom glassmorphic gradient separator line at the bottom (unless it's the last item).

## Parameters
| Name | Type | Description |
| :--- | :--- | :--- |
| `itemValue` | `ItemValue` | Domain model containing the Name and Value strings. |
| `isLastItem` | `Boolean` | If true, the bottom separator line is hidden. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
val info = ItemValue("Processor", "Snapdragon 8 Gen 3")
InformationRow(
    itemValue = info,
    isLastItem = false
)
```
