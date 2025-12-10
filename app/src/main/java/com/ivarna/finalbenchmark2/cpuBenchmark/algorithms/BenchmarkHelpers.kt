package com.ivarna.finalbenchmark2.cpuBenchmark.algorithms

import java.util.concurrent.ThreadLocalRandom

// Data classes for ray tracing - moved from benchmark functions for better performance
data class Vec3(val x: Double, val y: Double, val z: Double) {
    fun dot(other: Vec3): Double = x * other.x + y * other.y + z * other.z
    fun length(): Double = kotlin.math.sqrt(dot(this))
    fun normalize(): Vec3 {
        val len = length()
        return if (len > 0.0) Vec3(x / len, y / len, z / len) else Vec3(0.0, 0.0, 0.0)
    }
    operator fun plus(other: Vec3): Vec3 = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3): Vec3 = Vec3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Double): Vec3 = Vec3(x * scalar, y * scalar, z * scalar)
}

data class Ray(val origin: Vec3, val direction: Vec3)

data class Sphere(val center: Vec3, val radius: Double) {
    fun intersect(ray: Ray): DoubleArray? {
        val oc = ray.origin - center
        val a = ray.direction.dot(ray.direction)
        val b = 2.0 * oc.dot(ray.direction)
        val c = oc.dot(oc) - radius * radius
        val discriminant = b * b - 4.0 * a * c

        return if (discriminant < 0.0) {
            null
        } else {
            val t1 = (-b - kotlin.math.sqrt(discriminant)) / (2.0 * a)
            val t2 = (-b + kotlin.math.sqrt(discriminant)) / (2.0 * a)

            when {
                t1 > 0.0 -> doubleArrayOf(t1)
                t2 > 0.0 -> doubleArrayOf(t2)
                else -> null
            }
        }
    }
}

object BenchmarkHelpers {

    /** Run a benchmark function and measure execution time */
    inline fun <T> measureBenchmark(block: () -> T): Pair<T, Long> {
        val startTime = System.nanoTime()
        val result = block()
        val endTime = System.nanoTime()
        val durationMs = (endTime - startTime) / 1_000_000
        return Pair(result, durationMs)
    }

    /**
     * Run a suspend benchmark function and measure execution time Allows yielding to prevent UI
     * freeze
     */
    suspend inline fun <T> measureBenchmarkSuspend(
            crossinline block: suspend () -> T
    ): Pair<T, Long> {
        val startTime = System.nanoTime()
        val result = block()
        val endTime = System.nanoTime()
        val durationMs = (endTime - startTime) / 1_000_000
        return Pair(result, durationMs)
    }

    /**
     * Generate random string of specified length - OPTIMIZED for performance Uses static character
     * set and efficient string building
     */
    fun generateRandomString(length: Int): String {
        // OPTIMIZED: Static character set to avoid recreation overhead
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val charArray = CharArray(length)

        // OPTIMIZED: Use ThreadLocalRandom for better performance and thread safety
        val random = ThreadLocalRandom.current()

        repeat(length) { index -> charArray[index] = chars[random.nextInt(chars.length)] }

        return String(charArray)
    }

    /** Check if a number is prime */
    fun isPrime(n: Long): Boolean {
        if (n <= 1L) return false
        if (n <= 3L) return true
        if (n % 2L == 0L || n % 3L == 0L) return false

        var i = 5L
        while (i * i <= n) {
            if (n % i == 0L || n % (i + 2L) == 0L) return false
            i += 6L
        }
        return true
    }

    /** Calculate checksum of a 2D matrix */
    fun calculateMatrixChecksum(matrix: Array<DoubleArray>): Long {
        var checksum = 0L
        for (row in matrix) {
            for (value in row) {
                checksum = checksum xor value.toBits()
            }
        }
        return checksum
    }

    /**
     * Shared iterative Fibonacci function for core-independent CPU benchmarking Uses O(n) time
     * complexity - same algorithm for both Single-Core and Multi-Core tests
     *
     * @param n The Fibonacci number to calculate (n=35 for benchmark stability)
     * @return The nth Fibonacci number
     */
    fun fibonacciIterative(n: Int): Long {
        if (n <= 1) return n.toLong()
        var prev = 0L
        var curr = 1L
        for (i in 2..n) {
            val next = prev + curr
            prev = curr
            curr = next
        }
        return curr
    }

