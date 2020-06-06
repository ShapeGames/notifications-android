package dk.shape.games.notifications.demo.setup

import dk.shape.componentkit2.Promise
import dk.shape.componentkit2.Result
import dk.shape.danskespil.foundation.cache.MemoryDataCache
import dk.shape.games.notifications.demo.mock.MockEventService
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfigComponentInterface
import dk.shape.games.sportsbook.offerings.common.appconfig.BetSlipConfig
import dk.shape.games.sportsbook.offerings.common.appconfig.EventConfig
import dk.shape.games.sportsbook.offerings.generics.event.data.EventRepository
import dk.shape.games.sportsbook.offerings.generics.event.data.EventsRepository
import dk.shape.games.sportsbook.offerings.generics.event.data.eventRespository
import dk.shape.games.sportsbook.offerings.generics.event.data.eventsRepository
import dk.shape.games.sportsbook.offerings.modules.event.data.synchronization.CompactEventSynchronizationComponent
import dk.shape.games.sportsbook.offerings.modules.event.data.synchronization.EventSynchronizationComponent
import dk.shape.games.sportsbook.offerings.modules.event.data.synchronization.OddsSynchronizationComponent
import java.io.IOException

internal val appConfigC: AppConfigComponentInterface =
    object : AppConfigComponentInterface {
        private val appConfig =
            AppConfig(betSlipConfig = BetSlipConfig(), event = EventConfig()).also {
                val notificationsField =
                    AppConfig::class.java.getDeclaredField("notifications")
                notificationsField.isAccessible = true
                notificationsField.set(
                    it,
                    AppConfig.Notifications(listOf<AppConfig.Notifications.NotificationGroup>())
                )
            }

        override fun getNow(): AppConfig? = appConfig

        override val appConfiguration: Promise<AppConfig, IOException, Void>
            get() = Promise<AppConfig, IOException, Void>(
                Result.success(
                    appConfig
                )
            )
    }

internal val eventRepositoryMock: EventRepository = eventRespository(
    cache = MemoryDataCache(),
    service = MockEventService(),
    appConfigComponent = appConfigC,
    eventSync = EventSynchronizationComponent(
        CompactEventSynchronizationComponent(
            OddsSynchronizationComponent()
        )
    )
)

internal val eventsRepositoryMock: EventsRepository = eventsRepository(
    cache = MemoryDataCache(),
    service = MockEventService()
)







