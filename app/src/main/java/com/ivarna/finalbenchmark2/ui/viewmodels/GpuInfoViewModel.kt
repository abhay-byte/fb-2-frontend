package com.ivarna.finalbenchmark2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.finalbenchmark2.utils.GpuInfoState
import com.ivarna.finalbenchmark2.utils.GpuInfoUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GpuInfoViewModel(private val gpuInfoUtils: GpuInfoUtils) : ViewModel() {
    
    private val _gpuInfoState = MutableStateFlow<GpuInfoState>(GpuInfoState.Loading)
    val gpuInfoState: StateFlow<GpuInfoState> = _gpuInfoState
    
    init {
        loadGpuInfo()
    }
    
    private fun loadGpuInfo() {
        viewModelScope.launch {
            _gpuInfoState.value = GpuInfoState.Loading
            _gpuInfoState.value = gpuInfoUtils.getGpuInfo()
        }
    }
    
    fun refreshGpuInfo() {
        loadGpuInfo()
    }
}