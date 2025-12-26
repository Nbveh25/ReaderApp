package ru.kazan.itis.bikmukhametov.avito.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object Route {
    @Serializable object Auth: NavKey
    @Serializable object Register: NavKey
    @Serializable object Books: NavKey
    @Serializable object Profile: NavKey
    @Serializable data class Reading(val bookId: String): NavKey
    @Serializable object Upload: NavKey
}

