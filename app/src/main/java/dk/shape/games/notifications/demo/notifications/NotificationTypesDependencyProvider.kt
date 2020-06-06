package dk.shape.games.notifications.demo.notifications

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.demo.setup.eventRepositoryMock
import dk.shape.games.notifications.features.types.NotificationTypesConfig
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlin.time.ExperimentalTime

@ExperimentalTime
class NotificationTypesDependencyProvider : ConfigProvider<NotificationTypesConfig> {
    override fun config(fragment: Fragment): NotificationTypesConfig {
        return NotificationTypesConfig(
            provideDeviceId = DeviceIdProviderMock::provideDeviceIdMock,
            notificationsDataSource = NotificationsRepositoryMock,
            eventHandler = NotificationTypesEventHandlerMock,
            loadingViewProvider = loadingViewProvider,
            eventRepository = eventRepositoryMock,
            provideNotifications = NotificationsProviderMock::provideNotificationsMock
        )
    }
}
