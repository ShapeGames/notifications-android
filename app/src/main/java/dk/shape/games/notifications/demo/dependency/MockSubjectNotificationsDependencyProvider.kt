package dk.shape.games.notifications.demo.dependency

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.presentation.SubjectNotificationsConfig
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi

class MockSubjectNotificationsDependencyProvider : ConfigProvider<SubjectNotificationsConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment) = SubjectNotificationsConfig(
        provideDeviceId = SubjectDeviceIdProviderMock::provideDeviceIdMock,
        provideNotifications = SubjectNotificationsProviderMock::provideNotificationsMock,
        provideNotificationsNow = { subjectNotifications },
        notificationsDataSource = SubjectNotificationsRepositoryMock,
        eventHandler = SubjectNotificationsEventHandlerMock,
        onTrackNotificationSaved = {
            val tot = 0
        }
    )
}
