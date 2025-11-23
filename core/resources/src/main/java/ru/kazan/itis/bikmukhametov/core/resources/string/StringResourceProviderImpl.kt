package ru.kazan.itis.bikmukhametov.core.resources.string

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResourceProviderImpl @Inject constructor(
    private val context: Context
) : StringResourceProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}

