package ru.kazan.itis.bikmukhametov.avito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import ru.kazan.itis.bikmukhametov.avito.navigation.AppBottomNavigation
import ru.kazan.itis.bikmukhametov.avito.navigation.Navigation
import ru.kazan.itis.bikmukhametov.avito.navigation.Navigator
import ru.kazan.itis.bikmukhametov.avito.navigation.Route
import ru.kazan.itis.bikmukhametov.avito.navigation.rememberNavigationState
import ru.kazan.itis.bikmukhametov.core.ui.theme.AvitoTheme
import kotlin.jvm.Throws

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvitoTheme {

                val navigationState = rememberNavigationState(
                    startRoute = Route.Auth,
                    topLevelRoutes = setOf(Route.Auth, Route.Books, Route.Upload, Route.Profile),
                )

                val navigator = remember { Navigator(navigationState) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            AppBottomNavigation(
                                currentRoute = navigationState.topLevelRoute,
                                onNavigate = { route ->
                                    navigator.navigate(route = route)
                                }
                            )
                        }
                    ) { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            Navigation(
                                navigator = navigator
                            )
                        }
                    }
                }
            }
        }
    }
}
