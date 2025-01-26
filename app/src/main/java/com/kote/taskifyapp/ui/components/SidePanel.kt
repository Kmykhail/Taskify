package com.kote.taskifyapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SidePanel(
    drawerState: DrawerState,
    scope: CoroutineScope,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val items = mapOf(
        "Today" to Icons.Outlined.Today,
        "Planned" to Icons.Outlined.AccessTime,
        "Completed" to Icons.Default.DoneAll,
        "Important" to Icons.Outlined.StarOutline,
        "Trash" to Icons.Outlined.DeleteOutline
    )
    val selectedItem = remember { mutableStateOf(items["Today"]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = modifier
                        .width((LocalConfiguration.current.screenWidthDp * 0.8).dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    items.forEach { (label, icon) ->
                        NavigationDrawerItem(
                            icon = { Icon(icon, contentDescription = icon.name) },
                            label = { Text(label) },
                            selected = icon == selectedItem.value,
                            onClick = {
                                selectedItem.value = icon
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        },
        content = content,
    )
}