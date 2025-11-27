package com.ivarna.finalbenchmark2.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

@Composable
fun FrostedGlassNavigationBar(
    items: List<BottomNavigationItem>,
    navController: NavHostController,
    hazeState: HazeState, // Pass this from parent
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Get color in Composable context - ensure it updates with theme changes
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val blurBackgroundColor = remember(surfaceColor) {
        // Use a semi-transparent version of the surface color for the blur effect
        // This ensures the haze material adapts to both light and dark themes
        surfaceColor.copy(alpha = 0.85f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp) // Standard NavigationBar height
            .hazeChild(state = hazeState) {
                // Apply haze effect to the entire navigation bar area
                backgroundColor = blurBackgroundColor
                blurRadius = 20.dp
                noiseFactor = 0.1f
            }
    ) {
        NavigationBar(
            containerColor = Color.Transparent, // Must be transparent to see blur
            contentColor = onSurfaceColor, // Set content color to match theme
            tonalElevation = 0.dp,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    },
                    icon = {
                        when (item.route) {
                            "device" -> Icon(
                                painter = painterResource(id = com.ivarna.finalbenchmark2.R.drawable.mobile_24),
                                contentDescription = item.label,
                                tint = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            "history" -> Icon(
                                painter = painterResource(id = com.ivarna.finalbenchmark2.R.drawable.history_24),
                                contentDescription = item.label,
                                tint = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            else -> Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.label,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    alwaysShowLabel = true
                )
            }
        }
    }
}

// In your parent composable (where you use Scaffold):
/*
@Composable
fun MainScreen() {
    val hazeState = remember { HazeState() }
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            FrostedGlassNavigationBar(
                items = navigationItems,
                navController = navController,
                hazeState = hazeState
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState) // Add this to content that should be blurred
        ) {
            NavHost(
                navController = navController,
                startDestination = "device",
                modifier = Modifier.padding(padding)
            ) {
                // Your navigation routes
            }
        }
    }
}
*/