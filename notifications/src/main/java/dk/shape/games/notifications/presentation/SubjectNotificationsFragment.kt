package dk.shape.games.notifications.presentation

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.games.notifications.R
import dk.shape.games.notifications.actions.SubjectNotificationsAction
import dk.shape.games.notifications.aliases.NotificationsLoadedListener
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.bindings.launch
import dk.shape.games.notifications.databinding.FragmentSubjectNotificationsBinding
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
        SubjectNotificationsInteractor(
            action = action,
            provideDeviceId = config.provideDeviceId,
            notificationsProvider = config.provideNotifications,
            notificationsDataSource = config.notificationsDataSource,
            notificationsEventHandler = config.eventHandler
        )
    }

    private val viewSwitcherViewModel: SubjectNotificationSwitcherViewModel by lazy {
        SubjectNotificationSwitcherViewModel(
            initialContentItem = getInitialSubjectViewModel(),
            onItemChanged = {
                requireBottomSheetView()?.let { bottomSheetView ->
                    TransitionManager.beginDelayedTransition(
                        bottomSheetView,
                        AutoTransition().setInterpolator(FastOutSlowInInterpolator())
                    )
                }
            })
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

    private val notificationsSheetViewModel: SubjectNotificationSheetViewModel by lazy {
        SubjectNotificationSheetViewModel(
            notificationViewModel = notificationViewModel,
            notificationSwitcherViewModel = viewSwitcherViewModel,
            onBackPressed = { dismiss() }
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
                viewSwitcherViewModel.showContent(this)
            }
        }

    private suspend fun loadNotifications(interactor: SubjectNotificationUseCases) {
        interactor.loadNotifications(
            onLoaded = onNotificationsLoaded,
            onFailure = {
                viewSwitcherViewModel.showError {
                    onRetry()
                }
            }
        )
    }

    private fun onRetry() {
        launch(Dispatchers.IO) {
            whenStarted {
                viewSwitcherViewModel.showLoading()
            }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentSubjectNotificationsBinding
        .inflate(layoutInflater)
        .apply {
            viewModel = notificationsSheetViewModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launch(Dispatchers.IO) {
            whenResumed {
                loadNotifications(interactor)
            }
        }
    }
}