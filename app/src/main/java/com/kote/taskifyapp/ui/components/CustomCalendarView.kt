package com.kote.taskifyapp.ui.components

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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.ui.home.HomeListView
import com.kote.taskifyapp.ui.home.TasksUiState
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@Composable
fun CustomCalendarView(
    groupedTasks: Map<String, List<Task>>,
    onSelectedDate: (String) -> Unit,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    markAsCompleted: (String, Int) -> Unit,
    paddingValues: PaddingValues,
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
            style = MaterialTheme.typography.headlineSmall
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
    val daysInMonth = month.lengthOfMonth()
    val daysList = remember(month){ (1 .. daysInMonth).map { month.atDay(it) } }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
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
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
        ) {
            items(daysList, key = {it}) { day ->
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = when (day) {
                            LocalDate.now() -> Color.White
                            selectedDate -> Color.LightGray
                            else -> Color.Transparent })
                        .wrapContentSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                selectedDate = day
                                onSelectedDate(selectedDate.toString())
                            }
                        )
                ) {
                    Text(
                        text = day.dayOfMonth.toString(),
                        textAlign = TextAlign.Center
                    )
                    if (groupedTasksByDate.contains(day)) {
                        Icon(
                            imageVector = Icons.Default.Circle,
                            tint = Color.Blue,
                            contentDescription = null,
                            modifier = Modifier.size(4.dp)
                        )
                    }
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