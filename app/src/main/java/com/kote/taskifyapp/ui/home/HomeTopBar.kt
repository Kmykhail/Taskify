package com.kote.taskifyapp.ui.home

import androidx.compose.animation.expandIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) } // State to manage dropdown visibility
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = "Today", // TODO
        )
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More options",
                    modifier = Modifier.size(24.dp)
                )
            }
                DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Sort Tasks") },
                    onClick = {
                        expanded = false
//                        onSortTasks()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Filter Completed Tasks") },
                    onClick = {
                        expanded = false
//                        onFilterCompletedTasks()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Filter High Priority") },
                    onClick = {
                        expanded = false
//                        onFilterHighPriority()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Switch View") },
                    onClick = {
                        expanded = false
//                        onSwitchView()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Settings") },
                    onClick = {
                        expanded = false
//                        onSettingsShortcut()
                    }
                )
            }
        }
    }
}

@Composable
fun Check1() {

}