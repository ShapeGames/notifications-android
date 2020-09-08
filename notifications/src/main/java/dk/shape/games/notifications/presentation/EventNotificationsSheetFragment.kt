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
import dk.shape.games.notifications.actions.EventNotificationsSheetAction
import dk.shape.games.notifications.aliases.EventNotificationsLoadedListener
import dk.shape.games.notifications.bindings.awareSet
import dk.shape.games.notifications.bindings.launch
import dk.shape.games.notifications.databinding.FragmentEventNotificationsSheetBinding
import dk.shape.games.notifications.presentation.viewmodels.notifications.*
import dk.shape.games.notifications.presentation.viewmodels.notifications.NotificationTypeCollectionViewModel
import dk.shape.games.notifications.presentation.viewmodels.state.ErrorMessageViewModel
import dk.shape.games.notifications.usecases.LegacyEventNotificationsInteractor
import dk.shape.games.notifications.usecases.LegacyEventNotificationsUseCases
import dk.shape.games.notifications.utils.ExpandedBottomSheetDialogFragment
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.coroutines.Dispatchers

class EventNotificationsSheetFragment : ExpandedBottomSheetDialogFragment() {

    object Args : ConfigFragmentArgs<EventNotificationsSheetAction, EventNotificationsSheetConfig>()

    private val config: EventNotificationsSheetConfig by config()
    private val action: EventNotificationsSheetAction by action()

    private val eventNotificationInteractor: LegacyEventNotificationsUseCases by lazy {
        LegacyEventNotificationsInteractor(
            config.notificationsDataSource
        )
    }

    private val viewSwitcherViewModel: EventNotificationSwitcherViewModel by lazy {
        EventNotificationSwitcherViewModel(
            initialContentItem = getInitialEventViewModel(),
            onItemChanged = {
                requireBottomSheetView()?.let { bottomSheetView ->
                    TransitionManager.beginDelayedTransition(
                        bottomSheetView,
                        AutoTransition().setInterpolator(FastOutSlowInInterpolator())
                    )
                }
            })
    }

    private val errorMessageViewModel = ErrorMessageViewModel {
        requireActivity()
    }

    private fun getInitialEventViewModel(): NotificationSheetEventViewModel? =
        config.provideNotificationsNow()?.find {
            it.groupId == action.groupId
        }?.notificationTypes?.let { possibleTypes ->
            notificationViewModel.apply {
                notificationTypesCollection.set(
                    NotificationTypeCollectionViewModel(
                        defaultTypeIds = emptySet(),
                        selectedTypeIds = emptySet(),
                        activatedTypeIds = emptySet(),
                        possibleTypeInfos = possibleTypes.toNotificationTypeInfos(),
                        selectionNotifier = notifySelection,
                        initialMasterState = false
                    )
                )
                headerViewModel.activeNotificationState.awareSet(false)
            }
        }

    private val notificationViewModel: NotificationSheetEventViewModel by lazy {
        NotificationSheetEventViewModel(
            eventId = action.eventId,
            eventInfo = action.eventInfo,
            onClosedPressed = { dismiss() },
            onPreferencesSaved = { stateData, onSuccess, onFailure ->
                launch(eventNotificationInteractor, Dispatchers.IO) {
                    whenResumed {
                        updateNotifications(
                            eventId = stateData.eventId,
                            notificationTypeIds = stateData.notificationTypeIds,
                            onSuccess = {
                                onSuccess()
                                config.eventHandler.onSubscriptionsUpdated(
                                    eventId = action.eventId,
                                    hasActiveSubscriptions = stateData.notificationTypeIds.isNotEmpty()
                                )
                            },
                            onError = {
                                onFailure()
                                errorMessageViewModel.showErrorMessage()
                            }
                        )
                    }
                }
            }
        )
    }

    private val notificationsSheetViewModel: EventNotificationSheetViewModel by lazy {
        EventNotificationSheetViewModel(
            notificationViewModel = notificationViewModel,
            notificationSwitcherViewModel = viewSwitcherViewModel,
            errorMessageViewModel = errorMessageViewModel,
            onBackPressed = {
                dismiss()
            }
        )
    }

    private val onNotificationsLoaded: EventNotificationsLoadedListener =
        { activatedTypeIds, possibleTypes, defaultTypesIds ->
            val initialTypeIds = if (defaultTypesIds.isNotEmpty()) {
                defaultTypesIds
            } else activatedTypeIds

            with(notificationViewModel) {
                notificationTypesCollection.set(
                    NotificationTypeCollectionViewModel(
                        selectedTypeIds = activatedTypeIds,
                        defaultTypeIds = initialTypeIds,
                        activatedTypeIds = activatedTypeIds,
                        possibleTypeInfos = possibleTypes.toNotificationTypeInfos(),
                        selectionNotifier = notificationViewModel.notifySelection,
                        initialMasterState = activatedTypeIds.isNotEmpty()
                    )
                )
                headerViewModel.activeNotificationState.awareSet(activatedTypeIds.isNotEmpty())
                viewSwitcherViewModel.showContent(this)
            }
        }

    private suspend fun loadNotifications() {
        eventNotificationInteractor.loadSubscription(
            eventId = action.eventId,
            notificationGroupId = action.groupId,
            provideNotifications = config.provideNotifications,
            onSuccess = onNotificationsLoaded,
            onError = {
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
                loadNotifications()
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
    ) = FragmentEventNotificationsSheetBinding
        .inflate(layoutInflater)
        .apply {
            viewModel = notificationsSheetViewModel
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launch(Dispatchers.IO) {
            whenResumed {
                loadNotifications()
            }
        }
    }
}