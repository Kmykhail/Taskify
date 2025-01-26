package com.kote.taskifyapp.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.data.Task

@Composable
fun HomeListView(
    tasks: List<Task>,
    tasksUiState: TasksUiState,
    onNavigateToTaskDetails: (String) -> Unit,
    markAsCompleted: (Int) -> Unit,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
    ) {
        ShowTasks(
            tasks = tasks.filter { !it.isCompleted },
            title = "Active",
            onNavigateToTaskDetails = onNavigateToTaskDetails,
            markAsCompleted = markAsCompleted
        )

        if (tasksUiState.filterType == FilterType.SHOW_COMPLETED) {
            ShowTasks(
                tasks = tasks.filter { it.isCompleted },
                title = "Completed",
                onNavigateToTaskDetails = onNavigateToTaskDetails,
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun ShowTasks(
    tasks: List<Task>,
    title: String,
    onNavigateToTaskDetails: (String) -> Unit,
    markAsCompleted: (Int) -> Unit = {},
) {
    if (tasks.isEmpty()) return

    var isExpanded by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val scrollPosition by derivedStateOf {
        if (listState.layoutInfo.totalItemsCount > 0) {
            val maxScroll = listState.layoutInfo.totalItemsCount - listState.layoutInfo.visibleItemsInfo.size
            if (maxScroll > 0) {
                listState.firstVisibleItemIndex.toFloat() / maxScroll
            } else {0f}
        } else {0f}
    }


    val transition = updateTransition(targetState = isExpanded, label = "expandTransition")
    val iconRotation by transition.animateFloat(
            transitionSpec = { tween(durationMillis = 500, easing = LinearOutSlowInEasing) },
            label = "iconRotation"
    ) { if (it) 180f else 0f }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color = Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 2.dp)
                .fillMaxWidth()
        ) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = tasks.size.toString())
            IconButton(onClick = {isExpanded = !isExpanded} ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Chevron down",
                    modifier = Modifier.rotate(iconRotation)
                )
            }
        }
//        LinearProgressIndicator(
//            progress = { scrollPosition },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(4.dp)
//                .rotate(90f)
//        )
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(task, onNavigateToTaskDetails, markAsCompleted, title == "Active")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TaskItem(
    task: Task,
    onNavigateToTaskDetails: (String) -> Unit,
    markAsCompleted: (Int) -> Unit,
    enabled: Boolean,
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .padding(end = 16.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onNavigateToTaskDetails(task.id.toString()) },
                onLongClick = { showBottomSheet = true}
            )
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { if (enabled) markAsCompleted(task.id) },
            enabled = enabled
        )
        Text(text = task.title ?: "Untitled")
        Spacer(modifier = Modifier.weight(1f))
        if (task.time != null) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = "Clock",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {showBottomSheet = false},
            sheetState = sheetState
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}