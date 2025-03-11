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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.SortType
import kotlinx.coroutines.delay

@Composable
fun HomeTopBar(
    groupTasksType: GroupTasksType,
    onSortChange: (SortType) -> Unit,
    onFiltrationChange: (GroupTasksType) -> Unit,
    onOpenSidePanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var pendingFilterUpdate by remember { mutableStateOf<GroupTasksType?>(null) }

    LaunchedEffect(expanded) {
        if (!expanded && pendingFilterUpdate != null) {
            delay(50)
            onFiltrationChange(pendingFilterUpdate!!)
            pendingFilterUpdate = null
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {onOpenSidePanel()}) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = when(groupTasksType) {
                    GroupTasksType.ALL -> stringResource(R.string.side_panel_all)
                    GroupTasksType.TODAY -> stringResource(R.string.side_panel_today)
                    GroupTasksType.PLANNED -> stringResource(R.string.side_panel_planned)
                    GroupTasksType.COMPLETED -> stringResource(R.string.side_panel_completed)
                },
                fontWeight = FontWeight.Bold
            )
        }
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
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.title_sort)) },
                    onClick = {
                        onSortChange(SortType.Title)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.date_sort)) },
                    onClick = {
                        onSortChange(SortType.Date)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.priority_sort)) },
                    onClick = {
                        onSortChange(SortType.Priority)
                        expanded = false
                    }
                )
            }
        }
    }
}
