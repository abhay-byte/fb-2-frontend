package com.ivarna.finalbenchmark2.ui.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.finalbenchmark2.data.providers.DeviceInfoProvider
import com.ivarna.finalbenchmark2.domain.model.ItemValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeviceInfoViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(DeviceInfoUiState())
    val uiState: StateFlow<DeviceInfoUiState> = _uiState
    
    private val deviceInfoProvider = DeviceInfoProvider()
    
    fun loadDeviceInfo(context: Context) {
        viewModelScope.launch {
            try {
                val deviceInfo = deviceInfoProvider.getData(context)
                _uiState.value = DeviceInfoUiState(
                    deviceInfoItems = deviceInfo,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = DeviceInfoUiState(
                    deviceInfoItems = emptyList(),
                    isLoading = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }
    
    data class DeviceInfoUiState(
        val deviceInfoItems: List<ItemValue> = emptyList(),
        val isLoading: Boolean = true,
        val error: String? = null
    )
}