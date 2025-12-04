package com.ivarna.finalbenchmark2.utils

import kotlin.math.ln

/**
 * Format bytes to human-readable string
 */
fun formatBytes(bytes: Long): String {
    val unit = 1024
    if (bytes < unit) return "$bytes B"
    val exp = (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
    val pre = "KMGTPE"[exp - 1] + "B"
    
    // Calculate power manually to avoid import issues
    var result = 1.0
    for (i in 0 until exp) {
        result *= unit.toDouble()
    }
    
    return String.format("%.1f %s", bytes / result, pre)
}