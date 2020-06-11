package dk.shape.games.notifications.demo.mock

import android.os.Parcelable
import dk.shape.danskespil.foundation.entities.PolyIcon
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.features.list.SubjectNotificationsEventHandler
import dk.shape.games.notifications.presentation.SubjectNotificationsFragment
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object SubjectDeviceIdProviderMock {
    fun provideDeviceIdMock(): String {
        return "1db655ee-48ea-11ea-b77f-2e728ce88125"
    }
}

object SubjectNotificationsProviderMock {
    fun provideNotificationsMock(): AppConfig.StatsNotifications {
        return AppConfig.StatsNotifications(
            group = listOf(
                AppConfig.StatsNotifications.StatsNotificationGroup(
                    sportId = "football:0000",
                    sportName = "Football",
                    defaultNotificationTypeIdentifiers = listOf(
                        AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_REMINDER,
                        AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_END
                    ),
                    notificationTypes = listOf(
                        AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationType(
                            identifier = AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_START,
                            icon = PolyIcon.Resource("", false),
                            name = "Startopstilling"
                        ),
                        AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationType(
                            identifier = AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_REMINDER,
                            icon = PolyIcon.Resource("", false),
                            name = "Inden event starter"
                        ),
                        AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationType(
                            identifier = AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.LINEUP_READY,
                            icon = PolyIcon.Resource("", false),
                            name = "Inden event starter"
                        ),
                        AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationType(
                            identifier = AppConfig.StatsNotifications.StatsNotificationGroup.StatsNotificationIdentifier.EVENT_END,
                            icon = PolyIcon.Resource("", false),
                            name = "Slutresultat for event"
                        )
                    )
                )
            )
        )
    }
}

object SubjectNotificationsRepositoryMock : SubjectNotificationsDataSource {

    private val subscriptionSetMock = mutableSetOf(
        Subscription(
            eventId = "team:0000",
            subjectId = "team:0000",
            subjectType = SubjectType.TEAMS,
            types = setOf()
        ),
        Subscription(
            eventId = "team:0001",
            subjectId = "team:0001",
            subjectType = SubjectType.TEAMS,
            types = setOf("event_start", "event_end")
        )
    )

    override suspend fun getSubscriptions(deviceId: String): Flow<Set<Subscription>> {
        return flow {
            emit(
                subscriptionSetMock.sortedBy {
                    it.eventId
                }.toSet()
            )
        }
    }

    override suspend fun updateSubjectSubscriptions(
        subjectId: String,
        subjectType: SubjectType,
        subscribedNotificationTypeIds: Set<String>
    ) {
        subscriptionSetMock.find { it.subjectId == subjectId }?.let { subscriptionSetMock.remove(it) }
        subscriptionSetMock.add(
            Subscription(
                eventId = subjectId,
                subjectId = subjectId,
                subjectType = subjectType,
                types = subscribedNotificationTypeIds.toSet()
            )
        )
    }

    override suspend fun register(
        deviceId: String,
        platform: String,
        environment: String,
        notificationToken: String
    ) {

    }
}

object subjectNotificationsEventHandlerMock : SubjectNotificationsEventHandler {
    override fun <T : Parcelable> onClosed(notificationsFragment: SubjectNotificationsFragment, actionData: T) {
        notificationsFragment.parentFragmentManager.popBackStack()
    }
}
