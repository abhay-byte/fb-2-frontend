package com.ivarna.finalbenchmark2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.finalbenchmark2.data.database.entities.BenchmarkWithCpuData
import com.ivarna.finalbenchmark2.data.repository.HistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log

import com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkResult

data class HistoryUiModel(
    val id: Long,
    val timestamp: Long,
    val finalScore: Double,
    val singleCoreScore: Double,
    val multiCoreScore: Double,
    val testName: String,
    val normalizedScore: Double = 0.0,
    val detailedResults: List<BenchmarkResult> = emptyList()
)

enum class HistorySort { DATE_NEWEST, DATE_OLDEST, SCORE_HIGH_TO_LOW, SCORE_LOW_TO_HIGH }

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {
    
    private val _selectedCategory = MutableStateFlow("All")
    private val _sortOption = MutableStateFlow(HistorySort.DATE_NEWEST)
    
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    val sortOption: StateFlow<HistorySort> = _sortOption.asStateFlow()
    
    val uiState: StateFlow<List<HistoryUiModel>> = combine(
        repository.getAllResults(),
        _selectedCategory,
        _sortOption
    ) { rawList, category, sort ->
        var list = rawList.map { benchmark ->
            // Parse detailed results from JSON string if available
            val detailedResults = try {
                if (benchmark.benchmarkResult.detailedResultsJson.isNotEmpty()) {
                    val gson = Gson()
                    val listType = object : TypeToken<List<com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkResult>>() {}.type
                    gson.fromJson(benchmark.benchmarkResult.detailedResultsJson, listType) as List<com.ivarna.finalbenchmark2.cpuBenchmark.BenchmarkResult>
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("HistoryViewModel", "Error parsing detailed results JSON: ${e.message}")
                emptyList()
            }
            
            HistoryUiModel(
                id = benchmark.benchmarkResult.id,
                timestamp = benchmark.benchmarkResult.timestamp,
                finalScore = benchmark.benchmarkResult.totalScore,
                singleCoreScore = benchmark.benchmarkResult.singleCoreScore,
                multiCoreScore = benchmark.benchmarkResult.multiCoreScore,
                testName = benchmark.benchmarkResult.type,
                normalizedScore = benchmark.benchmarkResult.normalizedScore,
                detailedResults = detailedResults
            )
        }
        
        // 1. Filter
        if (category != "All") {
            list = list.filter { it.testName.contains(category, ignoreCase = true) }
        }
        
        // 2. Sort
        list = when(sort) {
            HistorySort.DATE_NEWEST -> list.sortedByDescending { it.timestamp }
            HistorySort.DATE_OLDEST -> list.sortedBy { it.timestamp }
            HistorySort.SCORE_HIGH_TO_LOW -> list.sortedByDescending { it.finalScore }
            HistorySort.SCORE_LOW_TO_HIGH -> list.sortedBy { it.finalScore }
        }
        
        list
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    fun updateCategory(category: String) {
        _selectedCategory.value = category
    }
    
    fun updateSortOption(sort: HistorySort) {
        _sortOption.value = sort
    }
    
    fun deleteResult(id: Long) {
        viewModelScope.launch {
            repository.deleteResultById(id)
        }
    }
    
    fun deleteAllResults() {
        viewModelScope.launch {
            repository.deleteAllResults()
        }
    }
    
    fun getBenchmarkDetail(id: Long) = repository.getResultById(id)
}