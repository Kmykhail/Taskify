package com.kote.taskifyapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.AccessTime
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.ui.home.TaskFilterType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class SidePanelItem(
    val name: String,
    val icon: ImageVector,
    val sidePanelOptions: TaskFilterType
)

@Composable
fun SidePanel(
    drawerState: DrawerState,
    scope: CoroutineScope,
    selectedFilterType: TaskFilterType,
    onSelectedFilterType: (TaskFilterType) -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    val items = listOf(
        SidePanelItem("All", Icons.Default.ContentCopy, TaskFilterType.ALL),
        SidePanelItem("Today", Icons.Outlined.Today, TaskFilterType.TODAY),
        SidePanelItem("Planned", Icons.Outlined.AccessTime, TaskFilterType.PLANNED),
        SidePanelItem("Completed", Icons.Default.DoneAll, TaskFilterType.COMPLETED),
        SidePanelItem("Important", Icons.Outlined.StarOutline, TaskFilterType.IMPORTANT),
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = modifier
                        .width((LocalConfiguration.current.screenWidthDp * 0.8).dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(item.name) },
                            selected = item.sidePanelOptions == selectedFilterType,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onSelectedFilterType(item.sidePanelOptions)
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