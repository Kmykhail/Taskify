package com.kote.taskifyapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.util.convertMillisToLocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TaskSummary(
    task: Task,
    checkBox: @Composable () -> Unit,
    textStyleEffect: TextStyle = TextStyle(),
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
    ) {
        checkBox()
        Text(
            text = task.title ?: stringResource(R.string.untitled),
            style = textStyleEffect,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            val taskLocalDate = if (task.date != null) convertMillisToLocalDate(task.date) else null
            if (taskLocalDate != null) {
                Text(
                    text = "${taskLocalDate.format(DateTimeFormatter.ofPattern("MMM"))} ${taskLocalDate.dayOfMonth}",
                    fontSize = 12.sp,
                )
            }
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Clock",
                tint = if (task.time != null) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                modifier = Modifier
                    .size(16.dp)
            )
        }
    }
}