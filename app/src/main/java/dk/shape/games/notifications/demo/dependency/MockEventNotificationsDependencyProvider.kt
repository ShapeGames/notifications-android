package dk.shape.games.notifications.demo.dependency

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.demo.setup.eventsRepositoryMock
import dk.shape.games.notifications.features.list.EventNotificationsConfig
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MockEventNotificationsDependencyProvider : ConfigProvider<EventNotificationsConfig> {
    override fun config(fragment: Fragment): EventNotificationsConfig {
        return EventNotificationsConfig(
            provideDeviceId = DeviceIdProviderMock::provideDeviceIdMock,
            loadingViewProvider = loadingViewProvider,
            provideNotifications = NotificationsProviderMock::provideNotificationsMock,
            notificationsDataSource = NotificationsRepositoryMock,
            eventsRepository = eventsRepositoryMock,
            eventHandler = notificationsEventHandlerMock,
            provideBetEventIds = BetIdsProviderMock::provideBetIdsMock
        )
    }
}
