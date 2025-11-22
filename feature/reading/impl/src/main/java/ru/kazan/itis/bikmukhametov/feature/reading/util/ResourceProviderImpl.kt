package ru.kazan.itis.bikmukhametov.feature.reading.util

import android.content.Context
import ru.kazan.itis.bikmukhametov.util.ResourceProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProviderImpl @Inject constructor(
    private val context: Context
) : ResourceProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}