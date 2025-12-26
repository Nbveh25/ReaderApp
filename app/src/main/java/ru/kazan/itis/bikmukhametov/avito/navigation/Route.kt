package ru.kazan.itis.bikmukhametov.avito.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    data object Auth: Route()
    data object Register: Route()
    data object Books: Route()
    data object Profile: Route()
    @Serializable
    data class Reading(val bookId: String): Route()
    data object Upload: Route()
}

