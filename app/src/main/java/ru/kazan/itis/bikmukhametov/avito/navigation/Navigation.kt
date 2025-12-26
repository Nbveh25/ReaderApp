package ru.kazan.itis.bikmukhametov.avito.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
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
    backStack: MutableList<Any> = remember { mutableStateListOf() }
) {

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
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
                            backStack.clear()
                            backStack.add(Route.Books)
                        },
                        onNavigateToRegistration = {
                            backStack.add(Route.Register)
                        }
                    )
                }

                is Route.Register -> NavEntry(key) {
                    RegisterScreen(
                        onNavigateToBooks = {
                            backStack.clear()
                            backStack.add(Route.Books)
                        },
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }

                is Route.Books -> NavEntry(key) {
                    BooksScreen(
                        onNavigateToReading = { id ->
                            backStack.add(Route.Reading(bookId = id))
                        }
                    )
                }

                is Route.Profile -> NavEntry(key) {
                    ProfileScreen(
                        onLogoutClick = {
                            backStack.clear()
                            backStack.add(Route.Auth)
                        }
                    )
                }

                is Route.Reading -> NavEntry(key) {
                    ReadingScreen(
                        bookId = key.bookId,
                        onNavigateBack = {
                            backStack.removeLastOrNull()
                        }
                    )
                }

                is Route.Upload -> NavEntry(key) {
                    UploadScreen(
                        onNavigateToBooks = {
                            backStack.clear()
                            backStack.add(Route.Books)
                        }
                    )
                }

                else -> NavEntry(Unit) {
                    Unit
                }
            }
        }
    )

}
