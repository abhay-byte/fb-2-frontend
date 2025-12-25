# MemoryStats

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Description
A data class that encapsulates current memory (RAM) statistics of the device. It includes helper methods for formating byte values into human-readable strings (MB, GB, etc.).

## Properties
| Name | Type | Description |
| :--- | :--- | :--- |
| `usedBytes` | `Long` | The amount of RAM currently in use, in bytes. |
| `totalBytes` | `Long` | The total amount of RAM available on the device, in bytes. |
| `usagePercent` | `Int` | The percentage of RAM used (0-100). |
| `availableBytes` | `Long` | (Computed) The available RAM in bytes (`totalBytes - usedBytes`). |

## Methods

### `toString()`
Override of `toString()` to provide a formatted summary.
**Returns:** String format "Current: [Used] / [Total] ([Percent]%)" e.g., "Current: 4.2 GB / 8.0 GB (52%)"

### Companion Object: `formatBytes(bytes: Long): String`
Static utility to format byte counts into human readable units (B, KB, MB, GB, TB, PB).
**Usage:** `MemoryStats.formatBytes(1024 * 1024 * 1024L)` -> "1.0 GB"

## Usage
```kotlin
val stats = MemoryStats(
    usedBytes = 4L * 1024 * 1024 * 1024,
    totalBytes = 8L * 1024 * 1024 * 1024,
    usagePercent = 50
)
println(stats.toString()) // "Current: 4.0 GB / 8.0 GB (50%)"
```
