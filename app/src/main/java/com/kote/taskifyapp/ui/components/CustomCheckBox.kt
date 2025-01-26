package com.kote.taskifyapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomCheckBox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit),
    size: Dp = 24.dp
) {
    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Box(modifier = Modifier
            .size(size)
            .border(width = 1.dp, color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, CircleShape)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = { onCheckedChange(!checked) }
            )
        ) {
            Icon(imageVector = Icons.Default.Check,
                contentDescription = "checked",
                tint = if (checked) MaterialTheme.colorScheme.surface else Color.Transparent,
                modifier = Modifier
                    .background(color = if (checked) MaterialTheme.colorScheme.primary else Color.Transparent)
            )
        }
    }
}