package dk.shape.games.notifications.presentation

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import dk.shape.games.notifications.R
import dk.shape.games.notifications.actions.NotificationSettingsEventAction
import dk.shape.games.notifications.actions.NotificationSettingsSubjectAction
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.databinding.FragmentNotificationSettingsSubjectBinding
import dk.shape.games.notifications.extensions.toIds
import dk.shape.games.notifications.extensions.toTypeIds
import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationTypeCollectionViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.toNotificationTypeInfos
import dk.shape.games.notifications.presentation.viewmodels.settings.NotificationSettingsSubjectViewModel
import dk.shape.games.notifications.usecases.LegacyEventNotificationsInteractor
import dk.shape.games.notifications.usecases.LegacyEventNotificationsUseCases
import dk.shape.games.notifications.usecases.SubjectSettingsNotificationsInteractor
import dk.shape.games.notifications.usecases.SubjectSettingsNotificationsUseCases
import dk.shape.games.notifications.utils.ExpandableBottomSheetDialogFragment
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationSettingsEventFragment : ExpandableBottomSheetDialogFragment(
    paddingTopRes = R.dimen.expandable_sheet_padding_top
) {
    object Args :
        ConfigFragmentArgs<NotificationSettingsEventAction, NotificationSettingsEventConfig>()

    private val config: NotificationSettingsEventConfig by config()
    private val action: NotificationSettingsEventAction by action()

    private val legacyNotificationsInteractor: LegacyEventNotificationsUseCases by lazy {
        LegacyEventNotificationsInteractor(
            config.notificationsDataSource
        )
    }

    private lateinit var notificationViewModel: SubjectNotificationViewModel

    /*private val notificationViewModel: SubjectNotificationViewModel by lazy {
        SubjectNotificationViewModel(
            subjectId = action.subjectId,
            subjectType = action.subjectType,
            subjectName = action.subjectName,
            onClosedPressed = { dismiss() },
            onPreferencesSaved = { stateData, onSuccess, onFailure ->
                lifecycleScope.launch {
                    whenResumed {
                        withContext(Dispatchers.IO) {
                            notificationsInteractor.updateNotifications(
                                deviceId = config.provideDeviceId(),
                                subjectId = stateData.subjectId,
                                subjectType = stateData.subjectType,
                                notificationTypeIds = stateData.notificationTypeIds.toSet(),
                                onSuccess = {
                                    config.eventListener.onNotificationTypesChanged(stateData)
                                    onSuccess()
                                },
                                onError = onFailure
                            )
                        }
                    }
                }
            }
        ).apply {
            val isActivated = action.initialActiveNotifications.isNotEmpty()
            val initialNotificationIds = action.initialActiveNotifications.toIds()

            notificationTypesCollection.set(
                NotificationTypeCollectionViewModel(
                    defaultTypeIds = initialNotificationIds.toTypeIds(),
                    selectedTypeIds = initialNotificationIds.toTypeIds(),
                    activatedTypeIds = initialNotificationIds.toTypeIds(),
                    possibleTypeInfos = action.possibleNotifications.toNotificationTypeInfos(),
                    selectionNotifier = notifySelection,
                    initialMasterState = isActivated
                )
            )
            activeNotificationState.awareSet(isActivated)
        }
    }*/

    private val notificationSubjectViewModel: NotificationSettingsSubjectViewModel by lazy {
        NotificationSettingsSubjectViewModel(
            notificationViewModel = notificationViewModel,
            onBackPressed = {
                dismiss()
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNotificationSettingsSubjectBinding
        .inflate(layoutInflater)
        .apply {
            viewModel = notificationSubjectViewModel
        }.root

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        config.eventListener.onDismiss()
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}