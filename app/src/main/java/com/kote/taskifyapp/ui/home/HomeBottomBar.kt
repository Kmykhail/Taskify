package com.kote.taskifyapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.ui.navigation.UserHomeScreens

@Composable
fun HomeBottomBar(
    clickableScreen: UserHomeScreens,
    onClick: (UserHomeScreens) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(
            onClick = {
                onClick(UserHomeScreens.TASKS)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Task,
                contentDescription = "Home",
                tint = if (clickableScreen == UserHomeScreens.TASKS) Color.Blue else Color.Gray,
                modifier = Modifier.size(26.dp)
            )
        }
        IconButton(
            onClick = {
                onClick(UserHomeScreens.CALENDAR)
            }
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "Calendar",
                tint = if (clickableScreen == UserHomeScreens.CALENDAR) Color.Blue else Color.Gray,
                modifier = Modifier.size(26.dp)
            )
        }
        IconButton(
            onClick = {
                onClick(UserHomeScreens.SETTINGS)
          }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = if (clickableScreen == UserHomeScreens.SETTINGS) Color.Blue else Color.Gray,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}