package com.ivarna.finalbenchmark2.utils

import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Data class to hold comprehensive display information
 */
data class DisplayInfo(
    val resolution: String,
    val density: String,
    val physicalSize: String,
    val aspectRatio: String,
    val refreshRate: String,
    val maxRefreshRate: String,
    val hdrSupport: String,
    val hdrTypes: List<String>,
    val orientation: String,
    val rotation: Int,
    val exactDpiX: String,
    val exactDpiY: String,
    val wideColorGamut: Boolean,
    val realMetrics: String,
    val safeAreaInsets: String,
    val displayCutout: String,
    val brightnessLevel: String?,
    val screenTimeout: String?
)

/**
 * Utility class to gather comprehensive display information
 */
class DisplayUtils(private val context: Context) {
    
    /**
     * Get comprehensive display information
     */
    fun getDisplayInfo(): DisplayInfo {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display ?: windowManager.defaultDisplay
        } else {
            windowManager.defaultDisplay
        }
        
        val metrics = DisplayMetrics()
        val realMetrics = DisplayMetrics()
        
        // Get standard metrics (excluding system bars)
        display?.getMetrics(metrics)
        
        // Get real metrics (including system bars) for Android API 17+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display?.getRealMetrics(realMetrics)
        } else {
            // Manually copy metrics values for older APIs
            realMetrics.widthPixels = metrics.widthPixels
            realMetrics.heightPixels = metrics.heightPixels
            realMetrics.density = metrics.density
            realMetrics.densityDpi = metrics.densityDpi
            realMetrics.scaledDensity = metrics.scaledDensity
            realMetrics.xdpi = metrics.xdpi
            realMetrics.ydpi = metrics.ydpi
        }
        
        // Calculate physical screen size
        val physicalSize = calculatePhysicalSize(realMetrics)
        
        // Calculate aspect ratio
        val aspectRatio = calculateAspectRatio(realMetrics.widthPixels, realMetrics.heightPixels)
        
        // Get refresh rate information
        val refreshRateInfo = getRefreshRateInfo(display)
        
        // Get HDR information
        val hdrInfo = getHdrInfo(display)
        
        // Get orientation and rotation
        val orientationInfo = getOrientationInfo(display)
        
        // Get exact DPI values
        val exactDpiX = "${realMetrics.xdpi} dpi"
        val exactDpiY = "${realMetrics.ydpi} dpi"
        
        // Check wide color gamut support
        val wideColorGamut = checkWideColorGamut(display)
        
        // Get safe area and cutout info
        val safeAreaAndCutoutInfo = getSafeAreaAndCutoutInfo()
        
        // Get brightness and timeout (if available)
        val brightnessAndTimeoutInfo = getBrightnessAndTimeoutInfo()
        
        return DisplayInfo(
            resolution = "${realMetrics.widthPixels} x ${realMetrics.heightPixels}",
            density = "${metrics.densityDpi} dpi",
            physicalSize = physicalSize,
            aspectRatio = aspectRatio,
            refreshRate = refreshRateInfo.first,
            maxRefreshRate = refreshRateInfo.second,
            hdrSupport = if (hdrInfo.first) "Yes" else "No",
            hdrTypes = hdrInfo.second,
            orientation = orientationInfo.first,
            rotation = orientationInfo.second,
            exactDpiX = exactDpiX,
            exactDpiY = exactDpiY,
            wideColorGamut = wideColorGamut,
            realMetrics = "${metrics.widthPixels} x ${metrics.heightPixels} (excl. system bars)\n${realMetrics.widthPixels} x ${realMetrics.heightPixels} (incl. system bars)",
            safeAreaInsets = safeAreaAndCutoutInfo.first,
            displayCutout = safeAreaAndCutoutInfo.second,
            brightnessLevel = brightnessAndTimeoutInfo.first,
            screenTimeout = brightnessAndTimeoutInfo.second
        )
    }
    
    /**
     * Calculate physical screen size in inches using exact DPI values
     */
    private fun calculatePhysicalSize(metrics: DisplayMetrics): String {
        val x = (metrics.widthPixels / metrics.xdpi.toDouble()).pow(2)
        val y = (metrics.heightPixels / metrics.ydpi.toDouble()).pow(2)
        val inches = sqrt(x + y)
        return String.format("%.1f\"", inches)
    }
    
    /**
     * Calculate aspect ratio and return it in a human-readable format
     */
    private fun calculateAspectRatio(width: Int, height: Int): String {
        val gcd = gcd(width, height)
        val aspectWidth = width / gcd
        val aspectHeight = height / gcd
        
        // Check for common aspect ratios and format them nicely
        return when {
            aspectWidth == 19 && aspectHeight == 9 -> "19:9 (Full HD+)"
            aspectWidth == 20 && aspectHeight == 9 -> "20:9 (QHD+)"
            aspectWidth == 18 && aspectHeight == 9 -> "18:9 (QHD)"
            aspectWidth == 16 && aspectHeight == 9 -> "16:9 (Full HD)"
            aspectWidth == 4 && aspectHeight == 3 -> "4:3"
            aspectWidth == 3 && aspectHeight == 2 -> "3:2"
            else -> "$aspectWidth:$aspectHeight"
        }
    }
    
    /**
     * Get refresh rate information (current and maximum supported)
     */
    private fun getRefreshRateInfo(display: android.view.Display?): Pair<String, String> {
        val currentRefreshRate = display?.refreshRate?.let { "${it.toInt()} Hz" } ?: "Unknown"
        
        // For Android API 23+ we can get supported modes
        val maxRefreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && display != null) {
            val modes = display.supportedModes
            if (modes.isNotEmpty()) {
                val maxMode = modes.maxByOrNull { it.physicalWidth * it.physicalHeight }
                "${maxMode?.physicalHeight?.toInt() ?: "Unknown"} Hz"
            } else {
                currentRefreshRate
            }
        } else {
            currentRefreshRate
        }
        
        return Pair(currentRefreshRate, maxRefreshRate)
    }
    
    /**
     * Get HDR capabilities information
     */
    private fun getHdrInfo(display: android.view.Display?): Pair<Boolean, List<String>> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && display != null) {
            val hdrCapabilities = display.hdrCapabilities
            if (hdrCapabilities != null) {
                val hdrTypes = hdrCapabilities.supportedHdrTypes?.map { type ->
                    when (type) {
                        1 -> "HDR10"  // HDR_TYPE_HDR10
                        2 -> "HLG"    // HDR_TYPE_HLG  
                        3 -> "Dolby Vision"  // HDR_TYPE_DOLBY_VISION
                        4 -> "Static HDR"    // HDR_TYPE_STATIC_HDR
                        else -> "Unknown HDR Type ($type)"
                    }
                } ?: emptyList()
                Pair(hdrTypes.isNotEmpty(), hdrTypes)
            } else {
                Pair(false, emptyList())
            }
        } else {
            Pair(false, emptyList())
        }
    }
    
    /**
     * Get orientation and rotation information
     */
    private fun getOrientationInfo(display: android.view.Display?): Pair<String, Int> {
        val rotation = display?.rotation ?: Surface.ROTATION_0
        val orientation = when (rotation) {
            Surface.ROTATION_0 -> "Portrait"
            Surface.ROTATION_90 -> "Landscape"
            Surface.ROTATION_180 -> "Reverse Portrait"
            Surface.ROTATION_270 -> "Reverse Landscape"
            else -> "Unknown"
        }
        return Pair(orientation, rotation)
    }
    
    /**
     * Check if display supports wide color gamut
     */
    private fun checkWideColorGamut(display: android.view.Display?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                display?.isWideColorGamut ?: false
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }
    
    /**
     * Get safe area insets and display cutout information
     */
    private fun getSafeAreaAndCutoutInfo(): Pair<String, String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This would require window insets which are better handled in the UI layer
            // For now, return placeholder info that can be enhanced later
            Pair("Safe area info requires window insets", "Cutout info requires window insets")
        } else {
            Pair("Not available", "Not available (Android 9+ required)")
        }
    }
    
    /**
     * Get brightness level and screen timeout (if permissions allow)
     */
    private fun getBrightnessAndTimeoutInfo(): Pair<String?, String?> {
        return try {
            val contentResolver = context.contentResolver
            
            // Get current brightness (requires WRITE_SETTINGS permission for accurate value)
            val brightnessValue = android.provider.Settings.System.getInt(
                contentResolver,
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
                -1
            )
            val brightness = if (brightnessValue >= 0) {
                "${((brightnessValue / 255.0) * 100).toInt()}%"
            } else {
                null
            }
            
            // Get screen timeout
            val timeoutValue = android.provider.Settings.System.getInt(
                contentResolver,
                android.provider.Settings.System.SCREEN_OFF_TIMEOUT,
                -1
            )
            val timeout = if (timeoutValue > 0) {
                when (timeoutValue) {
                    15000 -> "15 seconds"
                    30000 -> "30 seconds"
                    60000 -> "1 minute"
                    120000 -> "2 minutes"
                    300000 -> "5 minutes"
                    600000 -> "10 minutes"
                    1800000 -> "30 minutes"
                    -1 -> "Never"
                    else -> "${timeoutValue / 1000} seconds"
                }
            } else {
                null
            }
            
            Pair(brightness, timeout)
        } catch (e: Exception) {
            Pair(null, null)
        }
    }
    
    /**
     * Calculate greatest common divisor for aspect ratio calculation
     */
    private fun gcd(a: Int, b: Int): Int {
        return if (b == 0) a else gcd(b, a % b)
    }
}