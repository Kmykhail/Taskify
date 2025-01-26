package com.kote.taskifyapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DashboardCustomize
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeBottomBar(
    userHomeScreen: UserHomeScreens,
    onHomeScreenClick: (UserHomeScreens) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = { onHomeScreenClick(UserHomeScreens.TASKS) }
        ) {
            Icon(
                imageVector = Icons.Filled.DashboardCustomize,
                contentDescription = "Home",
                tint = if (userHomeScreen == UserHomeScreens.TASKS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(26.dp)
            )
        }
        IconButton(
            onClick = { onHomeScreenClick(UserHomeScreens.CALENDAR) }
        ) {
            Icon(
                imageVector = Icons.Default.Today,
                contentDescription = "Calendar",
                tint = if (userHomeScreen == UserHomeScreens.CALENDAR) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(26.dp)
            )
        }
        IconButton(
            onClick = { onHomeScreenClick(UserHomeScreens.SETTINGS) }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = if (userHomeScreen == UserHomeScreens.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}