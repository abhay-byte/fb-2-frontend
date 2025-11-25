fn main() {
    // This build script is intentionally minimal
    // The library is configured to build both as a binary (main.rs) 
    // and as a library with FFI bindings (ffi.rs)
    
    // For Android NDK builds, we might need to specify additional flags
    // but for now, we just ensure the standard library features are available
    println!("cargo:rerun-if-changed=build.rs");
}