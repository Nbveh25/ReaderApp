package ru.kazan.itis.bikmukhametov.core.resources.image

import android.content.ContentResolver
import android.provider.OpenableColumns
import java.io.InputStream
import javax.inject.Inject
import androidx.core.net.toUri

internal class ImageResourceProviderImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : ImageResourceProvider {

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
            var name: String? = null
            
            if (uri.scheme == "content") {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (nameIndex != -1) {
                            name = cursor.getString(nameIndex)
                        }
                    }
                }
            }
            
            name ?: uri.lastPathSegment
        } catch (e: Exception) {
            null
        }
    }
}

