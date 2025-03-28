package com.kote.taskifyapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.outlined.AccessTime
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kote.taskifyapp.R
import com.kote.taskifyapp.ui.home.GroupTasksType
import com.kote.taskifyapp.util.TestTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class SidePanelItem(
    val name: String,
    val icon: ImageVector,
    val sidePanelOptions: GroupTasksType
)

@Composable
fun SidePanel(
    drawerState: DrawerState,
    scope: CoroutineScope,
    selectedFilterType: GroupTasksType,
    onSelectedFilterType: (GroupTasksType) -> Unit,
    gestureEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val items = listOf(
        SidePanelItem(stringResource(R.string.side_panel_all), Icons.Default.ContentCopy, GroupTasksType.ALL),
        SidePanelItem(stringResource(R.string.side_panel_today), Icons.Outlined.Today, GroupTasksType.TODAY),
        SidePanelItem(stringResource(R.string.side_panel_planned), Icons.Outlined.AccessTime, GroupTasksType.PLANNED),
        SidePanelItem(stringResource(R.string.side_panel_completed), Icons.Default.DoneAll, GroupTasksType.COMPLETED)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = modifier
                        .fillMaxHeight()
                        .width((LocalConfiguration.current.screenWidthDp * 0.8).dp)
                        .verticalScroll(rememberScrollState())
                        .padding(top = 10.dp)
                ) {
                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = item.name) },
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
        gesturesEnabled = gestureEnabled
    )
}