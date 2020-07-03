package dk.shape.games.notifications.demo.mock

import dk.shape.componentkit2.Promise
import dk.shape.danskespil.foundation.entities.PolyIcon
import dk.shape.games.notifications.aliases.Notifications
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.presentation.SubjectInfo
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import dk.shape.games.notifications.usecases.LegacyEventNotificationsUseCases
import dk.shape.games.notifications.usecases.SubjectSettingsNotificationsUseCases
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.common.appconfig.BetSlipConfig
import dk.shape.games.sportsbook.offerings.common.appconfig.EventConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.NotificationsComponentInterface
import dk.shape.games.sportsbook.offerings.modules.notification.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.IOException
import java.util.*

typealias SubjectSubscription = dk.shape.games.notifications.entities.Subscription

val mockLegacySubscriptions: MutableList<Subscription> = mutableListOf(
    Subscription(
        eventId = "event:1234",
        commaSeparatedTypes = ",event_start"
    ),
    Subscription(
        eventId = "event:1235",
        commaSeparatedTypes = ",yellow_card,red_card,event_end,event_start,corner,replacement,penalty,extra_time"
    )
)

val mockSubjectSubscriptions: Set<SubjectSubscription> = listOf(
    SubjectSubscription(
        subjectId = "subject:1234",
        subjectType = SubjectType.TEAMS,
        types = listOf("event_start").toSet()
    ),
    SubjectSubscription(
        subjectId = "subject:1235",
        subjectType = SubjectType.TEAMS,
        types = listOf("event_start", "event_end","lineup_ready").toSet()
    )
).toSet()

val mockSubjectInfos = listOf(
    SubjectInfo(
        subjectId = "subject:1234",
        subjectType = SubjectType.TEAMS,
        subjectName = "Manchester",
        sportId = "sport:football"
    ),
    SubjectInfo(
        subjectId = "subject:1235",
        subjectType = SubjectType.TEAMS,
        subjectName = "Liverpool",
        sportId = "sport:football"
    )
)

val mockEvents: List<Event> = listOf(
    Event(
        id = "event:1234",
        home = "Manchester",
        away = null,
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
    ),
    Event(
        id = "event:1235",
        home = "Barcelona",
        away = "Roma",
        awayImageUrl = null,
        scheduledStartTime = Date(),
        markets = listOf(),
        name = "Barcelona - Roma",
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
            ),
            AppConfig.Notifications.NotificationGroup.NotificationType(
                identifier = "event_end",
                name = "Event ends"
            ),
            AppConfig.Notifications.NotificationGroup.NotificationType(
                identifier = "yellow_card",
                name = "Yellow card"
            ),
            AppConfig.Notifications.NotificationGroup.NotificationType(
                identifier = "red_card",
                name = "Red card"
            ),
            AppConfig.Notifications.NotificationGroup.NotificationType(
                identifier = "corner",
                name = "Corner"
            ),
            AppConfig.Notifications.NotificationGroup.NotificationType(
                identifier = "penalty",
                name = "Penalty"
            ),
            AppConfig.Notifications.NotificationGroup.NotificationType(
                identifier = "replacement",
                name = "Replacement"
            ),
            AppConfig.Notifications.NotificationGroup.NotificationType(
                identifier = "extra_time",
                name = "Extra time"
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

val mockLegacyNotificationsComponent = object : NotificationsComponentInterface {
    override fun register(token: String?, environment: String?): Promise<Any, Throwable, Void> {
        val promise: Promise<Any, Throwable, Void> = Promise()
        promise.returnValue(null)
        return promise
    }

    override fun observeSubscriptions(): Promise<Void, IOException, MutableList<Subscription>> {
        val promise: Promise<Void, IOException, MutableList<Subscription>> = Promise()
        promise.returnValue(null)
        return promise
    }

    override fun getSubscriptions(): Promise<MutableList<Subscription>, IOException, Void> {
        val promise: Promise<MutableList<Subscription>, IOException, Void> = Promise()
        promise.returnValue(mockLegacySubscriptions)
        return promise
    }

    override fun subscribe(
        eventId: String?,
        commaSeperatedTypes: String?
    ): Promise<Boolean, IOException, Void> {
        val promise: Promise<Boolean, IOException, Void> = Promise()
        promise.returnValue(true)
        return promise
    }

    override fun unsubscribe(eventId: String?): Promise<Boolean, IOException, Void> {
        val promise: Promise<Boolean, IOException, Void> = Promise()
        promise.returnValue(true)
        return promise
    }
}

val mockSubjectNotificationsDataSource = object : SubjectNotificationsDataSource {
    override suspend fun hasActiveSubscription(deviceId: String, subjectId: String): Flow<Boolean> =
        flowOf(true)

    override suspend fun updateSubjectSubscriptions(
        deviceId: String,
        subjectId: String,
        subjectType: SubjectType,
        subscribedNotificationTypeIds: Set<String>
    ) {
    }

    override suspend fun getSubscriptions(deviceId: String): Flow<Set<dk.shape.games.notifications.entities.Subscription>> =
        flowOf(mockSubjectSubscriptions)

    override suspend fun getAllSubscriptions(deviceId: String): Flow<Set<dk.shape.games.notifications.entities.Subscription>> =
        flowOf(mockSubjectSubscriptions)

    override suspend fun register(
        deviceId: String,
        platform: String,
        environment: String,
        notificationToken: String
    ) {
    }

}