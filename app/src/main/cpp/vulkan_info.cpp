#include <jni.h>
#include <string>
#include <vulkan/vulkan.h>
#include <sstream>
#include <vector>
#include <android/log.h>
#include <iomanip>
#include <android_native_app_glue.h>

// Macro for logging
#define LOG_TAG "VulkanNative"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Helper: API Version to String
std::string apiVersionToString(uint32_t version) {
    uint32_t major = VK_VERSION_MAJOR(version);
    uint32_t minor = VK_VERSION_MINOR(version);
    uint32_t patch = VK_VERSION_PATCH(version);
    std::stringstream ss;
    ss << major << "." << minor << "." << patch;
    return ss.str();
}

// Helper: Driver Version to String
std::string driverVersionToString(uint32_t version, uint32_t vendorId) {
    uint32_t major = (version >> 22) & 0x3ff;
    uint32_t minor = (version >> 12) & 0x3ff;
    uint32_t patch = version & 0xfff;
    std::stringstream ss;
    ss << major << "." << minor << "." << patch;
    return ss.str();
}

// Helper: Memory Flags to String
std::string memoryHeapFlagsToString(VkMemoryHeapFlags flags) {
    std::vector<std::string> flagStrings;
    if (flags & VK_MEMORY_HEAP_DEVICE_LOCAL_BIT) flagStrings.push_back("DEVICE_LOCAL");
    if (flags & VK_MEMORY_HEAP_MULTI_INSTANCE_BIT) flagStrings.push_back("MULTI_INSTANCE");
    
    std::stringstream ss;
    for (size_t i = 0; i < flagStrings.size(); ++i) {
        if (i > 0) ss << ", ";
        ss << flagStrings[i];
    }
    return ss.str();
}

// Helper: Device Type to String
std::string deviceTypeToString(VkPhysicalDeviceType type) {
    switch (type) {
        case VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU: return "Integrated GPU";
        case VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU: return "Discrete GPU";
        case VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU: return "Virtual GPU";
        case VK_PHYSICAL_DEVICE_TYPE_CPU: return "CPU";
        default: return "Other";
    }
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_ivarna_finalbenchmark2_utils_VulkanNativeBridge_getVulkanInfoNative(JNIEnv* env, jclass clazz) {
    // 1. Initialize Vulkan Instance
    VkApplicationInfo appInfo = {};
    appInfo.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
    appInfo.pApplicationName = "FinalBenchmark2";
    appInfo.apiVersion = VK_API_VERSION_1_0;

    VkInstanceCreateInfo createInfo = {};
    createInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    createInfo.pApplicationInfo = &appInfo;
    
    VkInstance instance;
    if (vkCreateInstance(&createInfo, nullptr, &instance) != VK_SUCCESS) {
        return env->NewStringUTF("{\"supported\": false, \"error\": \"Failed to create Vulkan instance\"}");
    }

    // 2. Enumerate Devices
    uint32_t deviceCount = 0;
    vkEnumeratePhysicalDevices(instance, &deviceCount, nullptr);
    if (deviceCount == 0) {
        vkDestroyInstance(instance, nullptr);
        return env->NewStringUTF("{\"supported\": true, \"error\": \"No Vulkan devices found\"}");
    }

    std::vector<VkPhysicalDevice> devices(deviceCount);
    vkEnumeratePhysicalDevices(instance, &deviceCount, devices.data());
    VkPhysicalDevice device = devices[0]; // Use first device

    // 3. Get Properties & Features
    VkPhysicalDeviceProperties deviceProps;
    vkGetPhysicalDeviceProperties(device, &deviceProps);
    
    VkPhysicalDeviceMemoryProperties memoryProps;
    vkGetPhysicalDeviceMemoryProperties(device, &memoryProps);
    
    VkPhysicalDeviceFeatures deviceFeatures;
    vkGetPhysicalDeviceFeatures(device, &deviceFeatures); // Valid for Vulkan 1.0 features only
    
    // Extensions
    uint32_t deviceExtCount = 0;
    vkEnumerateDeviceExtensionProperties(device, nullptr, &deviceExtCount, nullptr);
    std::vector<VkExtensionProperties> deviceExtensions(deviceExtCount);
    if (deviceExtCount > 0) {
        vkEnumerateDeviceExtensionProperties(device, nullptr, &deviceExtCount, deviceExtensions.data());
    }
    
    // 4. Build JSON
    std::stringstream json;
    json << "{";
    json << "\"supported\": true, ";
    json << "\"apiVersion\": \"" << apiVersionToString(appInfo.apiVersion) << "\", ";
    json << "\"driverVersion\": \"" << driverVersionToString(deviceProps.driverVersion, deviceProps.vendorID) << "\", ";
    json << "\"physicalDeviceName\": \"" << deviceProps.deviceName << "\", ";
    json << "\"physicalDeviceType\": \"" << deviceTypeToString(deviceProps.deviceType) << "\", ";
    json << "\"vendorId\": " << deviceProps.vendorID << ", ";
    
    // Memory Heaps
    json << "\"memoryHeaps\": [";
    for (uint32_t i = 0; i < memoryProps.memoryHeapCount; ++i) {
        if (i > 0) json << ", ";
        json << "{ \"size\": " << memoryProps.memoryHeaps[i].size << ", \"flags\": \"" << memoryHeapFlagsToString(memoryProps.memoryHeaps[i].flags) << "\"}";
    }
    json << "], ";
    
    // Extensions
    json << "\"deviceExtensions\": [";
    for (uint32_t i = 0; i < deviceExtCount; ++i) {
        if (i > 0) json << ", ";
        json << "\"" << deviceExtensions[i].extensionName << "\"";
    }
    json << "], ";
    
    // Features (REMOVED BROKEN FIELDS HERE)
    json << "\"features\": {";
    json << "\"geometryShader\": " << (deviceFeatures.geometryShader ? "true" : "false") << ", ";
    json << "\"tessellationShader\": " << (deviceFeatures.tessellationShader ? "true" : "false") << ", ";
    json << "\"multiViewport\": " << (deviceFeatures.multiViewport ? "true" : "false") << ", ";
    json << "\"textureCompressionETC2\": " << (deviceFeatures.textureCompressionETC2 ? "true" : "false") << ", ";
    json << "\"textureCompressionASTC_LDR\": " << (deviceFeatures.textureCompressionASTC_LDR ? "true" : "false") << ", ";
    json << "\"textureCompressionBC\": " << (deviceFeatures.textureCompressionBC ? "true" : "false");
    json << "}"; // End features
    
    json << "}"; // End root object

    vkDestroyInstance(instance, nullptr);
    return env->NewStringUTF(json.str().c_str());
}