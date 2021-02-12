package dk.shape.games.notifications.presentation

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import dk.shape.games.notifications.R
import dk.shape.games.notifications.actions.NotificationSettingsSubjectAction
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.databinding.FragmentNotificationSettingsSubjectBinding
import dk.shape.games.notifications.extensions.toStrings
import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationSheetSubjectViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationTypeCollectionViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.toNotificationTypeInfos
import dk.shape.games.notifications.presentation.viewmodels.settings.NotificationSettingsTypesSubjectViewModel
import dk.shape.games.notifications.presentation.viewmodels.state.ErrorMessageViewModel
import dk.shape.games.notifications.usecases.SubjectSettingsNotificationsInteractor
import dk.shape.games.notifications.usecases.SubjectSettingsNotificationsUseCases
import dk.shape.games.notifications.utils.ExpandedBottomSheetDialogFragment
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationSettingsSubjectFragment : ExpandedBottomSheetDialogFragment() {
    object Args :
        ConfigFragmentArgs<NotificationSettingsSubjectAction, NotificationSettingsSubjectConfig>()

    private val config: NotificationSettingsSubjectConfig by config()
    private val action: NotificationSettingsSubjectAction by action()

    private val notificationsInteractor: SubjectSettingsNotificationsUseCases by lazy {
        SubjectSettingsNotificationsInteractor(
            config.notificationsDataSource
        )
    }

    private val errorMessageViewModel: ErrorMessageViewModel by lazy {
        ErrorMessageViewModel {
            requireActivity()
        }
    }

    private val notificationViewModel: NotificationSheetSubjectViewModel by lazy {
        NotificationSheetSubjectViewModel(
            subjectId = action.subjectId,
            subjectType = action.subjectType,
            subjectName = action.subjectName,
            isHeaderTitleVisible = false,
            onClosedPressed = { dismiss() },
            onPreferencesSaved = { stateData, onSuccess, onFailure ->
                lifecycleScope.launchWhenResumed {

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
                    defaultTypeIds = action.defaultNotificationTypeIds.toStrings(),
                    selectedTypeIds = initialNotificationIds.toStrings(),
                    activatedTypeIds = initialNotificationIds.toStrings(),
                    possibleTypeInfos = action.possibleNotifications.toNotificationTypeInfos(),
                    selectionNotifier = notifySelection,
                    initialMasterState = isActivated
                )
            )
            headerViewModel.activeNotificationState.awareSet(isActivated)
        }
    }

    private val notificationSubjectViewModel: NotificationSettingsTypesSubjectViewModel by lazy {
        NotificationSettingsTypesSubjectViewModel(
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