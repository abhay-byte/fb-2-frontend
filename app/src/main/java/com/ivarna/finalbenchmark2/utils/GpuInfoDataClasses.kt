package com.ivarna.finalbenchmark2.utils

/**
 * Data classes for GPU information structures
 */

// Sealed class for GPU info loading states
sealed class GpuInfoState {
    object Loading : GpuInfoState()
    data class Success(val gpuInfo: GpuInfo) : GpuInfoState()
    data class Error(val message: String) : GpuInfoState()
}

// Main GPU info container
data class GpuInfo(
    val basicInfo: GpuBasicInfo,
    val frequencyInfo: GpuFrequencyInfo?,
    val openGLInfo: OpenGLInfo?,
    val vulkanInfo: VulkanInfo?
)

// Basic GPU information
data class GpuBasicInfo(
    val name: String,
    val vendor: String,
    val driverVersion: String,
    val openGLVersion: String,
    val vulkanVersion: String? // null if not supported
)

// GPU frequency information
data class GpuFrequencyInfo(
    val currentFrequency: Long?, // in MHz
    val maxFrequency: Long?     // in MHz
)

// OpenGL information
data class OpenGLInfo(
    val version: String,
    val glslVersion: String,
    val extensions: List<String>,
    val capabilities: OpenGLCapabilities
)

// OpenGL capabilities
data class OpenGLCapabilities(
    val maxTextureSize: Int,
    val maxViewportWidth: Int,
    val maxViewportHeight: Int,
    val maxFragmentUniformVectors: Int,
    val maxVertexAttributes: Int,
    val maxRenderbufferSize: Int,
    val supportedTextureCompressionFormats: List<String>
)

// Vulkan information
data class VulkanInfo(
    val supported: Boolean,
    val apiVersion: String?,
    val driverVersion: String?,
    val physicalDeviceName: String?,
    val physicalDeviceType: String?,
    val instanceExtensions: List<String>,
    val deviceExtensions: List<String>,
    val features: VulkanFeatures?,
    val memoryHeaps: List<VulkanMemoryHeap>?
)

// Vulkan features
data class VulkanFeatures(
    val geometryShader: Boolean,
    val tessellationShader: Boolean,
    val multiViewport: Boolean,
    val sparseBinding: Boolean,
    val variableMultisampleRate: Boolean,
    val protectedMemory: Boolean,
    val samplerYcbcrConversion: Boolean,
    val shaderDrawParameters: Boolean
)

// Vulkan memory heap
data class VulkanMemoryHeap(
    val index: Int,
    val size: Long, // in bytes
    val flags: String // "DEVICE_LOCAL", "HOST_VISIBLE", etc.
)