package com.ivarna.finalbenchmark2.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
        surfaceColor.copy(alpha = 0.6f)
    }

    Box(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .fillMaxWidth()
            .height(72.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(percent = 50),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(percent = 50))
            .hazeChild(state = hazeState) {
                // Apply haze effect to the entire navigation bar area
                backgroundColor = blurBackgroundColor
                blurRadius = 20.dp
                noiseFactor = 0.1f
            }
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(percent = 50)
            )
    ) {
        NavigationBar(
            containerColor = Color.Transparent, // Must be transparent to see blur
            contentColor = onSurfaceColor, // Set content color to match theme
            tonalElevation = 0.dp,
            windowInsets = WindowInsets(0),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    selected = isSelected,
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    ),
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
                        Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                            }
                            
                            val iconTint = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                            // Using Primary for icon because the glow is background. 
                            // If glow is transparent-ish, Primary tint looks like "lit up".
                            // If glow is strong, use onPrimary. 
                            // User said: "only icon should lit up". "Add underglow".
                            // Image 2 shows Black icon on bright glow.
                            // My glow is primary (colored). 
                            // I will stick to Primary tint for the icon itself to ensure it looks "lit", 
                            // or onPrimary if primary is dark. 
                            // Let's use MaterialTheme.colorScheme.primary for the "lit up" look + Glow.
                            // Actually, if background is Primary(0.5), Primary icon might blend.
                            // Let's use `onSurface` (White) or `primary` (Bright).
                            // I'll use `primary` for consistency with "lit up".

                            when (item.route) {
                                "device" -> Icon(
                                    painter = painterResource(id = com.ivarna.finalbenchmark2.R.drawable.mobile_24),
                                    contentDescription = item.label,
                                    tint = iconTint
                                )
                                "history" -> Icon(
                                    painter = painterResource(id = com.ivarna.finalbenchmark2.R.drawable.history_24),
                                    contentDescription = item.label,
                                    tint = iconTint
                                )
                                else -> Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = iconTint
                                )
                            }
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