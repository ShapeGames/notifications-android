package dk.shape.games.notifications.demo.dependency

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.demo.mock.*
import dk.shape.games.notifications.presentation.NotificationSettingsSubjectConfig
import dk.shape.games.toolbox_library.configinjection.ConfigProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
class MockNotificationSettingsSubjectDependencyProvider :
    ConfigProvider<NotificationSettingsSubjectConfig> {
    @ExperimentalCoroutinesApi
    override fun config(fragment: Fragment): NotificationSettingsSubjectConfig {
        return NotificationSettingsSubjectConfig(
            notificationsDataSource = mockSubjectNotificationsDataSource,
            provideDeviceId = {
                "device:1234"
            }
        )
    }
}