package ru.kazan.itis.bikmukhametov.avito.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Books,
        BottomNavItem.Upload,
        BottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Показываем BottomNavigation только на основных экранах
    val showBottomNav = currentRoute in listOf(
        Routes.BOOKS,
        Routes.UPLOAD,
        Routes.PROFILE
    )

    if (showBottomNav) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                // Очищаем back stack до начала bottom navigation
                                popUpTo(Routes.BOOKS) {
                                    saveState = true
                                }
                                // Восстанавливаем состояние при повторном выборе
                                restoreState = true
                                // Избегаем множественных копий одного экрана
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Books : BottomNavItem(
        route = Routes.BOOKS,
        title = "Книги",
        icon = Icons.Default.Book
    )

    object Upload : BottomNavItem(
        route = Routes.UPLOAD,
        title = "Загрузка",
        icon = Icons.Default.Upload
    )

    object Profile : BottomNavItem(
        route = Routes.PROFILE,
        title = "Профиль",
        icon = Icons.Default.Person
    )
}
