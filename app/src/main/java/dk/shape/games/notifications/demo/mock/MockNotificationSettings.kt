package dk.shape.games.notifications.demo.mock

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
        commaSeparatedTypes = "event_start"
    )
)

val mockSubjectSubscriptions: Set<SubjectSubscription> = listOf<SubjectSubscription>(
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
        notificationConfigurationId = "",
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

val mockAppConfig: AppConfig = AppConfig(
    betSlipConfig = BetSlipConfig(),
    event = EventConfig(),
    teamSubjectNotifications = subjectNotifications
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