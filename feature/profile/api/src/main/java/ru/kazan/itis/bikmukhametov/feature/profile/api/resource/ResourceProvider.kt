package ru.kazan.itis.bikmukhametov.feature.profile.api.resource

import java.io.InputStream

interface ResourceProvider {

    // чтение по uri
    fun openInputStream(uriString: String): InputStream?
    
    // получает из файла uri
    fun getFileName(uriString: String): String?
}

