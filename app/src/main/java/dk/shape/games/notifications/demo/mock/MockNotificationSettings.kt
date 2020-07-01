package dk.shape.games.notifications.demo.mock

import dk.shape.danskespil.foundation.entities.PolyIcon
import dk.shape.games.notifications.aliases.Notifications
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.presentation.SubjectInfo
import dk.shape.games.notifications.usecases.LegacyEventNotificationsUseCases
import dk.shape.games.notifications.usecases.SubjectSettingsNotificationsUseCases
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.common.appconfig.BetSlipConfig
import dk.shape.games.sportsbook.offerings.common.appconfig.EventConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription
import java.util.*

typealias SubjectSubscription = dk.shape.games.notifications.entities.Subscription

val mockLegacySubscriptions: List<Subscription> = listOf(
    Subscription(
        eventId = "event:1234",
        commaSeparatedTypes = ",event_start"
    )
)

val mockSubjectSubscriptions: Set<SubjectSubscription> = listOf(
    SubjectSubscription(
        subjectId = "subject:1234",
        subjectType = SubjectType.TEAMS,
        types = listOf("event_start").toSet()
    )
).toSet()

val mockSubjectInfos = listOf(
    SubjectInfo(
        subjectId = "subject:1234",
        subjectType = SubjectType.TEAMS,
        subjectName = "Manchester",
        sportId = "sport:football"
    )
)

val mockEvents: List<Event> = listOf(
    Event(
        id = "event:1234",
        home = "Manchester",
        away = "Liverpool",
        awayImageUrl = null,
        scheduledStartTime = Date(),
        markets = listOf(),
        name = "Manchester - Liverpool",
        levelPath = listOf(),
        hasLiveVideoStream = false,
        isInRunning = true,
        hasBetInRun = false,
        hasLiveStatisticsStream = false,
        hasTVCoverage = false,
        hasStatistics = false,
        hasBetBuilder = false,
        marketCollections = listOf(),
        notificationConfigurationId = "sport:football",
        numberOfMarkets = 0,
        channels = listOf(),
        eventType = Event.EventType.DEFAULT,
        isSuspended = false,
        commentary = null,
        homeImageUrl = null,
        icon = null,
        lastUpdate = null,
        mainMarketId = "",
        streamId = null,
        tvChannels = null
    )
)

val mockTeamSubjectNotificationGroups: List<AppConfig.SubjectNotificationGroup> = listOf(
    AppConfig.SubjectNotificationGroup(
        sportId = "sport:football",
        sportName = "Football",
        defaultNotificationTypeIdentifiers = listOf(
            AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_REMINDER,
            AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_END
        ),
        notificationTypes = listOf(
            AppConfig.SubjectNotificationGroup.SubjectNotificationType(
                identifier = AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.LINEUP_READY,
                icon = PolyIcon.Resource("icon-lineup-ready", false),
                name = "Startopstilling"
            ),
            AppConfig.SubjectNotificationGroup.SubjectNotificationType(
                identifier = AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_REMINDER,
                icon = PolyIcon.Resource("icon-event-reminder", false),
                name = "Inden event starter"
            ),
            AppConfig.SubjectNotificationGroup.SubjectNotificationType(
                identifier = AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_START,
                icon = PolyIcon.Resource("icon-event-start", false),
                name = "Inden event starter"
            ),
            AppConfig.SubjectNotificationGroup.SubjectNotificationType(
                identifier = AppConfig.SubjectNotificationGroup.SubjectNotificationIdentifier.EVENT_END,
                icon = PolyIcon.Resource("icon-event-end", false),
                name = "Slutresultat for event"
            )
        )
    )
)

val mockEventNotificationGroups: List<AppConfig.Notifications.NotificationGroup> = listOf(
    AppConfig.Notifications.NotificationGroup(
        groupId = "sport:football",
        defaultNotificationTypeIdentifiers = listOf("event_start"),
        notificationTypes = listOf(
            AppConfig.Notifications.NotificationGroup.NotificationType(
                identifier = "event_start",
                name = "Event starts"
            )
        )

    )
)

val mockAppConfig: AppConfig = AppConfig(
    betSlipConfig = BetSlipConfig(),
    event = EventConfig(),
    notifications = Notifications(mockEventNotificationGroups),
    teamSubjectNotifications = mockTeamSubjectNotificationGroups
)

val mockLegacyEventNotificationsUseCases = object : LegacyEventNotificationsUseCases {
    override fun getAllSubscriptions(): List<Subscription> = mockLegacySubscriptions

    override fun updateNotifications(
        eventId: String,
        notificationTypeIds: Set<String>,
        onError: () -> Unit
    ) {
    }
}

val mockSubjectSettingsNotificationsUseCases = object : SubjectSettingsNotificationsUseCases {
    override suspend fun getAllSubscriptions(deviceId: String): Set<SubjectSubscription> =
        mockSubjectSubscriptions

    override suspend fun updateNotifications(
        deviceId: String,
        subjectId: String,
        subjectType: SubjectType,
        notificationTypeIds: Set<String>,
        onError: () -> Unit
    ) {
    }

}