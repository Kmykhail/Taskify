package com.kote.taskifyapp.ui.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.ui.theme.TaskifyTheme
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@Composable
fun HomeCalendarView(
    groupedTasks: Map<String, List<Task>>,
    onSelectedDate: (String) -> Unit,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    markAsCompleted: (String, Int) -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2, pageCount = { Int.MAX_VALUE })
    var dateString by remember { mutableStateOf("") }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect {page ->
                val yearMonth = YearMonth.now().plusMonths(page - (Int.MAX_VALUE / 2).toLong())
                dateString = "${yearMonth.month} ${yearMonth.year}"
            }
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(paddingValues)
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = dateString,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 10.dp)
        ) { page ->
            CalendarView(
                month = YearMonth.now().plusMonths(page - (Int.MAX_VALUE / 2).toLong()),
                onSelectedDate = onSelectedDate,
                groupedTasks = groupedTasks,
                onNavigateToTaskDetails = onNavigateToTaskDetails,
                markAsCompleted = markAsCompleted
            )
        }
    }
}

@Composable
private fun CalendarView(
    month: YearMonth,
    onSelectedDate: (String) -> Unit,
    groupedTasks: Map<String, List<Task>>,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    markAsCompleted: (String, Int) -> Unit,
) {
    val now = LocalDate.now()
    val daysInMonth = month.lengthOfMonth()
    val daysList = remember(month){ (1 .. daysInMonth).map { month.atDay(it) } }
    var selectedDate by remember { mutableStateOf(now) }
    val groupedTasksByDate = remember(groupedTasks, daysList) {
        val map = mutableStateMapOf<LocalDate, MutableMap<String, MutableList<Task>>>()
        groupedTasks.forEach { (group, tasks) ->
            tasks.forEach { task ->
                if (task.date != null) {
                    val taskLocalDate =
                        Instant.ofEpochMilli(task.date).atZone(ZoneId.systemDefault()).toLocalDate()
                    daysList.find { taskLocalDate.isEqual(it) }?.let {
                        map.getOrPut(taskLocalDate) { mutableMapOf() }.getOrPut(group) { mutableListOf() }.add(task)
                    }
                }
            }
        }
        map
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .padding(bottom = 6.dp)
        ) {
            items(daysList, key = {it}) { day ->
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = when (day) {
                                selectedDate -> if (selectedDate >= now) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                now -> MaterialTheme.colorScheme.surfaceContainerLowest
                                else -> Color.Transparent
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                selectedDate = day
                                onSelectedDate(selectedDate.toString())
                            }
                        )
                        .size(42.dp)
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        textAlign = TextAlign.Center,
                        color = when (day) {
                            selectedDate -> MaterialTheme.colorScheme.surfaceContainerLowest
                            LocalDate.now() -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.Circle,
                        tint = if (groupedTasksByDate.contains(day) && selectedDate != day) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentDescription = null,
                        modifier = Modifier
                            .size(4.dp)
                    )
                }
            }
        }

        groupedTasksByDate[selectedDate]?.toMap()?.let {
//            Log.d("Debug", "groupedTasksByDate, selectedDate: $selectedDate, map: $it")
            HomeListView(
                groupedTasks = it,
                onNavigateToTaskDetails = onNavigateToTaskDetails,
                onNavigateToSelectionScreen = {},
                markAsCompleted = markAsCompleted,
            )
        }
    }
}

@Preview
@Composable
fun HomeCalendarPreview() {
    TaskifyTheme {
        HomeCalendarView(
            groupedTasks = mapOf<String, List<Task>>(
                "Activity" to listOf(
                        Task(date = 1740009600000),
                        Task(date = 1740009600000),
                        Task(date = 1738540800000),
                        Task(date = 1739836800000)
                    )
            ),
            onSelectedDate = { _ -> Unit },
            onNavigateToTaskDetails = { _, _ -> Unit },
            markAsCompleted = { _, _ -> Unit }
        )
    }
}