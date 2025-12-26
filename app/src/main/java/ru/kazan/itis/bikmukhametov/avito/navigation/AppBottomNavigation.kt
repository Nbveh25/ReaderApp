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
fun AppBottomNavigation(
    currentRoute: Any?,
    onNavigate: (Any) -> Unit
) {

    val items = listOf(
        BottomNavItem.Books,
        BottomNavItem.Upload,
        BottomNavItem.Profile
    )

    val showBottomNav = currentRoute is Route.Books ||
            currentRoute is Route.Upload ||
            currentRoute is Route.Profile

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
                            onNavigate(item.route)
                        }
                    }
                )
            }
        }
    }
}

sealed class BottomNavItem(
    val route: Route,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Books : BottomNavItem(
        route = Route.Books,
        title = "Книги",
        icon = Icons.Default.Book
    )

    object Upload : BottomNavItem(
        route = Route.Upload,
        title = "Загрузка",
        icon = Icons.Default.Upload
    )

    object Profile : BottomNavItem(
        route = Route.Profile,
        title = "Профиль",
        icon = Icons.Default.Person
    )
}
