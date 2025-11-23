package ru.kazan.itis.bikmukhametov.feature.profile.impl.data.datasource.remote

import android.util.Log
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.kazan.itis.bikmukhametov.feature.profile.api.config.S3Config
import ru.kazan.itis.bikmukhametov.feature.profile.api.datasource.remote.AvatarUploader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

class AvatarUploaderImpl @Inject constructor(
    private val s3Config: S3Config
) : AvatarUploader {

    companion object {
        private const val AVATARS_FOLDER = "avatars"
        private const val DEFAULT_CONTENT_TYPE = "image/jpeg"
    }

    private fun createS3Client(): AmazonS3Client {
        val credentials = BasicAWSCredentials(
            s3Config.accessKeyId,
            s3Config.secretAccessKey
        )

        val clientConfiguration = ClientConfiguration().apply {
            connectionTimeout = 60000
            socketTimeout = 120000
            maxErrorRetry = 3
        }
        
        val s3Client = AmazonS3Client(credentials, clientConfiguration)

        val endpointWithoutProtocol = s3Config.endpoint
            .removePrefix("https://")
            .removePrefix("http://")
        s3Client.setEndpoint(endpointWithoutProtocol)

        try {
            val region = com.amazonaws.regions.Region.getRegion(
                com.amazonaws.regions.Regions.fromName(s3Config.region)
            )
            s3Client.setRegion(region)
        } catch (e: Exception) {
            Log.w("AvatarUploaderImpl", "Не удалось установить регион ${s3Config.region}, используем endpoint напрямую")
        }
        
        return s3Client
    }

    private fun getContentType(fileName: String): String {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "webp" -> "image/webp"
            "gif" -> "image/gif"
            else -> DEFAULT_CONTENT_TYPE
        }
    }

    override suspend fun uploadAvatar(
        inputStream: InputStream,
        fileName: String,
        userId: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val s3Client = createS3Client()
        
        try {
            val imageBytes = inputStream.readBytes()
            Log.d("AvatarUploaderImpl", "Размер изображения: ${imageBytes.size} байт")

            val objectKey = "$AVATARS_FOLDER/$userId/$fileName"
            Log.d("AvatarUploaderImpl", "Object Key: $objectKey")

            val contentType = getContentType(fileName)
            Log.d("AvatarUploaderImpl", "Content-Type: $contentType")

            val metadata = ObjectMetadata().apply {
                contentLength = imageBytes.size.toLong()
            }

            val byteArrayInputStream = ByteArrayInputStream(imageBytes)

            val putRequest = PutObjectRequest(
                s3Config.bucketName,
                objectKey,
                byteArrayInputStream,
                metadata
            )

            Log.d("AvatarUploaderImpl", "Bucket: ${s3Config.bucketName}")
            Log.d("AvatarUploaderImpl", "Object Key: $objectKey")
            Log.d("AvatarUploaderImpl", "Content-Type: $contentType")
            Log.d("AvatarUploaderImpl", "Content-Length: ${imageBytes.size}")

            s3Client.putObject(putRequest)

            val publicUrl = "${s3Config.endpoint}/${s3Config.bucketName}/$objectKey"
            Log.d("AvatarUploaderImpl", "Файл успешно загружен: $publicUrl")
            
            Result.success(publicUrl)
        } catch (e: com.amazonaws.AmazonServiceException) {
            val errorMessage = when (e.statusCode) {
                401 -> "Ошибка аутентификации. Проверьте Access Key ID и Secret Access Key"
                403 -> "Доступ запрещен. Проверьте права доступа к бакету"
                404 -> "Бакет не найден: ${s3Config.bucketName}"
                else -> "Ошибка загрузки аватара: ${e.errorCode} - ${e.message}"
            }
            Log.e("AvatarUploaderImpl", errorMessage, e)
            Result.failure(IOException(errorMessage, e))
        } catch (e: IOException) {
            Log.e("AvatarUploaderImpl", "IOException при загрузке аватара: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AvatarUploaderImpl", "Exception при загрузке аватара: ${e.message}", e)
            Result.failure(IOException("Ошибка загрузки аватара: ${e.message}", e))
        } finally {
            s3Client.shutdown()
        }
    }
}

