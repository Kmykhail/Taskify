package com.kote.taskifyapp.ui.home

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kote.taskifyapp.data.Task
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HomeListView(
    groupedTasks: Map<String, List<Task>>,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    onNavigateToSelectionScreen: () -> Unit,
    markAsCompleted: (String, Int) -> Unit,
    groupTasksType: GroupTasksType? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 10.dp)
            .fillMaxSize()
    ) {
        val firstKey = groupedTasks.keys.firstOrNull()
        groupedTasks.forEach { (title, taskList) ->
            if (taskList.isNotEmpty()) {
                item {
                    if (firstKey != title) Spacer(modifier = Modifier.height(20.dp))
                    groupTasksType?.let {
                        if (it == GroupTasksType.COMPLETED) {
                            Text(
                                "Completed tasks will be deleted after 30 days",
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                    TaskSection(
                        title = title,
                        tasks = taskList,
                        isExpanded = expandedStates.getOrPut(title) { true },
                        onToggleExpand = { expandedStates[title] = !expandedStates[title]!! },
                        onNavigateToTaskDetails = onNavigateToTaskDetails,
                        onNavigateToSelectionScreen = onNavigateToSelectionScreen,
                        markAsCompleted = markAsCompleted
                    )
                }
            }
        }
    }
}

@Composable
fun TaskSection(
    title: String,
    tasks: List<Task>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    onNavigateToSelectionScreen: () -> Unit,
    markAsCompleted: (String, Int) -> Unit
) {
    if (tasks.isNotEmpty()) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            SectionHeader(title, tasks.size, isExpanded, onToggleExpand)
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    tasks.forEach { task ->
                        TaskItem(
                            task = task,
                            onNavigateToTaskDetails = onNavigateToTaskDetails,
                            onNavigateToSelectionScreen = onNavigateToSelectionScreen,
                            markAsCompleted = {markAsCompleted(title, task.id)}
                        )
                    }
                }
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
            Text(text = "No reminders", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    taskCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    val transition = updateTransition(targetState = isExpanded, label = "expandTransition")
    val iconRotation by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 350, easing = LinearOutSlowInEasing) },
        label = "iconRotation"
    ) { if (it) 0f else -90f }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand)
            .padding(horizontal = 12.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Text("$taskCount")
        IconButton(onClick = onToggleExpand) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand/Collapse",
                modifier = Modifier.rotate(iconRotation)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun TaskItem(
    task: Task,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    onNavigateToSelectionScreen: () -> Unit,
    markAsCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun vibration() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = modifier
            .padding(vertical = 2.dp)
            .padding(end = 16.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onNavigateToTaskDetails(task.id.toString(), null) },
                onLongClick = {
                    vibration()
                    onNavigateToSelectionScreen()
                }
            )
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { if (!task.isCompleted) markAsCompleted() },
            enabled = !task.isCompleted
        )
        Text(
            text = task.title ?: "Untitled",
            style = TextStyle(textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        task.date?.let {
            val taskLocalDate = Instant.ofEpochMilli(task.date).atZone(ZoneId.systemDefault()).toLocalDate()
            Text(
                text = "${taskLocalDate.format(DateTimeFormatter.ofPattern("MMM"))} ${taskLocalDate.dayOfMonth}",
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        task.time?.let {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = "Clock",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
