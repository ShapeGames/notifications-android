package dk.shape.games.notifications.presentation

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.actions.NotificationSettingsEventAction
import dk.shape.games.notifications.actions.NotificationSettingsSubjectAction
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.presentation.viewmodels.state.StateDataSubject
import dk.shape.games.notifications.repositories.SubjectNotificationsDataSource
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.sportsbook.offerings.modules.notification.NotificationsComponentInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi

// TODO: add documentation once API is stable

@ExperimentalCoroutinesApi
data class NotificationSettingsConfig(
    val legacyNotificationsComponent: NotificationsComponentInterface,

    val subjectNotificationsDataSource: SubjectNotificationsDataSource,

    val provideEventIdsForUserBetsAsync: ((List<String>?) -> Unit) -> Unit,

    val provideEventIdsForBetSlip: () -> List<String>?,

    val provideSubjectInfo: suspend (Set<Subscription>) -> (List<SubjectInfo>)?,

    val provideAppConfig: () -> AppConfig,

    val provideEvents: suspend (eventIds: List<String>) -> List<Event>,

    val provideDeviceId: suspend () -> String,

    val onBackPressed: () -> Unit,

    val onSubjectNotificationTypesClicked: (Fragment, NotificationSettingsSubjectAction, (StateDataSubject) -> Unit) -> Unit,

    val onEventNotificationTypesClicked: (Fragment, NotificationSettingsEventAction) -> Unit
)