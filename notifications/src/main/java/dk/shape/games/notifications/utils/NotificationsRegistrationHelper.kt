package dk.shape.games.notifications.utils

import android.content.SharedPreferences
import dk.shape.games.notifications.repositories.NotificationsDataSource
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

/**
 * This class facilitates registration of notification token and provides suspending function to
 * obtain device ID that is guaranteed to be registered. It helps to make sure your token stays
 * up-to-date and registers a new one only if its really needed. It provides automatic retry logic
 * with exponential backoff throughout lifetime of your app in case of registration errors.
 *
 * @param sharedPrefs [SharedPreferences] where the class can store registered token
 * @param notificationsDataSource [NotificationsDataSource] which provides registration method
 * @param provideDeviceId Device ID provider
 * @param platform platform identifier you are registering with
 * @param environment app environment, usually "debug", "inhouse" or "production"
 */
@ExperimentalTime
class NotificationsRegistrationHelper(
    private val sharedPrefs: SharedPreferences,
    private val notificationsDataSource: NotificationsDataSource,
    private val provideDeviceId: suspend () -> String,
    private val platform: String = "android",
    private val environment: String
) {

    companion object {
        const val PREFS_NOTIFICATION_TOKEN = "notificationToken"
        const val PREFS_DEVICE_ID = "deviceID"
    }

    private val registrationAttemptScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var currentRegistrationAttempt: Job? = null

    private var registeredDeviceId: String? = null

    private val retryDelayFactor = 2.0

    private val maxRetryDelay = 32.toDuration(DurationUnit.SECONDS)

    private val minRetryDelay = 1.toDuration(DurationUnit.SECONDS)

    private var currentDelay = 0.toDuration(DurationUnit.SECONDS)

    private val retryDelaySemaphore = Semaphore(1, 0)

    private var retryDelaySemaphoreReleaseJob: Job? = null

    fun registerNotificationsToken(
        notificationsToken: String
    ) {
        if (notificationsToken == getLastRegisteredNotificationToken()) {
            return
        }
        if (currentRegistrationAttempt?.isActive == true) currentRegistrationAttempt?.cancel()
        if (retryDelaySemaphoreReleaseJob?.isActive == true) retryDelaySemaphoreReleaseJob?.cancel()

        currentRegistrationAttempt = registrationAttemptScope.launch(
            CoroutineExceptionHandler { coroutineContext, throwable ->
                currentDelay = (currentDelay.times(retryDelayFactor)).coerceAtMost(maxRetryDelay)
                    .coerceAtLeast(minRetryDelay)
                registerNotificationsToken(notificationsToken)
            }
        ) {
            val deviceId = async(Dispatchers.IO) {
                retryDelaySemaphoreReleaseJob = GlobalScope.async(Dispatchers.IO) {
                    delay(currentDelay.toLongMilliseconds())
                    retryDelaySemaphore.release()
                }
                retryDelaySemaphore.acquire()
                val deviceId = provideDeviceId()
                notificationsDataSource.register(
                    deviceId,
                    platform,
                    environment,
                    notificationsToken
                )
                registeredDeviceId = deviceId
                deviceId
            }.await()
            sharedPrefs.edit()
                .putString(PREFS_NOTIFICATION_TOKEN, notificationsToken)
                .putString(PREFS_DEVICE_ID, deviceId)
                .apply()
        }
    }

    suspend fun getRegisteredDeviceId(): String {
        if (retryDelaySemaphoreReleaseJob?.isActive == true) {
            retryDelaySemaphoreReleaseJob?.cancel()
            retryDelaySemaphore.release()
        }

        if (currentRegistrationAttempt?.isActive == true) {
            currentRegistrationAttempt?.join()
        }

        getLastRegisteredDeviceId()?.let { return it }

        throw IllegalStateException("You forgot to call registerNotificationToken")
    }

    private fun getLastRegisteredNotificationToken(): String? {
        return sharedPrefs.getString(PREFS_NOTIFICATION_TOKEN, null)
    }

    private fun getLastRegisteredDeviceId(): String? {
        return registeredDeviceId ?: sharedPrefs.getString(PREFS_DEVICE_ID, null)
    }

}