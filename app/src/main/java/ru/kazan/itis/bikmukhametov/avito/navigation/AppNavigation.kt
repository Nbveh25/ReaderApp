package ru.kazan.itis.bikmukhametov.avito.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ru.kazan.itis.bikmukhametov.feature.auth.presentation.screen.auth.AuthScreen
import ru.kazan.itis.bikmukhametov.feature.books.presentation.screen.downloaded.DownloadedBooksScreen
import ru.kazan.itis.bikmukhametov.feature.reading.presentation.screen.ReadingScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Routes.AUTH
) {
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
            // TODO: RegistrationScreen(
            //     onNavigateToLogin = {
            //         navController.popBackStack()
            //     },
            //     onNavigateToHome = {
            //         navController.navigate(Routes.HOME) {
            //             popUpTo(Routes.REGISTRATION) { inclusive = true }
            //         }
            //     }
            // )
        }

        composable(Routes.BOOKS) {
            DownloadedBooksScreen(
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

