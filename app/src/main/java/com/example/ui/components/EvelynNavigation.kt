package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Opacity
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PeonyDark
import com.example.ui.theme.PeonyLight
import com.example.ui.theme.PeonyRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class EvelynScreen(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val inBottomBar: Boolean
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, true),
    CALENDAR("Calendar", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth, true),
    LOG_PERIOD("Log Period", Icons.Filled.Opacity, Icons.Outlined.Opacity, true),
    FERTILITY("Fertility", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, true),
    SYMPTOMS("Symptoms", Icons.Filled.EditNote, Icons.Outlined.EditNote, true),
    STATISTICS("Statistics", Icons.Filled.BarChart, Icons.Outlined.BarChart, false),
    NOTIFICATIONS("Notifications", Icons.Filled.Notifications, Icons.Outlined.Notifications, false),
    SETTINGS("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, false);
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvelynTopBar(
    currentScreen: EvelynScreen,
    onNavigate: (EvelynScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.testTag("evelyn_top_bar"),
        title = {
            Text(
                text = if (currentScreen == EvelynScreen.HOME) "Evelyn Cycle" else currentScreen.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PeonyDark
                )
            )
        },
        navigationIcon = {
            if (!currentScreen.inBottomBar) {
                IconButton(
                    onClick = { onNavigate(EvelynScreen.HOME) },
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PeonyDark
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = { onNavigate(EvelynScreen.STATISTICS) },
                modifier = Modifier.testTag("top_action_statistics")
            ) {
                Icon(
                    imageVector = if (currentScreen == EvelynScreen.STATISTICS) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                    contentDescription = "Statistics",
                    tint = if (currentScreen == EvelynScreen.STATISTICS) PeonyRose else TextSecondary
                )
            }
            IconButton(
                onClick = { onNavigate(EvelynScreen.NOTIFICATIONS) },
                modifier = Modifier.testTag("top_action_notifications")
            ) {
                Icon(
                    imageVector = if (currentScreen == EvelynScreen.NOTIFICATIONS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = if (currentScreen == EvelynScreen.NOTIFICATIONS) PeonyRose else TextSecondary
                )
            }
            IconButton(
                onClick = { onNavigate(EvelynScreen.SETTINGS) },
                modifier = Modifier.testTag("top_action_settings")
            ) {
                Icon(
                    imageVector = if (currentScreen == EvelynScreen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings & Privacy",
                    tint = if (currentScreen == EvelynScreen.SETTINGS) PeonyRose else TextSecondary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun EvelynBottomBar(
    currentScreen: EvelynScreen,
    onNavigate: (EvelynScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val bottomScreens = EvelynScreen.values().filter { it.inBottomBar }

    NavigationBar(
        modifier = modifier.testTag("evelyn_bottom_nav"),
        windowInsets = WindowInsets.navigationBars,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = TextPrimary,
        tonalElevation = 1.dp
    ) {
        bottomScreens.forEach { screen ->
            val selected = currentScreen == screen
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(screen) },
                icon = {
                    Icon(
                        imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PeonyDark,
                    selectedTextColor = PeonyDark,
                    indicatorColor = PeonyLight,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("bottom_nav_${screen.name.lowercase()}")
            )
        }
    }
}
