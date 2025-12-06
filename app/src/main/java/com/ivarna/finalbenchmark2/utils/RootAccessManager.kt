package com.ivarna.finalbenchmark2.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Singleton class to manage root access state across the application
 * This prevents repeated root checks and improves performance
 */
object RootAccessManager {
    private var _isRootAccessChecked = false
    private var _hasRootAccess: Boolean = false
    private val mutex = Mutex()
    
    /**
     * Checks if the device has root access
     * This method will only perform the actual check once and cache the result
     */
    suspend fun hasRootAccess(): Boolean = mutex.withLock {
        if (!_isRootAccessChecked) {
            _hasRootAccess = RootUtils.canExecuteRootCommandRobust()
            _isRootAccessChecked = true
        }
        _hasRootAccess
    }
    
    /**
     * Resets the cached root access state (for testing purposes)
     */
    fun reset() {
        _isRootAccessChecked = false
        _hasRootAccess = false
    }
    
    /**
     * Gets the cached root access state without performing a new check
     */
    fun getCachedRootAccess(): Boolean? {
        return if (_isRootAccessChecked) _hasRootAccess else null
    }
}