package ru.kazan.itis.bikmukhametov.core.resources.image

import java.io.InputStream

interface ImageResourceProvider {

    fun openInputStream(uriString: String): InputStream?

    fun getFileName(uriString: String): String?
}

