package com.kote.taskifyapp.ui.home

import android.util.Log
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kote.taskifyapp.util.convertMillisToLocalDate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun HomeCalendarView(
    groupedTasks: Map<String, List<Task>>,
    selectedDate: LocalDate,
    setSelectedDate: (LocalDate) -> Unit,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    markAsCompleted: (String, Int) -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = Short.MAX_VALUE / 2, pageCount = { Short.MAX_VALUE.toInt() })
    var dateString by remember { mutableStateOf("") }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val currentMonth = YearMonth.now().plusMonths(page - (Short.MAX_VALUE / 2).toLong())
            dateString = "${currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${currentMonth.year}"
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
            val displayedMonth = YearMonth.now().plusMonths(page.toLong() - (Short.MAX_VALUE / 2).toLong())
            CalendarView(
                month = displayedMonth,
                onMonthChanges = {
                    scope.launch {
                        pagerState.animateScrollToPage(page = pagerState.currentPage + it, animationSpec = tween(durationMillis = 300))
                    }
                },
                onSelectedDate = { setSelectedDate(it)  },
                selectedDate = selectedDate,
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
    onMonthChanges: (Int) -> Unit,
    onSelectedDate: (LocalDate) -> Unit,
    selectedDate: LocalDate,
    groupedTasks: Map<String, List<Task>>,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    markAsCompleted: (String, Int) -> Unit,
) {
    val todayDate = LocalDate.now()
    val dateList = remember(month) {
        val previousMonth = month.minusMonths(1)
        val nextMonth = month.plusMonths(1)
        val daysFromPrevMonth = month.atDay(1).dayOfWeek.value - 1
        val daysInCurrentMonth: Int = month.lengthOfMonth()

        val totalCells = 7 * 6
        val daysFromNextMonth = totalCells - (daysInCurrentMonth + daysFromPrevMonth)

        buildList<LocalDate> {
            for (day in (previousMonth.lengthOfMonth() - daysFromPrevMonth + 1)..previousMonth.lengthOfMonth()) {
                add(previousMonth.atDay(day))
            }

            for (day in 1 .. daysInCurrentMonth) {
                add(month.atDay(day))
            }

            for (day in 1 .. daysFromNextMonth) {
                add(nextMonth.atDay(day))
            }
        }
    }
    val groupedTasksByDate = remember(groupedTasks, dateList) {
        val map = mutableStateMapOf<LocalDate, MutableMap<String, MutableList<Task>>>()
        groupedTasks.forEach { (group, tasks) ->
            tasks.forEach { task ->
                if (task.date != null) {
                    val taskDate = convertMillisToLocalDate(task.date)
                    dateList.find { taskDate == it }?.let {
                        map.getOrPut(taskDate) { mutableMapOf() }.getOrPut(group) { mutableListOf() }.add(task)
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .padding(bottom = 6.dp)
        ) {
            items(dateList, key = {it}) { date ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = when (date) {
                            selectedDate -> if (selectedDate >= todayDate) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            todayDate -> MaterialTheme.colorScheme.secondaryContainer
                            else -> Color.Transparent
                        })
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                Log.d("Debug", "click by date: ${date}")
                                onSelectedDate(date)
                                if (date.month != month.month) {
                                    onMonthChanges(if (date.isBefore(month.atDay(1))) -1 else 1)
                                }
                            })
                        .size(42.dp)
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        textAlign = TextAlign.Center,
                        color = when (date) {
                            selectedDate -> MaterialTheme.colorScheme.surfaceContainerLowest
                            todayDate -> MaterialTheme.colorScheme.primary
                            else -> {
                                if (date.month == month.month) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                }
                            }
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.Circle,
                        tint = if (groupedTasksByDate.contains(date) && selectedDate != date) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentDescription = null,
                        modifier = Modifier.size(4.dp)
                    )
                }
            }
        }

        groupedTasksByDate[selectedDate]?.toMap()?.let {
            if (month.month == selectedDate.month) {
                HomeListView(
                    groupedTasks = it,
                    onNavigateToTaskDetails = onNavigateToTaskDetails,
                    onNavigateToSelectionScreen = {},
                    markAsCompleted = markAsCompleted,
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeCalendarPreview() {
    TaskifyTheme {
        HomeCalendarView(
            groupedTasks = mapOf(
                "Activity" to listOf(
                    Task(date = 1740009600000),
                    Task(date = 1740009600000),
                    Task(date = 1738540800000),
                    Task(date = 1739836800000)
                )
            ),
            onNavigateToTaskDetails = { _, _ -> },
            markAsCompleted = { _, _ -> },
            selectedDate = LocalDate.now(),
            setSelectedDate = {},
        )
    }
}