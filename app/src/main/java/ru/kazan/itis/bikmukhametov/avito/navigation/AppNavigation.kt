package ru.kazan.itis.bikmukhametov.avito.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.kazan.itis.bikmukhametov.feature.auth.presentation.screen.auth.AuthScreen
import ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.books.BooksScreen
import ru.kazan.itis.bikmukhametov.feature.profile.impl.presentation.screen.profile.ProfileScreen
import ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.reading.ReadingScreen
import ru.kazan.itis.bikmukhametov.feature.upload.impl.presentation.screen.upload.UploadScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Routes.AUTH,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Routes.AUTH) {
                AuthScreen(
                    onNavigateToHome = {
                        navController.navigate(Routes.BOOKS) {
                            popUpTo(Routes.AUTH) { inclusive = true }
                        }
                    },
                    onNavigateToRegistration = {
                        navController.navigate(Routes.PROFILE) // TODO()
                    }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogoutClick = {
                        navController.navigate(Routes.AUTH) {
                            popUpTo(Routes.BOOKS) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.UPLOAD) {
                UploadScreen()
            }

            composable(Routes.BOOKS) {
                BooksScreen(
                    onNavigateToReading = { bookId ->
                        navController.navigate("${Routes.READING}/$bookId")
                    }
                )
            }

            composable("${Routes.READING}/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                ReadingScreen(
                    bookId = bookId,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

        }
    }
}

