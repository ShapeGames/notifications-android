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
import dk.shape.games.notifications.extensions.toIds
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
import retrofit2.HttpException
import java.io.IOException
import kotlin.time.ExperimentalTime

@FlowPreview
@ExperimentalTime
@ExperimentalCoroutinesApi
class NotificationSettingsFragment : Fragment() {

    object Args : ConfigFragmentArgs<NotificationSettingsAction, NotificationSettingsConfig>()

    private val action: NotificationSettingsAction by action()
    private val config: NotificationSettingsConfig by config()

    private var savedEventIds: List<String>? = null

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
    ): List<NotificationsSettingsEventViewModel> =
        legacyNotificationsInteractor.loadAllSubscriptions(
            eventIds = eventIds,
            appConfig = appConfig,
            onSaveEventIds = { subscribedEventIds ->
                savedEventIds = subscribedEventIds
            },
            provideEvents = config.provideEvents
        ).map { loadedSubscription ->
            loadedSubscription.toNotificationsSettingsEventViewModel(
                onEventNotificationTypesClicked = { action ->
                    config.onEventNotificationTypesClicked(
                        this@NotificationSettingsFragment,
                        action
                    ) { stateData ->
                        with(stateData) {
                            switcherViewModel.findEventViewModel(eventId)?.update(this)
                        }
                    }
                },
                onSetNotifications = { notificationIds, onError ->
                    legacyNotificationsInteractor.updateNotifications(
                        eventId = loadedSubscription.event.id,
                        notificationTypeIds = notificationIds,
                        onSuccess = {},
                        onError = onError
                    )
                }
            )
        }

    private suspend fun getSubjectNotificationsViewModels(
        deviceId: String,
        appConfig: AppConfig
    ): List<NotificationsSettingsSubjectViewModel> =
        subjectNotificationsInteractor.loadAllSubscriptions(
            deviceId,
            appConfig,
            config.provideSubjectInfo
        ).map { loadedSubscription ->
            loadedSubscription.toNotificationsSettingsSubjectViewModel(
                onSubjectNotificationTypesClicked = { action ->
                    config.onSubjectNotificationTypesClicked(
                        this,
                        action
                    ) { stateData ->
                        with(stateData) {
                            switcherViewModel.findSubjectViewModel(subjectId)?.update(this)
                        }
                    }
                },
                onSetNotifications = { notificationTypes, onError ->
                    lifecycleScope.launch {
                        whenResumed {
                            withContext(Dispatchers.IO) {
                                subjectNotificationsInteractor.updateNotifications(
                                    deviceId = deviceId,
                                    subjectId = loadedSubscription.subscription.subjectId,
                                    subjectType = loadedSubscription.subscription.subjectType,
                                    notificationTypeIds = notificationTypes.toIds(),
                                    onSuccess = {},
                                    onError = onError
                                )
                            }
                        }
                    }
                }
            )
        }

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
}

data class SubjectInfo(
    val subjectId: String,
    val subjectType: SubjectType,
    val subjectName: String,
    val sportId: String
)
