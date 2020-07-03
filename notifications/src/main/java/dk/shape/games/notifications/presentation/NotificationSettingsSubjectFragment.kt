package dk.shape.games.notifications.presentation

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.whenResumed
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.games.notifications.R
import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.aliases.NotificationsLoadedListener
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.bindings.launch
import dk.shape.games.notifications.databinding.FragmentNotificationSettingsSubjectBinding
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationTypeCollectionViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationViewModel
import dk.shape.games.notifications.presentation.viewmodels.settings.NotificationSettingsSubjectMainViewModel
import dk.shape.games.notifications.usecases.SubjectNotificationUseCases
import dk.shape.games.notifications.utils.ExpandableBottomSheetDialogFragment
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.coroutines.Dispatchers

class NotificationSettingsSubjectFragment : ExpandableBottomSheetDialogFragment() {

    object Args : ConfigFragmentArgs<SubjectNotificationsAction, SubjectNotificationsConfig>()

    private val config: SubjectNotificationsConfig by config()
    private val action: SubjectNotificationsAction by action()

    private val interactor: SubjectNotificationUseCases by lazy {
        SubjectNotificationsInteractor(
            action = action,
            provideDeviceId = config.provideDeviceId,
            notificationsProvider = config.provideNotifications,
            notificationsDataSource = config.notificationsDataSource,
            notificationsEventHandler = config.eventHandler
        )
    }

    private val notificationSubjectViewModel: NotificationSettingsSubjectMainViewModel by lazy {
        NotificationSettingsSubjectMainViewModel(
            notificationViewModel = getInitialSubjectViewModel()!!,
            onBackPressed = {
                dismiss()
            }
        )
    }

    private fun getInitialSubjectViewModel(): SubjectNotificationViewModel? =
        config.provideNotificationsNow()?.find {
            it.sportId == action.sportId
        }?.notificationTypes?.let { notificationTypesForSport ->
            notificationViewModel.apply {
                notificationTypesCollection.set(
                    SubjectNotificationTypeCollectionViewModel(
                        defaultIdentifiers = emptySet(),
                        selectedIdentifiers = emptySet(),
                        activatedIdentifiers = emptySet(),
                        possibleTypes = notificationTypesForSport.toSet(),
                        selectionNotifier = notifySelection,
                        initialMasterState = false
                    )
                )
                activeNotificationState.awareSet(false)
            }
        }

    private val notificationViewModel: SubjectNotificationViewModel by lazy {
        SubjectNotificationViewModel(
            subjectId = action.subjectId,
            subjectType = action.subjectType,
            subjectName = action.subjectName,
            onClosedPressed = { dismiss() },
            onPreferencesSaved = { stateData, onSuccess, onFailure ->
                launch(interactor, Dispatchers.IO) {
                    whenResumed {
                        saveNotificationPreferences(
                            stateData = stateData,
                            onSuccess = onSuccess,
                            onFailure = { onFailure() }
                        )
                    }
                }
            }
        )
    }

    private val onNotificationsLoaded: NotificationsLoadedListener =
        { activatedTypes, possibleTypes, defaultTypes ->
            val activeIdentifiers = activatedTypes.map { it.identifier }.toSet()
            val initialIdentifiers = if (defaultTypes.isNotEmpty()) {
                defaultTypes.toSet()
            } else activeIdentifiers.toSet()

            with(notificationViewModel) {
                notificationTypesCollection.set(
                    SubjectNotificationTypeCollectionViewModel(
                        defaultIdentifiers = initialIdentifiers,
                        selectedIdentifiers = activeIdentifiers.toSet(),
                        activatedIdentifiers = activeIdentifiers,
                        possibleTypes = possibleTypes,
                        selectionNotifier = notificationViewModel.notifySelection,
                        initialMasterState = activatedTypes.isNotEmpty()
                    )
                )
                activeNotificationState.awareSet(activatedTypes.isNotEmpty())
            }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launch(Dispatchers.IO) {
            whenResumed {
                loadNotifications(interactor)
            }
        }
    }

    private suspend fun loadNotifications(interactor: SubjectNotificationUseCases) {
        interactor.loadNotifications(
            onLoaded = onNotificationsLoaded,
            onFailure = {}
        )
    }

    private fun onRetry() {
        launch(Dispatchers.IO) {
            whenResumed {
                loadNotifications(interactor)
            }
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private fun BottomSheetDialogFragment.requireBottomSheetView(): ViewGroup? =
        requireView().parent as? ViewGroup?

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        config.eventHandler.onDismissed()
    }

    override fun dismiss() {
        super.dismiss()
        config.eventHandler.onDismissed()
    }
}