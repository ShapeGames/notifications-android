package dk.shape.games.notifications.repositories

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.*

interface NotificationsServiceBase {

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

        @JvmStatic
        fun create(baseUrl: String, httpClient: OkHttpClient): NotificationsServiceBase {
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

            return retrofit.create(NotificationsServiceBase::class.java)
        }
    }
}