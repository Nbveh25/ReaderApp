package ru.kazan.itis.bikmukhametov.avito.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ru.kazan.itis.bikmukhametov.feature.auth.presentation.screen.auth.AuthScreen
import ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books.BooksScreen
import ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile.ProfileScreen
import ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading.ReadingScreen
import ru.kazan.itis.bikmukhametov.feature.register.impl.presentation.screen.register.RegisterScreen
import ru.kazan.itis.bikmukhametov.feature.upload.impl.presentation.screen.upload.UploadScreen

@Composable
fun Navigation(
    navigator: Navigator,
) {
    val currentBackStack = navigator.state.backStacks[navigator.state.topLevelRoute]
        ?: error("BackStack for ${navigator.state.topLevelRoute} not found")
    
    // NavBackStack implements List<NavKey>, so we can use it directly
    @Suppress("UNCHECKED_CAST")
    val backStackList = currentBackStack as List<NavKey>

    NavDisplay(
        backStack = backStackList,
        onBack = {
            navigator.goBack()
        },
        transitionSpec = {
            // Slide in from right when navigating forward
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        entryProvider = { key ->
            when (key) {
                is Route.Auth -> NavEntry(key) {
                    AuthScreen(
                        onNavigateToBooks = {
                            navigator.navigate(Route.Books)
                        },
                        onNavigateToRegistration = {
                            navigator.navigate(Route.Register)
                        }
                    )
                }

                is Route.Register -> NavEntry(key) {
                    RegisterScreen(
                        onNavigateToBooks = {
                            navigator.navigate(Route.Books)
                        },
                        onNavigateBack = {
                            navigator.goBack()
                        }
                    )
                }

                is Route.Books -> NavEntry(key) {
                    BooksScreen(
                        onNavigateToReading = { id ->
                            navigator.navigate(Route.Reading(bookId = id))
                        }
                    )
                }

                is Route.Profile -> NavEntry(key) {
                    ProfileScreen(
                        onLogoutClick = {
                            navigator.navigate(Route.Auth)
                        }
                    )
                }

                is Route.Reading -> NavEntry(key) {
                    ReadingScreen(
                        bookId = key.bookId,
                        onNavigateBack = {
                            navigator.goBack()
                        }
                    )
                }

                is Route.Upload -> NavEntry(key) {
                    UploadScreen(
                        onNavigateToBooks = {
                            navigator.navigate(Route.Books)
                        }
                    )
                }

                else -> error("Unknown key: $key")
            }
        }
    )

}
