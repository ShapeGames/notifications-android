package dk.shape.games.notifications.usecases

import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.presentation.SubjectInfo
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception

data class SubjectSettingsNotificationsInteractor(
    private val notificationsComponent: SubjectNotificationsDataSource,
    val provideSubjectInfo: suspend (Set<Subscription>) -> (List<SubjectInfo>)?
) : SubjectSettingsNotificationsUseCases {

    override suspend fun loadAllSubscriptions(
        deviceId: String,
        appConfig: AppConfig
    ): List<LoadedSubscription> =
        notificationsComponent.getAllSubscriptions(deviceId).first().let { subscriptions ->
            provideSubjectInfo(subscriptions)?.sortedBy { subjectInfo ->
                subjectInfo.subjectName
            }?.mapNotNull { subjectInfo ->

                subscriptions.find { subscription ->
                    subscription.subjectId == subjectInfo.subjectId
                }?.takeIf { subscription ->
                    subscription.types.isNotEmpty()
                }?.let { matchingSubscription ->
                    val statsNotificationGroups = appConfig.toStatsNotifications(subjectInfo)

                    statsNotificationGroups.find { notificationGroup ->
                        notificationGroup.sportId == subjectInfo.sportId
                    }?.let { matchingGroup ->
                        LoadedSubscription(
                            name = subjectInfo.subjectName,
                            subscription = matchingSubscription,
                            notificationGroup = matchingGroup
                        )
                    }
                }
            }
        } ?: emptyList()

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

    private fun AppConfig.toStatsNotifications(info: SubjectInfo): List<AppConfig.SubjectNotificationGroup> =
        when (info.subjectType) {
            SubjectType.TEAMS -> teamSubjectNotifications
            SubjectType.ATHLETES -> athleteSubjectNotifications
            else -> throw IllegalArgumentException("Notifications are only supported for teams and athletes at the moment.")
        }
}