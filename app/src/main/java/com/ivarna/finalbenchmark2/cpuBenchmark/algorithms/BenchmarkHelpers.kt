package com.ivarna.finalbenchmark2.cpuBenchmark.algorithms

import java.util.concurrent.ThreadLocalRandom

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
}
