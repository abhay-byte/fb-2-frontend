package com.ivarna.finalbenchmark2.ui.screens

import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.ivarna.finalbenchmark2.utils.OnboardingPreferences

sealed interface PermissionUiState {
    object Checking : PermissionUiState
    object NotGranted : PermissionUiState
    object Granted : PermissionUiState
    object NotRequired : PermissionUiState
}

@Composable
fun PermissionsScreen(
    onNextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var permissionUiState by remember { mutableStateOf<PermissionUiState>(PermissionUiState.Checking) }
    val context = LocalContext.current
    val onboardingPreferences = remember { OnboardingPreferences(context) }
    
    // Check permission status when screen loads
    val hasPermissionChecked = remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        if (!hasPermissionChecked.value) {
            // Check permission status
            permissionUiState = if (Build.VERSION.SDK_INT >= 33) {
                val hasPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
                
                if (hasPermission) {
                    PermissionUiState.Granted
                } else {
                    PermissionUiState.NotGranted
                }
            } else {
                // Android < 13 doesn't require this permission
                PermissionUiState.NotRequired
            }
            hasPermissionChecked.value = true
        }
    }
    
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
                    // Notifications Icon
                    Icon(
                        imageVector = Icons.Rounded.Notifications,
                        contentDescription = "Notifications Permission",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Headline Text
                    Text(
                        text = "Stay Running",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Subtext
                    Text(
                        text = "Benchmarks are heavy operations. Without proper permissions, the OS might kill the app before tests finish.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Explanation Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Text(
                                text = "Why do we need this?",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Start
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "We use a Foreground Service to mark the benchmark process as 'User Visible'. This prevents Android from killing the app to save battery while high-intensity tests are running. This requires Notification permissions to display the 'Benchmark Running' status.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Permission Status and Action
                    when (permissionUiState) {
                        is PermissionUiState.Checking -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        is PermissionUiState.NotGranted -> {
                            val permissionLauncher = rememberLauncherForActivityResult(
                                ActivityResultContracts.RequestPermission()
                            ) { isGranted ->
                                permissionUiState = if (isGranted) {
                                    PermissionUiState.Granted
                                } else {
                                    PermissionUiState.NotGranted
                                }
                            }
                            
                            Button(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Allow Notifications",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        
                        is PermissionUiState.Granted -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Notifications,
                                    contentDescription = "Permission Granted",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(24.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = "Permission Granted",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                        
                        is PermissionUiState.NotRequired -> {
                            Text(
                                text = "Auto-Granted (Android < 13)",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                // Action Button - anchored at bottom
                Button(
                    onClick = {
                        onNextClicked()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}