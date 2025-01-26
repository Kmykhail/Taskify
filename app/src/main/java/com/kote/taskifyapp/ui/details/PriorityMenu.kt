package com.kote.taskifyapp.ui.details

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.Priority

@Composable
fun PriorityMenu(
    onDismissRequest: (Boolean) -> Unit,
    onPriorityChange: (Priority) -> Unit
) {
    val priorityList = listOf(
        Priority.High to Color.Red,
        Priority.Medium to Color.Yellow,
        Priority.Low to Color.Green,
        Priority.NoPriority to MaterialTheme.colorScheme.outline
    )
    DropdownMenu(
        expanded = true,
        onDismissRequest = {onDismissRequest(false)}
    ) {
        priorityList.forEach {priority ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = when(priority.first) {
                            Priority.High -> stringResource(R.string.high_priority)
                            Priority.Medium -> stringResource(R.string.medium_priority)
                            Priority.Low -> stringResource(R.string.low_priority)
                            Priority.NoPriority -> stringResource(R.string.no_priority)
                        }
                    )
               },
                leadingIcon = { Icon(Icons.Filled.Flag, contentDescription = priority.first.toString(), tint = priority.second) },
                onClick = {
                    onPriorityChange(priority.first)
                    onDismissRequest(false)
                }
            )
        }
    }
}