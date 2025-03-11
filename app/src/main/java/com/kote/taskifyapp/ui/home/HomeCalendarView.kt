package com.kote.taskifyapp.ui.home

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.MAX_MONTH
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.util.convertMillisToLocalDate
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeCalendarView(
    locale: String,
    groupedTasks: Map<Int, List<Task>>,
    selectedDate: LocalDate,
    setSelectedDate: (LocalDate) -> Unit,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    markAsCompleted: (Int, Int) -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val scope = rememberCoroutineScope()
    val currentYearMonth = remember { YearMonth.now() }
    val pagerState = rememberPagerState(initialPage = MAX_MONTH / 2, pageCount = { MAX_MONTH })
    var dateString by remember { mutableStateOf("") }

    LaunchedEffect(pagerState.currentPage) {
        val currentMonth = currentYearMonth.plusMonths(pagerState.currentPage - (MAX_MONTH / 2).toLong())
        val monthName = currentMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale(locale))
        dateString = "${monthName.replaceFirstChar { it.uppercase() }} ${currentMonth.year}"
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
            listOf(
                R.string.monday,
                R.string.tuesday,
                R.string.wednesday,
                R.string.thursday,
                R.string.friday,
                R.string.saturday,
                R.string.sunday
            ).forEach { dayOfWeek ->
                Text(
                    text = stringResource(dayOfWeek),
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
            val displayedMonth = currentYearMonth.plusMonths(page.toLong() - (MAX_MONTH / 2).toLong())
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
    groupedTasks: Map<Int, List<Task>>,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    markAsCompleted: (Int, Int) -> Unit,
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
        groupedTasks.flatMap { (group, tasks) ->
            tasks.mapNotNull { task ->
                task.date?.let { convertMillisToLocalDate(it) }?.takeIf { it in dateList }?.let { date ->
                    date to (group to task)
                }
            }
        }.groupBy({ it.first }, { it.second }).mapValues { (_, list) ->
            list.groupBy({ it.first }, { it.second }).mapValues { (_, tasks) -> tasks.toMutableList() }
        }.toMutableMap()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(horizontal = 6.dp)
                .padding(bottom = 6.dp)
        ) {
            items(dateList, key = { it.toEpochDay() }) { date ->
                val isSelected = date == selectedDate
                val isToday = date == todayDate
                val isInCurrentMonth = date.month == month.month

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color = when {
                            isSelected && selectedDate >= todayDate -> MaterialTheme.colorScheme.primary
                            isSelected -> MaterialTheme.colorScheme.error
                            isToday -> MaterialTheme.colorScheme.secondaryContainer
                            else -> Color.Transparent
                        })
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
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
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.surfaceContainerLowest
                            isToday -> MaterialTheme.colorScheme.primary
                            isInCurrentMonth -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.outlineVariant
                        }
                    )
                    Icon(
                        imageVector = Icons.Default.Circle,
                        tint = if (groupedTasksByDate.contains(date) && !isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
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