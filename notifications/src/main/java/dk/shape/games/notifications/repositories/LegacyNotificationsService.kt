package dk.shape.games.notifications.repositories

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription as LegacySubscription
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.*

interface LegacyNotificationsService: NotificationsServiceBase {

    @Headers("Content-Type: application/json")
    @GET("/api/v1/subscriptions/{deviceId}")
    suspend fun getSubscriptions(
        @Path("deviceId") deviceId: String
    ): List<LegacySubscription>

    @Headers("Content-Type: application/json")
    @GET("/api/v1/subscriptions/{deviceId}?showAll=true")
    suspend fun getAllSubscriptions(
        @Path("deviceId") deviceId: String
    ): List<LegacySubscription>

    companion object {

        private val contentType = "application/json".toMediaType()

        @JvmStatic
        fun create(baseUrl: String, httpClient: OkHttpClient): LegacyNotificationsService {
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

            return retrofit.create(LegacyNotificationsService::class.java)
        }
    }
}