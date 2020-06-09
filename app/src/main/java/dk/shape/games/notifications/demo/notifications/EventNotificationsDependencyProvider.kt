package dk.shape.games.notifications.demo.notifications

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.demo.setup.eventsRepositoryMock
import dk.shape.games.notifications.features.list.NotificationsConfig
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlin.time.ExperimentalTime

@ExperimentalTime
class EventNotificationsDependencyProvider : ConfigProvider<NotificationsConfig> {
    override fun config(fragment: Fragment): NotificationsConfig {
        return NotificationsConfig(
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
