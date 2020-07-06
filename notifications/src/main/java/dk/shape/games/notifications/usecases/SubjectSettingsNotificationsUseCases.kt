package dk.shape.games.notifications.usecases

import dk.shape.games.notifications.aliases.SubjectNotificationGroup
import dk.shape.games.notifications.aliases.SubjectNotificationIdentifier
import dk.shape.games.notifications.aliases.SubjectNotificationType
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.presentation.SubjectInfo
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig

interface SubjectSettingsNotificationsUseCases {

    suspend fun loadAllSubscriptions(
        deviceId: String,
        appConfig: AppConfig,
        provideSubjectInfo: suspend (Set<Subscription>) -> (List<SubjectInfo>)?
    ): List<LoadedSubscription>

    suspend fun updateNotifications(
        deviceId: String,
        subjectId: String,
        subjectType: SubjectType,
        notificationTypeIds: Set<SubjectNotificationIdentifier>,
        onSuccess: () -> Unit,
        onError: () -> Unit
    )
}

data class LoadedSubscription(
    val name: String,
    val subscription: Subscription,
    val notificationGroup: SubjectNotificationGroup
)