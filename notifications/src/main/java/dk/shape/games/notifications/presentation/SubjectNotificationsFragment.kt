package dk.shape.games.notifications.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.games.notifications.R
import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.aliases.NotifificationsLoadedListener
import dk.shape.games.notifications.databinding.FragmentSubjectNotificationsBinding
import dk.shape.games.notifications.extensions.awareSet
import dk.shape.games.notifications.extensions.launch
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationSheetViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationSwitcherViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationTypeCollectionViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationViewModel
import dk.shape.games.notifications.usecases.SubjectNotificationUseCases
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.coroutines.Dispatchers

class SubjectNotificationsFragment : BottomSheetDialogFragment() {

    object Args : ConfigFragmentArgs<SubjectNotificationsAction, SubjectNotificationsConfig>()

    private val config: SubjectNotificationsConfig by config()
    private val action: SubjectNotificationsAction by action()

    private val interactor: SubjectNotificationUseCases by lazy {
        SubjectNotificationInteractor(
            action = action,
            provideDeviceId = config.provideDeviceId,
            provideNotifications = config.provideNotifications,
            notificationsDataSource = config.notificationsDataSource,
            notificationsEventHandler = config.eventHandler
        )
    }

    private val viewSwitcherViewModel: SubjectNotificationSwitcherViewModel by lazy {
        SubjectNotificationSwitcherViewModel()
    }

    private val notificationViewModel: SubjectNotificationViewModel by lazy {
        SubjectNotificationViewModel(
            subjectId = action.subjectId,
            subjectType = action.subjectType,
            subjectName = action.subjectName,
            onClosedPressed = {
                config.eventHandler.onClosed(fragment = this, action = action)
            },
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

    private val notificationsSheetViewModel: SubjectNotificationSheetViewModel by lazy {
        SubjectNotificationSheetViewModel(
            screenTitle = config.screenTitle(),
            notificationViewModel = notificationViewModel,
            notificationSwitcherViewModel = viewSwitcherViewModel,
            onClosedPressed = {
                config.eventHandler.onClosed(fragment = this, action = action)
            }
        )
    }

    private suspend fun loadNotifications(interactor: SubjectNotificationUseCases) {
        val onLoaded: NotifificationsLoadedListener =
            { activatedTypes, possibleTypes, defaultTypes ->
                val activeIdentifiers = activatedTypes.map { it.identifier }.toSet()
                val initialIdentifiers = if (defaultTypes.isNotEmpty()) {
                    defaultTypes.toSet()
                } else activeIdentifiers.toSet()

                notificationViewModel.apply {
                    notificationTypesCollection.set(
                        SubjectNotificationTypeCollectionViewModel(
                            defaultIdentifiers = initialIdentifiers,
                            selectedIdentifiers = activeIdentifiers.toSet(),
                            activatedTypes = activatedTypes,
                            possibleTypes = possibleTypes,
                            selectionNotifier = notificationViewModel.notifySelection,
                            initialMasterState = activatedTypes.isNotEmpty()
                        )
                    )
                    activeNotificationState.awareSet(activatedTypes.isNotEmpty())
                    viewSwitcherViewModel.showContent(notificationViewModel)
                }
            }
        interactor.loadNotifications(
            onLoaded = onLoaded,
            onFailure = {
                with(viewSwitcherViewModel) {
                    showError {
                        launch(Dispatchers.IO) {
                            whenStarted { showLoading() }
                            whenResumed { loadNotifications(interactor) }
                        }
                    }
                }
            }
        )
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        launch(viewSwitcherViewModel, Dispatchers.IO) {
            whenStarted { showLoading() }
            whenResumed { loadNotifications(interactor) }
        }
        return FragmentSubjectNotificationsBinding
            .inflate(layoutInflater)
            .apply { viewModel = notificationsSheetViewModel }.root
    }
}