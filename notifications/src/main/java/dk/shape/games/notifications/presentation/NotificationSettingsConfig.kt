package dk.shape.games.notifications.presentation

import androidx.fragment.app.Fragment
import dk.shape.games.notifications.actions.LegacyEventNotificationTypesAction
import dk.shape.games.notifications.actions.SubjectNotificationTypesAction
import dk.shape.games.notifications.entities.Subscription
import dk.shape.games.notifications.usecases.LegacyEventNotificationsUseCases
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import dk.shape.games.notifications.usecases.SubjectSettingsNotificationsUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi

// TODO: add documentation once API is stable

@ExperimentalCoroutinesApi
data class NotificationSettingsConfig(
    val legacyEventNotificationsUseCasesProvider: () -> LegacyEventNotificationsUseCases,

    val subjectSettingUseCasesProvider: () -> SubjectSettingsNotificationsUseCases,

    val provideEventIdsForUserBetsAsync: ((List<String>?) -> Unit) -> Unit,

    val provideEventIdsForBetSlip: () -> List<String>?,

    val provideSubjectInfo: suspend (Set<Subscription>) -> (List<SubjectInfo>)?,

    val provideAppConfig: () -> AppConfig,

    val provideEvents: suspend (eventIds: List<String>) -> List<Event>,

    val provideDeviceId: suspend () -> String,

    val onBackPressed: () -> Unit,

    val onSubjectNotificationTypesClicked: (Fragment, SubjectNotificationTypesAction) -> Unit,

    val onEventNotificationTypesClicked: (Fragment, LegacyEventNotificationTypesAction) -> Unit
)