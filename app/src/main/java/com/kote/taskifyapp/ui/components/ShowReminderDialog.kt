package com.kote.taskifyapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.R
import com.kote.taskifyapp.data.ReminderType

@Composable
fun ShowReminderDialog(
    reminderType: ReminderType,
    onConfirmation: (ReminderType) -> Unit,
    onDismissRequest: () -> Unit
) {
    var selectedType by remember { mutableStateOf(reminderType) }
    val reminderTypeOptions = mapOf(
        ReminderType.None to stringResource(R.string.none), ReminderType.OnTime to stringResource(
            R.string.on_time)
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.alert_reminder_title)) },
        dismissButton = { Button(onClick = { onDismissRequest() }) { Text(stringResource(R.string.alert_reminder_dismiss)) } },
        confirmButton = {
            Button(
                onClick = { onConfirmation(selectedType) }
            ) { Text(stringResource(R.string.alert_ok)) }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                reminderTypeOptions.forEach { (k, v) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .clickable {
                                selectedType = k
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Text(text = v, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = if (selectedType == k) MaterialTheme.colorScheme.primary else Color.Transparent
                        )
                    }
                }
            }
        }
    )
}
