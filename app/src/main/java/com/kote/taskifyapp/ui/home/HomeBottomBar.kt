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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.ui.navigation.UserHomeScreens

@Composable
fun HomeBottomBar(
    clickableScreen: MutableState<UserHomeScreens>,
    onCalendarGroupChange: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = { clickableScreen.value = UserHomeScreens.TASKS }
        ) {
            Icon(
                imageVector = Icons.Filled.DashboardCustomize,
                contentDescription = "Home",
                tint = if (clickableScreen.value == UserHomeScreens.TASKS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(26.dp)
            )
        }
        IconButton(
            onClick = {
                onCalendarGroupChange()
                clickableScreen.value = UserHomeScreens.CALENDAR
            }
        ) {
            Icon(
                imageVector = Icons.Default.Today,
                contentDescription = "Calendar",
                tint = if (clickableScreen.value == UserHomeScreens.CALENDAR) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(26.dp)
            )
        }
        IconButton(
            onClick = { clickableScreen.value = UserHomeScreens.SETTINGS }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = if (clickableScreen.value == UserHomeScreens.SETTINGS) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}