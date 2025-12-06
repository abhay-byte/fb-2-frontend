package com.ivarna.finalbenchmark2

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import com.ivarna.finalbenchmark2.ui.viewmodels.PerformanceOptimizationStatus
import com.ivarna.finalbenchmark2.navigation.MainNavigation
import com.ivarna.finalbenchmark2.ui.theme.FinalBenchmark2Theme
import com.ivarna.finalbenchmark2.ui.theme.LocalThemeMode
import com.ivarna.finalbenchmark2.ui.theme.provideThemeMode
import com.ivarna.finalbenchmark2.ui.viewmodels.MainViewModel
import com.ivarna.finalbenchmark2.ui.viewmodels.RootStatus
import com.ivarna.finalbenchmark2.utils.ThemePreferences
import com.ivarna.finalbenchmark2.ui.theme.ThemeMode
import com.topjohnwu.superuser.Shell
import dev.chrisbanes.haze.HazeState

class MainActivity : ComponentActivity() {
    
    // 1. Configure Shell at the top level
    companion object {
        init {
            // Set settings before the main shell is created
            Shell.enableVerboseLogging = true
            Shell.setDefaultBuilder(
                Shell.Builder.create()
                    .setFlags(Shell.FLAG_REDIRECT_STDERR)
                    .setTimeout(10)
            )
        }
    }
    
    private var mainViewModel: MainViewModel? = null
    private var sustainedModeIntendedState = false
    
    // Wake lock instance
    private lateinit var wakeLock: PowerManager.WakeLock
    private var isWakeLockHeld = false
    
    // Screen always on status
    private var isScreenAlwaysOn = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable edge to edge for full screen experience
        enableEdgeToEdge()
        
        // Initialize wake lock (but don't acquire yet)
        initializeWakeLock()
        
        // Enable screen always on
        enableScreenAlwaysOn()
        
        // Enable Sustained Performance Mode to prevent thermal throttling
        enableSustainedPerformanceMode()
        
        // Use WindowInsetsController to hide system bars for full-screen immersive mode
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
        
        val themePreferences = ThemePreferences(this)
        val themeMode = themePreferences.getThemeMode()
        
        // Apply the theme mode using AppCompatDelegate BEFORE setting content
        // This ensures the system theme is applied on first launch
        applyThemeMode(themeMode)
        
