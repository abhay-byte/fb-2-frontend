package com.ivarna.finalbenchmark2.utils

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

/**
 * Native bridge for Vulkan information extraction using JNI
 */
class VulkanNativeBridge {
    companion object {
        private const val TAG = "VulkanNativeBridge"
        
        // Load the native library
        private var isLibraryLoaded = false // Add this flag

        init {
            try {
                System.loadLibrary("vulkan_native")
                isLibraryLoaded = true // Mark as success
            } catch (e: UnsatisfiedLinkError) {
                Log.e(TAG, "Failed to load vulkan_native native library", e)
                isLibraryLoaded = false
            }
        }
        
        /**
         * Get Vulkan information from native code
         */
        @JvmStatic
        external fun getVulkanInfoNative(): String
        
        /**
         * Fallback Vulkan information when native library is not available
         */
        private fun getVulkanInfoFallback(): VulkanInfo {
            // Return a basic VulkanInfo with supported = false to indicate lack of detailed info
            return VulkanInfo(
                supported = false, // Assume not supported if native library fails
                apiVersion = null,
                driverVersion = null,
                physicalDeviceName = null,
                physicalDeviceType = null,
                instanceExtensions = emptyList(),
                deviceExtensions = emptyList(),
                features = null,
                memoryHeaps = null
            )
        }
    }
    
    /**
     * Get detailed Vulkan information by calling the native function and parsing the result
     */
    fun getVulkanInfo(): VulkanInfo {
        // 1. Check if library failed to load
        if (!isLibraryLoaded) {
            Log.e(TAG, "Aborting: Native library not loaded")
            return getVulkanInfoFallback() // This causes "Supported: No"
        }

        return try {
            val jsonString = getVulkanInfoNative()
            Log.d(TAG, "Vulkan info JSON: $jsonString")
            
            val jsonObject = JSONObject(jsonString)
            
            // Extract basic properties
            val supported = jsonObject.optBoolean("supported", false)
            
            if (!supported) {
                val error = jsonObject.optString("error", "Unknown error")
                Log.w(TAG, "Vulkan not supported: $error")
                return VulkanInfo(
                    supported = false,
                    apiVersion = null,
                    driverVersion = null,
                    physicalDeviceName = null,
                    physicalDeviceType = null,
                    instanceExtensions = emptyList(),
                    deviceExtensions = emptyList(),
                    features = null,
                    memoryHeaps = null
                )
            }
            
            // Extract properties when Vulkan is supported
            val apiVersion: String? = jsonObject.optString("apiVersion", null)
            val driverVersion: String? = jsonObject.optString("driverVersion", null)
            val physicalDeviceName: String? = jsonObject.optString("physicalDeviceName", null)
            val physicalDeviceType: String? = jsonObject.optString("physicalDeviceType", null)
            
            // Extract extensions
            val instanceExtensions = mutableListOf<String>()
            val instanceExtensionsArray = jsonObject.optJSONArray("instanceExtensions")
            if (instanceExtensionsArray != null) {
                for (i in 0 until instanceExtensionsArray.length()) {
                    try {
                        val extension = instanceExtensionsArray.getString(i)
                        if (extension != null) {
                            instanceExtensions.add(extension)
                        }
                    } catch (e: Exception) {
                        // Skip invalid extension entry
                        Log.w(TAG, "Invalid extension at index $i", e)
                    }
                }
            }
            
            // Extract device extensions
            val deviceExtensions = mutableListOf<String>()
            val deviceExtensionsArray = jsonObject.optJSONArray("deviceExtensions")
            if (deviceExtensionsArray != null) {
                for (i in 0 until deviceExtensionsArray.length()) {
                    try {
                        val extension = deviceExtensionsArray.getString(i)
                        if (extension != null) {
                            deviceExtensions.add(extension)
                        }
                    } catch (e: Exception) {
                        // Skip invalid extension entry
                        Log.w(TAG, "Invalid device extension at index $i", e)
                    }
                }
            }
            
            // Extract memory heaps
            val memoryHeaps = mutableListOf<VulkanMemoryHeap>()
            val memoryHeapsArray = jsonObject.optJSONArray("memoryHeaps")
            if (memoryHeapsArray != null) {
                for (i in 0 until memoryHeapsArray.length()) {
                    try {
                        val heapObj = memoryHeapsArray.getJSONObject(i)
                        if (heapObj != null) {
                            memoryHeaps.add(
                                VulkanMemoryHeap(
                                    index = i,
                                    size = heapObj.optLong("size", 0L),
                                    flags = heapObj.optString("flags", "UNKNOWN")
                                )
                            )
                        }
                    } catch (e: Exception) {
                        // Skip invalid heap entry
                        Log.w(TAG, "Invalid memory heap at index $i", e)
                    }
                }
            }
            
            // Extract features
            val featuresObj = jsonObject.optJSONObject("features")
            val features = if (featuresObj != null) {
                VulkanFeatures(
                    geometryShader = featuresObj.optBoolean("geometryShader", false),
                    tessellationShader = featuresObj.optBoolean("tessellationShader", false),
                    multiViewport = featuresObj.optBoolean("multiViewport", false),
                    sparseBinding = featuresObj.optBoolean("sparseBinding", false),
                    variableMultisampleRate = featuresObj.optBoolean("variableMultisampleRate", false),
                    protectedMemory = featuresObj.optBoolean("protectedMemory", false),
                    samplerYcbcrConversion = featuresObj.optBoolean("samplerYcbcrConversion", false),
                    shaderDrawParameters = featuresObj.optBoolean("shaderDrawParameters", false)
                )
            } else {
                null
            }
            
            VulkanInfo(
                supported = true,
                apiVersion = apiVersion,
                driverVersion = driverVersion,
                physicalDeviceName = physicalDeviceName,
                physicalDeviceType = physicalDeviceType,
                instanceExtensions = instanceExtensions,
                deviceExtensions = deviceExtensions,
                features = features,
                memoryHeaps = memoryHeaps
            )
        } catch (e: UnsatisfiedLinkError) {
            Log.w(TAG, "Native library not available, using fallback", e)
            // Return fallback info when native library is not available
            getVulkanInfoFallback()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting Vulkan info", e)
            // Return fallback info when there's an error parsing the JSON
            getVulkanInfoFallback()
        }
    }
}