package ru.kazan.itis.bikmukhametov.core.resources.string

interface StringResourceProvider {

    fun getString(resId: Int): String

    fun getString(resId: Int, vararg formatArgs: Any): String
}

