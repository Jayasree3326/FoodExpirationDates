package com.lorenzovainigli.foodexpirationdates.view.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.lorenzovainigli.foodexpirationdates.R
import com.lorenzovainigli.foodexpirationdates.ui.theme.FoodExpirationDatesTheme
import com.lorenzovainigli.foodexpirationdates.view.composable.screen.Screen
import com.lorenzovainigli.foodexpirationdates.view.preview.LanguagePreviews

@Composable
fun MyBottomAppBar(
    navController: NavHostController,
    currentDestination: String?
){
    val navigationItems = listOf(
        NavigationItem(
            label = stringResource(id = R.string.about_this_app),
            route = Screen.AboutScreen.route,
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Outlined.Info
        ),
        NavigationItem(
            label = stringResource(id = R.string.list),
            route = Screen.MainScreen.route,
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List
        ),
        NavigationItem(
            label = stringResource(id = R.string.settings),
            route = Screen.SettingsScreen.route,
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )
    NavigationBar {
        var selectedItem = when (currentDestination) {
            Screen.AboutScreen.route -> 0
            Screen.SettingsScreen.route -> 2
            else -> 1
        }
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.route)
                },
                icon = {
                    Icon(
                        imageVector = when (selectedItem == index) {
                            true -> item.selectedIcon
                            else -> item.unselectedIcon
                        },
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

@PreviewLightDark
@LanguagePreviews
@Composable
fun MyBottomAppBarPreview(){
    FoodExpirationDatesTheme {
        Surface {
            MyBottomAppBar(
                navController = rememberNavController(),
                currentDestination = Screen.AboutScreen.route
            )
        }
    }
}