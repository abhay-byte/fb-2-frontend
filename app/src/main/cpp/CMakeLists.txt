cmake_minimum_required(VERSION 3.22.1)

project("vulkan-native")

# Add library
add_library(
    vulkan_native
    SHARED
    vulkan_info.cpp
    cpu_info.cpp  # Added CPU info native implementation
)

# Find required libraries
find_library(
    log-lib
    log
)

find_library(
    android-lib
    android
)

# Find and link Vulkan library
find_library(
    vulkan-lib
    vulkan
)

# Check if Vulkan library exists
if(NOT vulkan-lib)
    message(WARNING "Vulkan library not found. This is expected on some Android devices.")
endif()

# Link libraries to the native library
target_link_libraries(
    vulkan_native
    ${log-lib}
    ${android-lib}
    ${vulkan-lib}
)

# Set include directories
target_include_directories(
    vulkan_native
    PRIVATE
    ${ANDROID_NDK}/sources/android/native_app_glue
)