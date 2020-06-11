package dk.shape.games.notifications.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.games.notifications.R
import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.aliases.SelectionStateNotifier
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.databinding.FragmentSubjectNotificationsBinding
import dk.shape.games.notifications.features.list.SubjectNotificationsConfig
import dk.shape.games.notifications.presentation.viewmodels.notifications.*
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationSheetViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationSwitcherViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationTypeCollectionViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.SubjectNotificationViewModel
import dk.shape.games.notifications.presentation.viewmodels.notifications.toNotificationTypeViewModel
import dk.shape.games.notifications.usecases.SubjectNotificationUseCases
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.coroutines.launch

class SubjectNotificationsFragment : BottomSheetDialogFragment() {

    object Args : ConfigFragmentArgs<SubjectNotificationsAction, SubjectNotificationsConfig>()

    private val config: SubjectNotificationsConfig by config()
    private val action: SubjectNotificationsAction by action()

    private val interactor: SubjectNotificationUseCases by lazy {
        SubjectNotificationInteractor(
            action = action,
            coroutineScope = lifecycleScope,
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
                config.eventHandler.onClosed(this, action)
            },
            onPreferencesSaved = { stateData, onSuccess, onFailure ->
                interactor.saveNotificationPreferences(
                    stateData = stateData,
                    onSuccess = onSuccess,
                    onFailure = { onFailure() }
                )
            }
        )
    }

    private val notificationsSheetViewModel: SubjectNotificationSheetViewModel by lazy {
        SubjectNotificationSheetViewModel(
            screenTitle = config.screenTitle(),
            notificationViewModel = notificationViewModel,
            notificationSwitcherViewModel = viewSwitcherViewModel,
            onClosedPressed = {
                config.eventHandler.onClosed(this, action)
            }
        )
    }

    private suspend fun loadNotifications() {
        interactor.loadNotifications(
            onLoaded = { activatedTypes, possibleTypes, defaultTypes ->
                val activeIdentifiers = activatedTypes.map { it.identifier }.toSet()

                notificationViewModel.apply {
                    notificationTypesCollection.set(
                        SubjectNotificationTypeCollectionViewModel(
                            subjectId = action.subjectId,
                            subjectType = action.subjectType,
                            defaultIdentifiers = defaultTypes,
                            initialIdentifiers = activeIdentifiers,
                            selectedIdentifiers = activeIdentifiers,
                            activatedTypes = activatedTypes,
                            possibleTypes = possibleTypes,
                            selectionNotifier = notificationViewModel.notifySelection
                        )
                    )

                    activeNotificationState.set(activatedTypes.isNotEmpty())
                    viewSwitcherViewModel.showContent(notificationViewModel)
                }
            },
            onFailure = {
                viewSwitcherViewModel.showError {
                    lifecycleScope.launch {
                        loadNotifications()
                    }
                }
            }
        )
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        lifecycleScope.launch {
            whenStarted { viewSwitcherViewModel.showLoading() }
            whenResumed { loadNotifications() }
        }
        return FragmentSubjectNotificationsBinding
            .inflate(layoutInflater)
            .apply {
                viewModel = notificationsSheetViewModel
            }.root
    }
}