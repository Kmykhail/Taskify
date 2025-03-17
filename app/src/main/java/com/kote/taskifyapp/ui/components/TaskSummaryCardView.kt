package com.kote.taskifyapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessAlarms
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.LocalAppLocale
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.util.convertMillisToLocalDate

@Composable
fun TaskSummaryCardView(
    task: Task,
    checkBox: @Composable () -> Unit,
    mainTextStyleEffect: TextStyle = TextStyle(),
    isOverdue: Boolean,
    modifier: Modifier = Modifier
) {
    val locale = LocalAppLocale.current

    Card(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        ) {
            checkBox()
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    task.date?.let { dateMilli ->
                        val taskDate = convertMillisToLocalDate(dateMilli)
                        val dayOfWeek = taskDate.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT_STANDALONE, locale).replaceFirstChar { it.uppercase() }
                        val month = taskDate.month.getDisplayName(java.time.format.TextStyle.SHORT_STANDALONE, locale).replaceFirstChar { it.uppercase() }
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)) {
                                append("$dayOfWeek\n")
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                append("${taskDate.dayOfMonth}\n")
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.Bold)) {
                                append(month)
                            }
                        }
                    } ?: buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                            append(stringResource(R.string.no_date))
                        }
                    }
                )
            }
            VerticalDivider(
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(vertical = 12.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title ?: stringResource(R.string.untitled),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = mainTextStyleEffect
                )
                Text(
                    text = task.description ?: stringResource(R.string.no_description),
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = mainTextStyleEffect
                )
            }
            task.time?.let { timeMilli ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format("%02d:%02d", timeMilli / 60, timeMilli % 60),
                        color = if (isOverdue) Color(0xFFAF2A2A) else MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.AccessAlarms, contentDescription = null,
                        tint = if (isOverdue) Color(0xFFAF2A2A) else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}