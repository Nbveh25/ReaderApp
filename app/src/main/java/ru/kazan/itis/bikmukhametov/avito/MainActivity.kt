package ru.kazan.itis.bikmukhametov.avito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.kazan.itis.bikmukhametov.avito.navigation.AppBottomNavigation
import ru.kazan.itis.bikmukhametov.avito.navigation.Navigation
import ru.kazan.itis.bikmukhametov.avito.navigation.Route
import ru.kazan.itis.bikmukhametov.core.ui.theme.AvitoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AvitoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val backStack = remember { mutableStateListOf<Any>(Route.Auth) }
                    Scaffold(
                        bottomBar = {
                            AppBottomNavigation(
                                currentRoute = backStack.lastOrNull(),
                                onNavigate = { route ->
                                    backStack.clear()
                                    backStack.add(route)
                                }
                            )
                        }
                    ) { paddingValues ->
                        Box(modifier = Modifier.padding(paddingValues)) {
                            Navigation(
                                backStack = backStack
                            )
                        }
                    }
                }
            }
        }
    }
}
