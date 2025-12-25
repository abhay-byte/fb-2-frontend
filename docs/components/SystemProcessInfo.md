# SystemProcessInfo

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Overview
This file contains data classes used to represent the state of running processes and system packages.

## Class: `SystemInfoSummary`
Aggregates high-level statistics about the system's process state.

### Properties
| Name | Type | Description |
| :--- | :--- | :--- |
| `runningProcesses` | `Int` | Count of currently active processes. Default: 0. |
| `totalPackages` | `Int` | Total number of installed packages found. Default: 0. |
| `totalServices` | `Int` | Total number of active services. Default: 0. |
| `processes` | `List<ProcessItem>` | List of detailed process items. Default: empty. |

## Class: `ProcessItem`
Represents a single running process with its resource usage details.

### Properties
| Name | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | Display name of the process (often the app label or package name). |
| `pid` | `Int` | Process ID. |
| `ramUsage` | `Int` | Memory usage in Megabytes (MB). |
| `state` | `String` | Current state of the process (e.g., "Running", "SLEEP"). |
| `packageName` | `String` | Comparison unique package name identifier. Default: "". |

## Usage
```kotlin
val summary = SystemInfoSummary(
    runningProcesses = 1,
    processes = listOf(
        ProcessItem(
            name = "My Browser",
            pid = 1234,
            ramUsage = 350,
            state = "Running",
            packageName = "com.example.browser"
        )
    )
)
```
