package dk.shape.games.notifications.repositories

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.*

interface NotificationsService {

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

    @Headers("Content-Type: application/json")
    @PUT("/api/v1/subscriptions")
    suspend fun updateSubscriptions(
        @Body request: SubscribeRequest
    )

    @Headers("Content-Type: application/json")
    @POST("/api/v1/register")
    suspend fun register(
        @Body request: RegistrationRequest
    )

    companion object {

        private val contentType = "application/json".toMediaType()

        @UnstableDefault
        @JvmStatic
        fun create(baseUrl: String, httpClient: OkHttpClient): NotificationsService {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(
                    Json(
                        JsonConfiguration(strictMode = false)
                    ).asConverterFactory(contentType)
                )
                .baseUrl(baseUrl)
                .client(httpClient)
                .build()

            return retrofit.create(NotificationsService::class.java)
        }
    }
}