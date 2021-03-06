package dk.shape.games.notifications.repositories

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.*

interface NotificationsService : NotificationsServiceBase {

    @Headers("Content-Type: application/json")
    @GET("/api/v1/subscriptions/{deviceId}")
    suspend fun getSubscriptions(
        @Path("deviceId") deviceId: String
    ): List<Subscription>

    @Headers("Content-Type: application/json")
    @GET("/api/v1/subscriptions/{deviceId}?subject_type={subject_type}")
    suspend fun getSubscriptionsForType(
        @Path("deviceId") deviceId: String,
        @Query("subject_type") subjectType: SubjectType
    ): List<Subscription>

    @Headers("Content-Type: application/json")
    @GET("/api/v1/subscriptions/{deviceId}?showAll=true")
    suspend fun getAllSubscriptions(
        @Path("deviceId") deviceId: String
    ): List<Subscription>

    companion object {

        private val contentType = "application/json".toMediaType()

        @JvmStatic
        fun create(baseUrl: String, httpClient: OkHttpClient): NotificationsService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }.asConverterFactory(contentType)
                )
                .baseUrl(baseUrl)
                .client(httpClient)
                .build()

            return retrofit.create(NotificationsService::class.java)
        }
    }
}