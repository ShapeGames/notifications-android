package dk.shape.games.notifications.demo.notifications

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.demo.setup.eventsRepositoryMock
import dk.shape.games.notifications.features.list.EventNotificationsConfig
import dk.shape.games.notifications.features.list.SubjectNotificationsConfig
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlin.time.ExperimentalTime

@ExperimentalTime
class SubjectNotificationsDependencyProvider : ConfigProvider<SubjectNotificationsConfig> {
    override fun config(fragment: Fragment): SubjectNotificationsConfig {
        return SubjectNotificationsConfig(
            screenTitle = { "Notifications" },
            provideDeviceId = SubjectDeviceIdProviderMock::provideDeviceIdMock,
            provideNotifications = SubjectNotificationsProviderMock::provideNotificationsMock,
            notificationsDataSource = SubjectNotificationsRepositoryMock,
            eventHandler = subjectNotificationsEventHandlerMock
        )
    }
}
