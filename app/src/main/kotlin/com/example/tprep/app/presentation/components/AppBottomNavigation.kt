package com.example.tprep.app.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tprep.app.navigation.NavigationState

@Composable
fun AppBottomNavigation(navigationState: NavigationState) {
    val navBackStackEntry by navigationState.navHostController.currentBackStackEntryAsState()
    val navigationItems = listOf(
        NavigationItem.PublicDecks,
        NavigationItem.LocalDecks,
        NavigationItem.History,
        NavigationItem.Profile
    )

    NavigationBar {
        navigationItems.forEach { item ->
            val isSelected = navBackStackEntry?.destination?.hierarchy?.any {
                it.route == item.route::class.qualifiedName
            } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) navigationState.navigateTo(item.route)
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(item.icon),
                        modifier = Modifier.size(20.dp),
                        contentDescription = stringResource(id = item.nameResId)
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = item.nameResId),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            )
        }
    }
}