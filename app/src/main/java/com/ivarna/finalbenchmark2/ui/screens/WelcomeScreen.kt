package com.ivarna.finalbenchmark2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.ivarna.finalbenchmark2.R
import com.ivarna.finalbenchmark2.utils.OnboardingPreferences

@Composable
fun WelcomeScreen(
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val onboardingPreferences = remember { OnboardingPreferences(context) }
    MaterialTheme {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Central content - centered vertically
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    // Mascot Image
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.mascot),
                        contentDescription = "App Mascot",
                        modifier = Modifier.size(200.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Headline Text
                    Text(
                        text = "Welcome to FinalBenchmark 2",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Subtitle Text
                    Text(
                        text = "The ultimate tool to push your device to its limits.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
                
                // Action Button - anchored at bottom
                Button(
                    onClick = {
                        // Don't mark onboarding as completed yet - let root check screen handle it
                        onNextClicked()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Start Benchmarking",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}