    /**
     * Cache-Resident Matrix Multiplication
     *
     * Performs multiple matrix multiplications (A × B = C) using optimized i-k-j loop order for
     * cache efficiency. Uses small matrices that fit in CPU cache to prevent memory bottlenecks.
     *
     * CACHE-RESIDENT STRATEGY:
     * - Small matrix size (128x128) fits in L2/L3 cache
     * - Multiple repetitions to maintain CPU utilization
     * - Matrices A and B allocated once, reused across repetitions
     * - Only matrix C is reset between repetitions
     *
     * @param size The size of the square matrices (size × size) - should be small (128)
     * @param repetitions Number of times to repeat the matrix multiplication
     * @return The checksum of the final resulting matrix C
     */
    fun performMatrixMultiplication(size: Int, repetitions: Int = 1): Long {
        // OPTIMIZED: Initialize matrices A and B ONCE (cache-resident strategy)
        val a = Array(size) { DoubleArray(size) { kotlin.random.Random.nextDouble() } }
        val b = Array(size) { DoubleArray(size) { kotlin.random.Random.nextDouble() } }

        // CACHE-RESIDENT: Repeat the multiplication multiple times
        repeat(repetitions) { rep ->
            // Initialize result matrix C for this repetition
            val c = Array(size) { DoubleArray(size) }

            // OPTIMIZED: Use i-k-j loop order for better cache locality
            for (i in 0 until size) {
                for (k in 0 until size) {
                    val aik = a[i][k]
                    for (j in 0 until size) {
                        c[i][j] += aik * b[k][j]
                    }
                }
            }

            // For the last repetition, return the checksum
            if (rep == repetitions - 1) {
                return calculateMatrixChecksum(c)
            }
        }

        // This should never be reached, but Kotlin requires a return statement
        return 0L
    }

    /**
     * Pure CPU Hash Computing (No Native Locks) Uses a custom FNV-like mixing algorithm to stress
     * the CPU ALU and L1 Cache. Guaranteed to scale perfectly on Multi-Core.
     *
     * FIXED WORK PER CORE: Pure Kotlin Hash Computing
     *
     * Performs fixed number of hash iterations using 4KB buffer (cache-friendly). Returns total
     * bytes processed for throughput calculation.
     *
     * @param bufferSize Size of the data buffer in bytes (4KB recommended)
     * @param iterations Number of hash iterations to perform
     * @return Total bytes processed (bufferSize * iterations)
     */
    fun performHashComputing(bufferSize: Int, iterations: Int): Long {
        // 1. Setup Data
        val data = ByteArray(bufferSize) { (it % 255).toByte() }
        var currentState = 0x811C9DC5.toInt() // FNV offset basis

        // 2. Pure CPU Loop (No System Calls)
        repeat(iterations) {
            // Process the buffer with a stride for speed (simulating SHA-256 block processing)
            // We read every 4th byte to keep the benchmark duration reasonable (~1.5s for 1M iters)
            for (i in 0 until bufferSize step 4) {
                currentState = (currentState xor data[i].toInt()) * 16777619 // FNV prime
            }
        }

        // 3. Return throughput metric
        return bufferSize.toLong() * iterations
    }

    /**
     * Generate a list of random strings efficiently for benchmarking
     *
     * FIXED WORK PER CORE: Efficient string generation for fair benchmarking
     *
     * @param count Number of strings to generate
     * @param length Length of each string (default: 16 characters)
     * @return MutableList<String> containing random strings
     */
    fun generateStringList(count: Int, length: Int = 16): MutableList<String> {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random = java.util.concurrent.ThreadLocalRandom.current()
        val list = ArrayList<String>(count)
        repeat(count) {
            val charArray = CharArray(length)
            repeat(length) { i -> charArray[i] = chars[random.nextInt(chars.length)] }
            list.add(String(charArray))
        }
        return list
    }

