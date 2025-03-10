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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.Priority
import com.kote.taskifyapp.data.Task
import com.kote.taskifyapp.ui.components.TaskSummaryCardView
import com.kote.taskifyapp.ui.components.TaskSummaryDefaultView
import com.kote.taskifyapp.ui.settings.TaskViewType
import com.kote.taskifyapp.ui.theme.TaskifyTheme

@Composable
fun HomeListView(
    taskViewType: TaskViewType = TaskViewType.DefaultView,
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
            item {
                if (firstKey != title) Spacer(modifier = Modifier.height(20.dp))
                groupTasksType?.let {
                    if (it == GroupTasksType.COMPLETED) {
                        Text(
                            text = stringResource(R.string.deletion_after_30_days),
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                    }
                }
                TaskSection(
                    taskViewType = taskViewType,
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

@Composable
fun TaskSection(
    taskViewType: TaskViewType,
    title: String,
    tasks: List<Task>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    onNavigateToSelectionScreen: () -> Unit,
    markAsCompleted: (String, Int) -> Unit
) {
    val backgroundColor = if (taskViewType == TaskViewType.DefaultView) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent
    if (tasks.isNotEmpty()) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = backgroundColor)
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
                            taskViewType = taskViewType,
                            task = task,
                            isOverdue = title == "Overdue",
                            onNavigateToTaskDetails = onNavigateToTaskDetails,
                            onNavigateToSelectionScreen = onNavigateToSelectionScreen,
                            markAsCompleted = {markAsCompleted(title, task.id)}
                        )
                    }
                }
            }
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
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand)
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$taskCount")
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand/Collapse",
                modifier = Modifier.rotate(iconRotation)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskItem(
    taskViewType: TaskViewType,
    task: Task,
    isOverdue: Boolean,
    onNavigateToTaskDetails: (String, String?) -> Unit,
    onNavigateToSelectionScreen: () -> Unit,
    markAsCompleted: () -> Unit
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
    if (taskViewType == TaskViewType.DefaultView) {
        TaskSummaryDefaultView(
            task = task,
            checkBox = {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { if (!task.isCompleted) markAsCompleted() },
                    enabled = !task.isCompleted,
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = when (task.priority) {
                            Priority.High -> Color(0xFFAF2A2A)
                            Priority.Medium -> Color(0xFFE0B83D)
                            Priority.Low -> Color(0xFF93C47D)
                            else -> MaterialTheme.colorScheme.outline
                        }
                    )
                )
            },
            mainTextStyleEffect = TextStyle(textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None),
            isOverdue = isOverdue,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onNavigateToTaskDetails(task.id.toString(), null) },
                    onLongClick = {
                        vibration()
                        onNavigateToSelectionScreen()
                    }
                )
        )
    } else {
        TaskSummaryCardView(
            task = task,
            checkBox = {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { if (!task.isCompleted) markAsCompleted() },
                    enabled = !task.isCompleted,
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = when (task.priority) {
                            Priority.High -> Color(0xFFAF2A2A)
                            Priority.Medium -> Color(0xFFE0B83D)
                            Priority.Low -> Color(0xFF93C47D)
                            else -> MaterialTheme.colorScheme.outline
                        }
                    )
                )
            },
            mainTextStyleEffect = TextStyle(textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None),
            isOverdue = isOverdue,
            modifier = Modifier
                .heightIn(min = 56.dp, max = 100.dp)
                .combinedClickable(
                    onClick = { onNavigateToTaskDetails(task.id.toString(), null) },
                    onLongClick = {
                        vibration()
                        onNavigateToSelectionScreen()
                    }
                )
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}
