package com.kote.taskifyapp.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
        Priority.NoPriority to Color.Gray
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
                            Priority.High -> "High priority"
                            Priority.Medium -> "Medium priority"
                            Priority.Low -> "Low priority"
                            Priority.NoPriority -> "No priority"
                        }
                    )
               },
                leadingIcon = { Icon(Icons.Outlined.Flag, contentDescription = priority.first.toString(), tint = priority.second) },
                onClick = {
                    onPriorityChange(priority.first)
                    onDismissRequest(false)
                }
            )
        }
    }
}