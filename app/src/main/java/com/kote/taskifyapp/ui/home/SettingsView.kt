package com.kote.taskifyapp.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.ui.settings.SettingsUiState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.kote.taskifyapp.R
import com.kote.taskifyapp.ui.components.CustomCheckBox
import com.kote.taskifyapp.ui.settings.SettingType
import com.kote.taskifyapp.ui.settings.TaskViewType
import com.kote.taskifyapp.ui.theme.TaskifyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsView(
    settingsUiState: SettingsUiState,
    setSettings: (SettingType?, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSettingDialog by remember { mutableStateOf(false) }
    var settingTitle by remember { mutableStateOf("") }
    var settingType by remember { mutableStateOf<SettingType?>(null) }
    var settingItems by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    if (showSettingDialog) {
        ShowSettingDialog(
            title = settingTitle,
            currentItem = settingsUiState.taskViewType.ordinal,
            itemList = settingItems,
            onConfirmation = { itemValue ->
                CoroutineScope(Dispatchers.Main).launch {
                    setSettings(settingType, itemValue)
                    delay(100)
                    showSettingDialog = false
                }
            },
            onDismissRequest = {showSettingDialog = false}
        )
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(horizontal = 10.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 2.dp, bottom = 10.dp)
        )

        Card(
            colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column {
                // View
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingTitle = "View"
                            settingType = SettingType.TaskViewType
                            settingItems = listOf(
                                Pair(TaskViewType.List.name,TaskViewType.List.ordinal),
                                Pair(TaskViewType.VerticalGrid.name, TaskViewType.VerticalGrid.ordinal)
                            )
                            showSettingDialog = true
                        }
                        .padding(10.dp)
                ) {
                    Text(
                        text = "View",
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun ShowSettingDialog(
    title: String,
    currentItem: Int,
    itemList: List<Pair<String, Int>>,
    onConfirmation: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    val dialogWidth = LocalConfiguration.current.screenWidthDp.dp * 0.8f
    val dialogHeight = LocalConfiguration.current.screenHeightDp * 0.6f

    Dialog(onDismissRequest = onDismissRequest) {
        Card{
            Column(
                modifier = Modifier
                    .width(dialogWidth)
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                LazyColumn(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .heightIn(min = 56.dp, max = dialogHeight.dp)
                ) {
                    item {
                        itemList.forEach { (label, item) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onConfirmation(item)
                                    }
                            ) {
                                CustomCheckBox(
                                    checked = item == currentItem,
                                    onCheckedChange = {onConfirmation(item)}
                                )
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }

                TextButton(onClick = {onDismissRequest()}, contentPadding = PaddingValues(0.dp), modifier = Modifier.align(Alignment.End)) {
                    Text(
                        text = stringResource(R.string.alert_reminder_dismiss),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ShowSettingDialogPreview() {
    TaskifyTheme {
        ShowSettingDialog(
            title = "View",
            currentItem = 0,
            itemList = listOf(
                Pair(TaskViewType.List.name,TaskViewType.List.ordinal),
                Pair(TaskViewType.VerticalGrid.name, TaskViewType.VerticalGrid.ordinal),
                Pair(TaskViewType.VerticalGrid.name, TaskViewType.VerticalGrid.ordinal),
                Pair(TaskViewType.VerticalGrid.name, TaskViewType.VerticalGrid.ordinal),
                Pair(TaskViewType.VerticalGrid.name, TaskViewType.VerticalGrid.ordinal),
                Pair(TaskViewType.VerticalGrid.name, TaskViewType.VerticalGrid.ordinal),
            ),
            onConfirmation = { _ -> },
            onDismissRequest = {}
        )
    }
}
