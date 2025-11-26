package com.ivarna.finalbenchmark2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.finalbenchmark2.ui.theme.FinalBenchmark2Theme
import java.text.SimpleDateFormat
import java.util.*

data class BenchmarkResult(
    val id: String,
    val timestamp: Long,
    val finalScore: Double,
    val singleCoreScore: Double,
    val multiCoreScore: Double,
    val testName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen() {
    // Placeholder data for benchmark history
    val benchmarkHistory = listOf(
        BenchmarkResult(
            id = "1",
            timestamp = System.currentTimeMillis() - 864000, // 24 hours ago
            finalScore = 1250.5,
            singleCoreScore = 350.2,
            multiCoreScore = 1450.8,
            testName = "Full Benchmark"
        ),
        BenchmarkResult(
            id = "2",
            timestamp = System.currentTimeMillis() - 172800, // 48 hours ago
            finalScore = 1180.3,
            singleCoreScore = 340.5,
            multiCoreScore = 1380.7,
            testName = "Full Benchmark"
        ),
        BenchmarkResult(
            id = "3",
            timestamp = System.currentTimeMillis() - 2592000, // 72 hours ago
            finalScore = 1320.7,
            singleCoreScore = 360.8,
            multiCoreScore = 1520.4,
            testName = "Efficiency Test"
        ),
        BenchmarkResult(
            id = "4",
            timestamp = System.currentTimeMillis() - 6048000, // 1 week ago
            finalScore = 1090.2,
            singleCoreScore = 320.1,
            multiCoreScore = 1280.5,
            testName = "Throttle Test"
        )
    )
    
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    
    FinalBenchmark2Theme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header
                Text(
                    text = "Benchmark History",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                
                if (benchmarkHistory.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "No history",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "No benchmark history found",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = "Run your first benchmark to see results here",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // Benchmark history list
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(benchmarkHistory) { result ->
                            BenchmarkHistoryItem(
                                result = result,
                                timestampFormatter = formatter
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BenchmarkHistoryItem(
    result: BenchmarkResult,
    timestampFormatter: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.testName,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                
                Text(
                    text = timestampFormatter.format(Date(result.timestamp)),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Single: ${String.format("%.1f", result.singleCoreScore)} | Multi: ${String.format("%.1f", result.multiCoreScore)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            // Final score
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = String.format("%.1f", result.finalScore),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Score",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}