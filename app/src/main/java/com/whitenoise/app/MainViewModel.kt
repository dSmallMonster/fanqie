package com.whitenoise.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for managing UI state and audio playback
 */
class MainViewModel : ViewModel() {
    
    private val audioManager = AudioManager()
    
    val isPlaying: StateFlow<Boolean> = audioManager.isPlaying
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    val currentNoiseType: StateFlow<NoiseType> = audioManager.currentNoiseType
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NoiseType.WHITE
        )
    
    /**
     * Toggle play/pause
     */
    fun togglePlayPause() {
        audioManager.togglePlayPause()
    }
    
    /**
     * Change noise type
     */
    fun setNoiseType(noiseType: NoiseType) {
        audioManager.setNoiseType(noiseType)
    }
    
    /**
     * Clean up resources when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}
