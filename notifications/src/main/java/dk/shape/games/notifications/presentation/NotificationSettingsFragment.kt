package dk.shape.games.notifications.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import dk.shape.games.notifications.R
import dk.shape.games.notifications.actions.NotificationSettingsAction
import dk.shape.games.notifications.databinding.FragmentNotificationSettingsBinding
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.notifications.extensions.toTypeIds
import dk.shape.games.notifications.presentation.viewmodels.settings.*
import dk.shape.games.notifications.usecases.LegacyEventNotificationsInteractor
import dk.shape.games.notifications.usecases.LegacyEventNotificationsUseCases
import dk.shape.games.notifications.usecases.SubjectSettingsNotificationsInteractor
import dk.shape.games.notifications.usecases.SubjectSettingsNotificationsUseCases
import dk.shape.games.sportsbook.offerings.common.appconfig.AppConfig
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import dk.shape.games.uikit.databinding.UIText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class NotificationSettingsFragment : Fragment() {

    object Args : ConfigFragmentArgs<NotificationSettingsAction, NotificationSettingsConfig>()

    private val action: NotificationSettingsAction by action()
    private val config: NotificationSettingsConfig by config()

    private lateinit var savedEventIds: List<String>

    private val legacyNotificationsInteractor: LegacyEventNotificationsUseCases by lazy {
        LegacyEventNotificationsInteractor(
            config.legacyNotificationsComponent
        )
    }

    private val subjectNotificationsInteractor: SubjectSettingsNotificationsUseCases by lazy {
        SubjectSettingsNotificationsInteractor(
            config.subjectNotificationsDataSource
        )
    }

    private val switcherViewModel: NotificationsSettingsSwitcherViewModel =
        NotificationsSettingsSwitcherViewModel.Loading

    private val toolbarViewModel: NotificationsToolbarViewModel by lazy {
        NotificationsToolbarViewModel.Settings(
            onBackPressed = config.onBackPressed
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentNotificationSettingsBinding.inflate(layoutInflater).apply {
            viewModel = NotificationsSettingsViewModel(toolbarViewModel, switcherViewModel)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        action.getEventIds { eventIds ->
            fetchNotifications(eventIds)
        }
    }

    @ExperimentalTime
    private fun fetchNotifications(eventIds: List<String>?) {
        switcherViewModel.setLoading()

        lifecycleScope.launch {
            whenResumed {
                withContext(Dispatchers.IO) {
                    try {
                        val appConfig = config.provideAppConfig()
                        val deviceId = config.provideDeviceId()

                        val eventNotificationViewModels =
                            getEventNotificationsViewModels(eventIds, appConfig)

                        val statsNotificationViewModels =
                            getSubjectNotificationsViewModels(deviceId, appConfig)

                        val hasSectionHeaders =
                            eventNotificationViewModels.isNotEmpty() && statsNotificationViewModels.isNotEmpty()

                        val eventHeaderViewModel = listOfNotNull(
                            if (hasSectionHeaders) {
                                NotificationsSettingsHeaderViewModel(
                                    title = UIText.Raw.Resource(R.string.notification_type_event),
                                    isFirst = true
                                )
                            } else null
                        )

                        val statsHeaderViewModel = listOfNotNull(
                            if (hasSectionHeaders) {
                                NotificationsSettingsHeaderViewModel(
                                    title = UIText.Raw.Resource(R.string.notification_type_stats)
                                )
                            } else null
                        )

                        val viewModels =
                            eventHeaderViewModel + eventNotificationViewModels + statsHeaderViewModel + statsNotificationViewModels

                        withContext(Dispatchers.Main) {
                            if (viewModels.isNotEmpty()) {
                                switcherViewModel.setContent(viewModels)
                            } else switcherViewModel.setEmpty()
                        }

                    } catch (e: Exception) {
                        if (e is IOException || e is HttpException || e is IllegalArgumentException) {
                            withContext(Dispatchers.Main) {
                                switcherViewModel.setError {
                                    fetchNotifications(savedEventIds)
                                }
                            }
                        } else throw e
                    }
                }
            }
        }
    }

    private suspend fun getEventNotificationsViewModels(
        eventIds: List<String>?,
        appConfig: AppConfig
    ): List<NotificationsSettingsEventViewModel> {
        val subscriptions = legacyNotificationsInteractor.getAllSubscriptions()
        val subscribedEventIds = eventIds ?: subscriptions.map { it.eventId }
        savedEventIds = subscribedEventIds

        return config.provideEvents(subscribedEventIds).mapNotNull { event ->

            appConfig.notifications.group.find { notificationGroup ->
                notificationGroup.groupId == event.notificationConfigurationId
            }?.let { matchingGroup ->

                subscriptions.find { subscription ->
                    subscription.eventId == event.id
                }?.takeIf { subscription ->
                    subscription.commaSeparatedTypes.isNotEmpty()
                }?.let { matchingSubscription ->
                    NotificationsSettingsEventViewModel(
                        event = event,
                        subscription = matchingSubscription,
                        notificationGroup = matchingGroup,
                        onEventNotificationTypesClicked = { action ->
                            config.onEventNotificationTypesClicked(this, action)
                        },
                        onSetNotifications = { notificationIds, onError ->
                            legacyNotificationsInteractor.updateNotifications(
                                eventId = event.id,
                                notificationTypeIds = notificationIds,
                                onError = onError
                            )
                        }
                    )
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @FlowPreview
    @ExperimentalTime
    private suspend fun getSubjectNotificationsViewModels(
        deviceId: String,
        appConfig: AppConfig
    ): List<NotificationsSettingsSubjectViewModel> =
        subjectNotificationsInteractor.getAllSubscriptions(deviceId).first().let { subscriptions ->
            config.provideSubjectInfo(subscriptions)?.sortedBy { subjectInfo ->
                subjectInfo.subjectName
            }?.mapNotNull { subjectInfo ->

                subscriptions.find { subscription ->
                    subscription.subjectId == subjectInfo.subjectId
                }?.takeIf { subscription ->
                    subscription.types.isNotEmpty()
                }?.let { matchingSubscription ->
                    val statsNotificationGroups = appConfig.toStatsNotifications(subjectInfo)

                    statsNotificationGroups.find { notificationGroup ->
                        notificationGroup.sportId == subjectInfo.sportId
                    }?.let { matchingGroup ->
                        NotificationsSettingsSubjectViewModel(
                            name = subjectInfo.subjectName,
                            subscription = matchingSubscription,
                            notificationGroup = matchingGroup,
                            onSubjectNotificationTypesClicked = { action ->
                                config.onSubjectNotificationTypesClicked(this, action)
                            },
                            onSetNotifications = { notificationTypes, onError ->
                                lifecycleScope.launch {
                                    whenResumed {
                                        withContext(Dispatchers.IO) {
                                            subjectNotificationsInteractor.updateNotifications(
                                                deviceId = deviceId,
                                                subjectId = matchingSubscription.subjectId,
                                                subjectType = matchingSubscription.subjectType,
                                                notificationTypeIds = notificationTypes.toTypeIds(),
                                                onError = onError
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        } ?: listOf()

    private fun NotificationSettingsAction.getEventIds(onResult: (List<String>?) -> Unit) {
        when {
            betslipComponentUUID != null -> {
                onResult(config.provideEventIdsForBetSlip())
            }
            openedFromMyGames -> {
                config.provideEventIdsForUserBetsAsync(onResult)
            }
            else -> onResult(null)
        }
    }

    private fun AppConfig.toStatsNotifications(info: SubjectInfo): List<AppConfig.SubjectNotificationGroup> =
        when (info.subjectType) {
            SubjectType.TEAMS -> teamSubjectNotifications
            SubjectType.ATHLETES -> athleteSubjectNotifications
            else -> throw IllegalArgumentException("Notifications are only supported for teams and athletes at the moment.")
        }
}

data class SubjectInfo(
    val subjectId: String,
    val subjectType: SubjectType,
    val subjectName: String,
    val sportId: String
)
