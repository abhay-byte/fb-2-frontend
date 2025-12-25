# ProcessTable & SummaryCard

**Package:** `com.ivarna.finalbenchmark2.ui.components`

## Overview
This file contains components for displaying system process information in a tabular format and a high-level summary card.

## Component: `ProcessTable`
Displays a list of `ProcessItem`s in a table (LazyColumn). Columns: App, PID, State, RAM.

### Parameters
| Name | Type | Description |
| :--- | :--- | :--- |
| `processes` | `List<ProcessItem>` | List of process data to display. |
| `modifier` | `Modifier` | Layout modifier. |

## Component: `SummaryCard`
Displays a 3-column summary of system totals (Processes, Packages, Services).

### Parameters
| Name | Type | Description |
| :--- | :--- | :--- |
| `summary` | `SystemInfoSummary` | Data object containing the counts. |
| `modifier` | `Modifier` | Layout modifier. |

## Usage
```kotlin
Column {
    SummaryCard(summary = mySystemSummary)
    Spacer(Modifier.height(16.dp))
    ProcessTable(processes = mySystemSummary.processes)
}
```
