package com.kote.taskifyapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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

@Composable
fun HomeTopBar(
    onSortChange: (SortType) -> Unit,
    onSwitchView: () -> Unit,
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
        Text(text = "Today") // TODO
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
                    text = { Text("Sort by title") },
                    onClick = {
                        onSortChange(SortType.TITLE)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Sort by date") },
                    onClick = {
                        onSortChange(SortType.DATE)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Sort by priority") },
                    onClick = {
                        onSortChange(SortType.PRIORITY)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("View") },
                    onClick = {
                        onSwitchView()
                        expanded = false
                    }
                )
            }
        }
    }
}
