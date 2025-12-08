package com.ivarna.finalbenchmark2.utils

import android.content.Context

class OnboardingPreferences(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("onboarding_preferences", Context.MODE_PRIVATE)
    
    companion object {
        private const val ONBOARDING_COMPLETED_KEY = "onboarding_completed"
    }
    
    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(ONBOARDING_COMPLETED_KEY, false)
    }
    
    fun setOnboardingCompleted() {
        sharedPreferences.edit().putBoolean(ONBOARDING_COMPLETED_KEY, true).apply()
    }
    
    fun resetOnboarding() {
        sharedPreferences.edit().putBoolean(ONBOARDING_COMPLETED_KEY, false).apply()
    }
}