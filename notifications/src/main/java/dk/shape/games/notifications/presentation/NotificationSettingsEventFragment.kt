package dk.shape.games.notifications.presentation

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import dk.shape.games.notifications.R
import dk.shape.games.notifications.actions.NotificationSettingsEventAction
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.databinding.FragmentNotificationSettingsEventBinding
import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationSheetEventViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationTypeCollectionViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.toNotificationTypeInfos
import dk.shape.games.notifications.presentation.viewmodels.settings.NotificationSettingsTypesEventViewModel
import dk.shape.games.notifications.presentation.viewmodels.state.ErrorMessageViewModel
import dk.shape.games.notifications.usecases.LegacyEventNotificationsInteractor
import dk.shape.games.notifications.usecases.LegacyEventNotificationsUseCases
import dk.shape.games.notifications.utils.ExpandedBottomSheetDialogFragment
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationSettingsEventFragment : ExpandedBottomSheetDialogFragment() {
    object Args :
        ConfigFragmentArgs<NotificationSettingsEventAction, NotificationSettingsEventConfig>()

    private val config: NotificationSettingsEventConfig by config()
    private val action: NotificationSettingsEventAction by action()

    private val legacyNotificationsInteractor: LegacyEventNotificationsUseCases by lazy {
        LegacyEventNotificationsInteractor(
            config.notificationsDataSource
        )
    }

    private val errorMessageViewModel: ErrorMessageViewModel by lazy {
        ErrorMessageViewModel {
            requireActivity()
        }
    }

    private val notificationViewModel: NotificationSheetEventViewModel by lazy {
        NotificationSheetEventViewModel(
            eventId = action.eventId,
            eventInfo = action.eventInfo,
            onClosedPressed = { dismiss() },
            onPreferencesSaved = { stateData, onSuccess, onFailure ->
                lifecycleScope.launchWhenResumed {
                    withContext(Dispatchers.IO) {
                        legacyNotificationsInteractor.updateNotifications(
                            eventId = stateData.eventId,
                            notificationTypeIds = stateData.notificationTypeIds,
                            onSuccess = {
                                config.eventListener.onNotificationTypesChanged(stateData)
                                onSuccess()
                            },
                            onError = {
                                onFailure()
                                errorMessageViewModel.showErrorMessage()
                            }
                        )
                    }
                }
            }
        ).apply {
            val isActivated = action.initialActiveNotificationIds.isNotEmpty()
            val initialNotificationIds = action.initialActiveNotificationIds

            notificationTypesCollection.set(
                NotificationTypeCollectionViewModel(
                    defaultTypeIds = action.defaultNotificationIds,
                    selectedTypeIds = initialNotificationIds,
                    activatedTypeIds = initialNotificationIds,
                    possibleTypeInfos = action.possibleNotifications.toNotificationTypeInfos(),
                    selectionNotifier = notifySelection,
                    initialMasterState = isActivated
                )
            )
            headerViewModel.activeNotificationState.awareSet(isActivated)
        }
    }

    private val notificationEventViewModel: NotificationSettingsTypesEventViewModel by lazy {
        NotificationSettingsTypesEventViewModel(
            notificationViewModel = notificationViewModel,
            errorMessageViewModel = errorMessageViewModel,
            onBackPressed = {
                dismiss()
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNotificationSettingsEventBinding
        .inflate(layoutInflater)
        .apply {
            viewModel = notificationEventViewModel
        }.root

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        config.eventListener.onDismiss()
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}