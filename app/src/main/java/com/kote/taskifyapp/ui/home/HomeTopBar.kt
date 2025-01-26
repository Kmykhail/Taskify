package com.kote.taskifyapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun HomeTopBar(
    taskFilterType: TaskFilterType,
    onSortChange: (SortType) -> Unit,
    onFiltrationChange: (TaskFilterType) -> Unit,
    onSwitchView: () -> Unit,
    onOpenSidePanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var pendingFilterUpdate by remember { mutableStateOf<TaskFilterType?>(null) }

    LaunchedEffect(expanded) {
        if (!expanded && pendingFilterUpdate != null) {
            delay(50)
            onFiltrationChange(pendingFilterUpdate!!)
            pendingFilterUpdate = null
        }
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = {onOpenSidePanel()}) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(24.dp)
            )
        }
        Text(text = "Today") // TODO
        Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
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
//                if (taskFilterType == TaskFilterType.SHOW_ACTIVE) {
//                    DropdownMenuItem(
//                        text = { Text("Show completed") },
//                        leadingIcon = { Icon(imageVector = Icons.Filled.Task, contentDescription = null) },
//                        onClick = {
//                            expanded = false
//                            pendingFilterUpdate = TaskFilterType.SHOW_COMPLETED
//                        }
//                    )
//                } else {
//                    DropdownMenuItem(
//                        text = { Text("Hide completed") },
//                        leadingIcon = { Icon(imageVector = Icons.Outlined.Task, contentDescription = null) },
//                        onClick = {
//                            expanded = false
//                            pendingFilterUpdate = TaskFilterType.SHOW_ACTIVE
//                        }
//                    )
//                }
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
