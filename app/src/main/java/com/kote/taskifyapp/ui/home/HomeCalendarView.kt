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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import java.time.ZoneOffset

@Composable
fun HomeCalendarView(
    groupedTasks: Map<String, List<Task>>,
    selectedDate: Long,
    setSelectedDate: (Long) -> Unit,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    markAsCompleted: (String, Int) -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val pagerState = rememberPagerState(initialPage = Short.MAX_VALUE / 2, pageCount = { Short.MAX_VALUE.toInt() })
    var dateString by remember { mutableStateOf("") }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect {page ->
                val yearMonth = YearMonth.now().plusMonths(page - (Short.MAX_VALUE / 2).toLong())
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
                month = YearMonth.now().plusMonths(page - (Short.MAX_VALUE / 2).toLong()),
                selectedDate = selectedDate,
                onSelectedDate = setSelectedDate,
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
    selectedDate: Long,
    onSelectedDate: (Long) -> Unit,
    groupedTasks: Map<String, List<Task>>,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    markAsCompleted: (String, Int) -> Unit,
) {
    val nowMillis = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    val daysInMonth = month.lengthOfMonth()
    val daysList = remember(month){ (1 .. daysInMonth).map { month.atDay(it).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() } }
    val groupedTasksByDate = remember(groupedTasks, daysList) {
        val map = mutableStateMapOf<Long, MutableMap<String, MutableList<Task>>>()
        groupedTasks.forEach { (group, tasks) ->
            tasks.forEach { task ->
                if (task.date != null) {
                    daysList.find { task.date == it }?.let {
                        map.getOrPut(task.date) { mutableMapOf() }.getOrPut(group) { mutableListOf() }.add(task)
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
            items(daysList, key = {it}) { dayMillis ->
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = when (dayMillis) {
                                selectedDate -> if (selectedDate >= nowMillis) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                nowMillis -> MaterialTheme.colorScheme.secondaryContainer //MaterialTheme.colorScheme.surfaceContainerLowest
                                else -> Color.Transparent
                            }
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelectedDate(dayMillis) }
                        )
                        .size(42.dp)
                ) {
                    Text(
                        text = Instant.ofEpochMilli(dayMillis).atZone(ZoneId.systemDefault()).toLocalDate().dayOfMonth.toString(),//day.dayOfMonth.toString(),
                        textAlign = TextAlign.Center,
                        color = when (dayMillis) {
                            selectedDate -> MaterialTheme.colorScheme.surfaceContainerLowest
                            nowMillis -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.Circle,
                        tint = if (groupedTasksByDate.contains(dayMillis) && selectedDate != dayMillis) MaterialTheme.colorScheme.primary else Color.Transparent,
                        contentDescription = null,
                        modifier = Modifier
                            .size(4.dp)
                    )
                }
            }
        }

        groupedTasksByDate[selectedDate]?.toMap()?.let {
            HomeListView(
                groupedTasks = it,
                onNavigateToTaskDetails = onNavigateToTaskDetails,
                onNavigateToSelectionScreen = {},
                markAsCompleted = markAsCompleted,
            )
        }
    }
}

//@Preview
//@Composable
//fun HomeCalendarPreview() {
//    TaskifyTheme {
//        val selectedDate = remember { mutableStateOf<Long?>(null) }
//        HomeCalendarView(
//            groupedTasks = mapOf(
//                "Activity" to listOf(
//                        Task(date = 1740009600000),
//                        Task(date = 1740009600000),
//                        Task(date = 1738540800000),
//                        Task(date = 1739836800000)
//                    )
//            ),
//            previousSelectedDate = selectedDate,
//            onNavigateToTaskDetails = { _, _ ->  },
//            markAsCompleted = { _, _ -> }
//        )
//    }
//}