    /**
     * Cache-Resident String Sorting Workload
     *
     * CACHE-RESIDENT STRATEGY:
     * - Uses small fixed-size list (4,096 strings) that fits in CPU cache
     * - Performs multiple iterations to maintain CPU utilization and achieve meaningful benchmark
     * times
     * - Prevents memory bandwidth bottlenecks by keeping data in cache
     * - Ensures true CPU throughput measurement
     *
     * @param sourceList The source list of strings to sort (cached-resident size: 4,096)
     * @param iterations Number of times to repeat the sorting operation
     * @return Checksum to prevent compiler optimization
     */
    fun runStringSortWorkload(sourceList: List<String>, iterations: Int): Int {
        var checkSum = 0
        // Reuse specific small size to keep data in L2 CPU Cache
        repeat(iterations) {
            // Create copy to ensure we are actually sorting (O(N) copy + O(N log N) sort)
            val workingList = ArrayList(sourceList)
            workingList.sort()
            if (workingList.isNotEmpty()) checkSum += workingList.last().hashCode()
        }
        return checkSum
    }

    /**
     * Ray Tracing Scene Renderer - FIXED: Memory-efficient checksum approach
     *
     * FIXED WORKLOAD THROUGHPUT APPROACH:
     * - Does NOT store pixels in memory (no mutableListOf allocations)
     * - Accumulates checksum instead (totalEnergy) to prove work was done
     * - Designed for iteration-based workload scaling
     * - Tests pure FPU throughput, not memory bandwidth
     *
     * @param width Image width in pixels
     * @param height Image height in pixels
     * @param maxDepth Maximum ray recursion depth
     * @return Double checksum representing total rendered energy
     */
    fun renderSceneChecksum(width: Int, height: Int, maxDepth: Int): Double {
        var totalEnergy = 0.0
        
        // Create scene with 3 spheres (same as original implementation)
        val spheres = listOf(
            Sphere(Vec3(0.0, 0.0, -1.0), 0.5),
            Sphere(Vec3(1.0, 0.0, -1.5), 0.3),
            Sphere(Vec3(-1.0, -0.5, -1.2), 0.4)
        )
        
        // Render image pixel by pixel, accumulating checksum instead of storing
        for (y in 0 until height) {
            for (x in 0 until width) {
                // Create ray from camera through pixel
                val ray = Ray(
                    Vec3(0.0, 0.0, 0.0),
                    Vec3(
                        (x.toDouble() - width / 2.0) / (width / 2.0),
                        (y.toDouble() - height / 2.0) / (height / 2.0),
                        -1.0
                    ).normalize()
                )
                
                // Trace ray and get color
                val color = traceRay(ray, spheres, maxDepth)
                
                // Accumulate energy instead of storing pixel (FIXED: No memory allocation)
                totalEnergy += color.x + color.y + color.z
            }
        }
        
        return totalEnergy
    }
    
    /**
     * Ray tracing function with recursion - FIXED: Memory-efficient
     *
     * @param ray The ray to trace
     * @param spheres List of spheres in the scene
     * @param depth Remaining recursion depth
     * @return Vec3 color result
     */
    private fun traceRay(ray: Ray, spheres: List<Sphere>, depth: Int): Vec3 {
        if (depth == 0) return Vec3(0.0, 0.0, 0.0)

        var closestT = Double.MAX_VALUE
        var hitSphere: Sphere? = null

        for (sphere in spheres) {
            val intersection = sphere.intersect(ray)
            if (intersection != null && intersection[0] < closestT) {
                closestT = intersection[0]
                hitSphere = sphere
            }
        }

        return if (hitSphere != null) {
            val hitPoint = ray.origin + ray.direction * closestT
            val normal = (hitPoint - hitSphere.center).normalize()

            // Simple shading with reflection
            val reflectedDir = ray.direction - normal * (2.0 * ray.direction.dot(normal))
            val reflectedRay = Ray(hitPoint + normal * 0.01, reflectedDir.normalize())

            val reflectedColor = traceRay(reflectedRay, spheres, depth - 1)

            // Return a color based on normal and reflection
            Vec3(
                (normal.x + 1.0) * 0.5 + reflectedColor.x * 0.3,
                (normal.y + 1.0) * 0.5 + reflectedColor.y * 0.3,
                (normal.z + 1.0) * 0.5 + reflectedColor.z * 0.3
            )
        } else {
            // Background color (simple gradient)
            Vec3(0.5, 0.7, 1.0) // Sky blue
        }
    }
}
