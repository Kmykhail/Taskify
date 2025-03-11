package com.kote.taskifyapp.ui.home

import android.content.Context
import androidx.annotation.StringRes
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kote.taskifyapp.R
import com.kote.taskifyapp.ui.components.CustomCheckBox
import com.kote.taskifyapp.ui.settings.Language
import com.kote.taskifyapp.ui.settings.SettingType
import com.kote.taskifyapp.ui.settings.TaskViewType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsView(
    settingsUiState: SettingsUiState,
    setSettings: (SettingType, Int, Context) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSettingDialog by remember { mutableStateOf(false) }
    var settingTitle by remember { mutableIntStateOf(0) }
    var settingType by remember { mutableStateOf(SettingType.Language) }
    var settingItems by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
    val context = LocalContext.current

    if (showSettingDialog) {
        ShowSettingDialog(
            title = settingTitle,
            currentItem = when(settingType) {
                SettingType.Language -> settingsUiState.language.ordinal
                SettingType.TaskViewType -> settingsUiState.taskViewType.ordinal
            },
            itemList = settingItems,
            onConfirmation = { itemValue ->
                CoroutineScope(Dispatchers.Main).launch {
                    setSettings(settingType, itemValue, context)
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
            text = stringResource(R.string.settings),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 2.dp, bottom = 10.dp)
        )

        Card(
            colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Column{
                SettingSummary(
                    title = R.string.language,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingTitle = R.string.language
                            settingType = SettingType.Language
                            settingItems = listOf(
                                Pair(Language.En.name, Language.En.ordinal),
                                Pair(Language.Uk.name, Language.Uk.ordinal)
                            )
                            showSettingDialog = true
                        }
                        .padding(10.dp)
                )
                SettingSummary(
                    title = R.string.task_view,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            settingTitle = R.string.task_view
                            settingType = SettingType.TaskViewType
                            settingItems = listOf(
                                Pair(TaskViewType.DefaultView.name, TaskViewType.DefaultView.ordinal),
                                Pair(TaskViewType.CardView.name, TaskViewType.CardView.ordinal)
                            )
                            showSettingDialog = true
                        }
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
private fun ShowSettingDialog(
    @StringRes title: Int,
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
                    text = stringResource(title),
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

@Composable
private fun SettingSummary(
    @StringRes title: Int,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        Text(
            text = stringResource(title),
            fontWeight = FontWeight.SemiBold
        )
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}