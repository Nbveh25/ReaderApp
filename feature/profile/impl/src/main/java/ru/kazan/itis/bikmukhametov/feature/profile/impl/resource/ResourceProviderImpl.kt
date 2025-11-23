package ru.kazan.itis.bikmukhametov.feature.profile.impl.resource

import android.content.ContentResolver
import android.net.Uri
import ru.kazan.itis.bikmukhametov.feature.profile.api.resource.ResourceProvider
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.net.toUri

class ResourceProviderImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : ResourceProvider {

    override fun openInputStream(uriString: String): InputStream? {
        return try {
            val uri = uriString.toUri()
            contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            null
        }
    }

    override fun getFileName(uriString: String): String? {
        return try {
            val uri = uriString.toUri()
            uri.lastPathSegment
        } catch (e: Exception) {
            null
        }
    }
}