        setContent {
            var currentThemeMode by remember { mutableStateOf(themeMode) }
            
            // Initialize MainViewModel to check root access once at app startup
            val mainViewModel: MainViewModel = viewModel()
            // Store reference to mainViewModel for use in Activity methods
            this@MainActivity.mainViewModel = mainViewModel
            val rootStatus by mainViewModel.rootState.collectAsStateWithLifecycle()
            val isRootAvailable = rootStatus == RootStatus.ROOT_WORKING || rootStatus == RootStatus.ROOT_AVAILABLE
            
            // Update performance optimization status based on sustained performance mode
            val performanceOptimizations by mainViewModel.performanceOptimizations.collectAsStateWithLifecycle()
            
            // Update wake lock and screen always on status in the MainViewModel
            LaunchedEffect(Unit) {
                mainViewModel.updateWakeLockStatus(PerformanceOptimizationStatus.ENABLED) // Ready to be used
                mainViewModel.updateScreenAlwaysOnStatus(PerformanceOptimizationStatus.ENABLED) // Always on
                Log.i("FinalBenchmark2", "Initial wake lock status updated to ENABLED (ready state)")
            }
            
            
            
            // Provide theme mode to the composition
            provideThemeMode(currentThemeMode) {
                FinalBenchmark2Theme(themeMode = currentThemeMode) {
                    val hazeState = remember { HazeState() }
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {} // Empty top bar to remove any system app bar
                    ) { innerPadding ->
                        MainNavigation(
                            modifier = Modifier.padding(innerPadding),
                            hazeState = hazeState,
                            rootStatus = rootStatus
                        )
                    }
                }
            }
        }
    }
    
    /**
     * Enable Sustained Performance Mode to prevent thermal throttling
     * This should be called as early as possible in the Activity lifecycle
     */
    private fun enableSustainedPerformanceMode() {
        // Check if device supports API 24 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                // Enable sustained performance mode
                window.setSustainedPerformanceMode(true)
                sustainedModeIntendedState = true
                
                // Log success (optional but recommended for debugging)
                Log.i("FinalBenchmark2", "Sustained Performance Mode: ENABLED")
            } catch (e: Exception) {
                // Handle any errors gracefully
                sustainedModeIntendedState = false
                Log.e("FinalBenchmark2", "Error enabling Sustained Performance Mode", e)
                // Check if it's because the device doesn't support it
                if (e is java.lang.UnsupportedOperationException) {
                    Log.w("FinalBenchmark2", "Sustained Performance Mode: NOT SUPPORTED on this device")
                }
            }
        } else {
            // Running on Android < 7.0, feature not available
            sustainedModeIntendedState = false
            Log.w("FinalBenchmark2", "Sustained Performance Mode requires API 24+. Current API: ${Build.VERSION.SDK_INT}")
        }
    }
    
    /**
     * Check if sustained performance mode is currently active by checking our internal state
     * This method returns our intended state of the sustained performance mode
     */
    fun isSustainedPerformanceModeActive(): Boolean {
        return sustainedModeIntendedState
    }
    
    private fun applyThemeMode(themeMode: ThemeMode) {
        when (themeMode) {
            ThemeMode.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            ThemeMode.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            ThemeMode.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            // For custom themes, we'll default to dark mode since they're mostly dark themes
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
    
    fun updateTheme(themeMode: ThemeMode) {
        applyThemeMode(themeMode)
        // Recreate the activity to apply the theme immediately
        recreate()
    }
    
    /**
     * Initialize the wake lock but don't acquire it yet
     * We'll acquire it when benchmark starts
     */
    private fun initializeWakeLock() {
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            
            // Create a PARTIAL_WAKE_LOCK
            // This keeps CPU running but allows screen to turn off if needed
            // (We're using FLAG_KEEP_SCREEN_ON separately to keep screen on)
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "FinalBenchmark2::CPUBenchmarkWakeLock"
            )
            
            // Set wake lock to not reference counted
            // This means acquire/release calls must be balanced
            wakeLock.setReferenceCounted(false)
            
            Log.i("FinalBenchmark2", "Wake Lock initialized successfully. isHeld: ${wakeLock.isHeld}")
            
        } catch (e: Exception) {
            Log.e("FinalBenchmark2", "Failed to initialize Wake Lock", e)
        }
    }
    
    /**
     * Acquire wake lock to keep CPU at maximum frequency
     * Call this when benchmark starts
     */
    fun acquireWakeLock() {
        try {
            Log.i("FinalBenchmark2", "Attempting to acquire wake lock. isWakeLockHeld: $isWakeLockHeld, isInitialized: ${::wakeLock.isInitialized}, isHeld: ${if (::wakeLock.isInitialized) wakeLock.isHeld else "N/A"}")
            
            if (!isWakeLockHeld && ::wakeLock.isInitialized) {
                // Acquire wake lock without timeout (held until manually released)
                wakeLock.acquire()
                isWakeLockHeld = true
                
                // Update MainViewModel status
                mainViewModel?.updateWakeLockStatus(PerformanceOptimizationStatus.ENABLED)
                
                Log.i("FinalBenchmark2", "Wake Lock ACQUIRED - CPU will stay at maximum frequency. isHeld: ${wakeLock.isHeld}")
            } else {
                Log.i("FinalBenchmark2", "Wake lock was not acquired. Conditions: isWakeLockHeld=$isWakeLockHeld, isInitialized=${::wakeLock.isInitialized}")
            }
        } catch (e: Exception) {
            Log.e("FinalBenchmark2", "Failed to acquire Wake Lock", e)
            isWakeLockHeld = false
        }
    }
    
    /**
     * Release wake lock when benchmark completes
     * IMPORTANT: Always release in finally block to prevent battery drain
     */
    fun releaseWakeLock() {
        try {
            Log.i("FinalBenchmark2", "Attempting to release wake lock. isWakeLockHeld: $isWakeLockHeld, isInitialized: ${::wakeLock.isInitialized}, isHeld: ${if (::wakeLock.isInitialized) wakeLock.isHeld else "N/A"}")
            
            if (isWakeLockHeld && ::wakeLock.isInitialized && wakeLock.isHeld) {
                wakeLock.release()
                isWakeLockHeld = false
                
                // Update MainViewModel status
                mainViewModel?.updateWakeLockStatus(PerformanceOptimizationStatus.DISABLED)
                
                Log.i("FinalBenchmark2", "Wake Lock RELEASED")
            } else {
                Log.i("FinalBenchmark2", "Wake lock was not released. Conditions: isWakeLockHeld=$isWakeLockHeld, isInitialized=${::wakeLock.isInitialized}, isHeld=${if (::wakeLock.isInitialized) wakeLock.isHeld else "N/A"}")
            }
        } catch (e: Exception) {
            Log.e("FinalBenchmark2", "Failed to release Wake Lock", e)
        }
    }
    
    /**
     * Enable screen always on using window flags
     * This keeps the screen on while this activity is visible
     */
    private fun enableScreenAlwaysOn() {
        try {
            // Add FLAG_KEEP_SCREEN_ON to window
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            isScreenAlwaysOn = true
            
            Log.i("FinalBenchmark2", "Screen Always On: ENABLED")
            
            // Optional: Also add FLAG_TURN_SCREEN_ON to wake screen if it's off
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            
        } catch (e: Exception) {
            Log.e("FinalBenchmark2", "Failed to enable Screen Always On", e)
            isScreenAlwaysOn = false
        }
    }
    
    /**
     * Disable screen always on when no longer needed
     */
    private fun disableScreenAlwaysOn() {
        try {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            isScreenAlwaysOn = false
            
            Log.i("FinalBenchmark2", "Screen Always On: DISABLED")
            
        } catch (e: Exception) {
            Log.e("FinalBenchmark2", "Failed to disable Screen Always On", e)
        }
    }
    
    /**
     * Get the current wake lock status
     */
    fun isWakeLockActive(): Boolean {
        val status = isWakeLockHeld
        Log.i("FinalBenchmark2", "isWakeLockActive() called, returning: $status, actual isHeld: ${if (::wakeLock.isInitialized) wakeLock.isHeld else "N/A"}")
        return status
    }
    
    /**
     * Check if wake lock is ready (initialized but not acquired)
     */
    fun isWakeLockReady(): Boolean {
        val isReady = ::wakeLock.isInitialized && !isWakeLockHeld
        Log.i("FinalBenchmark2", "isWakeLockReady() called, returning: $isReady")
        return isReady
    }
    
    
    /**
     * Get the current screen always on status
     */
    fun isScreenAlwaysOnActive(): Boolean {
        return isScreenAlwaysOn
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.i("FinalBenchmark2", "onDestroy() called - releasing wake lock if held")
        
        // Sustained mode is automatically disabled when activity is destroyed
        // But you can manually disable it if needed:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                this@MainActivity.window.setSustainedPerformanceMode(false)
                sustainedModeIntendedState = false
            } catch (e: Exception) {
                Log.e("FinalBenchmark2", "Error disabling Sustained Performance Mode", e)
                sustainedModeIntendedState = false
            }
        }
        
        // CRITICAL: Always release wake lock when activity is destroyed
        // This prevents battery drain if app crashes or is killed
        releaseWakeLock()
        
        // Screen Always On flag is automatically cleared when activity is destroyed
        Log.i("FinalBenchmark2", "Activity destroyed - all optimizations cleaned up")
    }
}