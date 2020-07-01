package dk.shape.games.notifications.usecases

import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

data class SubjectSettingsNotificationsInteractor(
    private val notificationsComponent: SubjectNotificationsDataSource
) : SubjectSettingsNotificationsUseCases {

    override suspend fun getAllSubscriptions(
        deviceId: String
    ): Set<Subscription> = notificationsComponent.getAllSubscriptions(deviceId).first()

    override suspend fun updateNotifications(
        deviceId: String,
        subjectId: String,
        subjectType: SubjectType,
        notificationTypeIds: Set<String>,
        onError: () -> Unit
    ) {
        try {
            notificationsComponent.updateSubjectSubscriptions(
                deviceId = deviceId,
                subjectId = subjectId,
                subjectType = subjectType,
                subscribedNotificationTypeIds = notificationTypeIds
            )
        } catch (e: Exception) {
            if (e is IOException || e is HttpException) {
                withContext(Dispatchers.Main) {
                    onError()
                }
            } else throw e
        }
    }
}