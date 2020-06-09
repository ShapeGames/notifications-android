package dk.shape.games.notifications.features.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dk.shape.games.notifications.actions.EventNotificationsAction
import dk.shape.games.notifications.databinding.FragmentEventNotificationsBinding
import dk.shape.games.notifications.usecases.EventNotificationInteractor
import dk.shape.games.notifications.usecases.EventNotificationsInteractor
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.android.synthetic.main.fragment_event_notifications.view.*

class EventNotificationsFragment : Fragment() {

    object Args : ConfigFragmentArgs<EventNotificationsAction, NotificationsConfig>()

    private val config: NotificationsConfig by config()

    private val action: EventNotificationsAction by action()

    private val notificationsViewModel: EventNotificationsViewModel by lazy {
        ViewModelProvider(
            this@EventNotificationsFragment,
            NotificationsViewModelFactory(
                EventNotificationsInteractor(
                    config.notificationsDataSource,
                    config.eventsRepository,
                    config.provideDeviceId,
                    config.provideBetEventIds,
                    config.provideNotifications,
                    action.includePlacements,
                    action.filterEventIds
                ),
                { event ->
                    ViewModelProvider(
                        this@EventNotificationsFragment,
                        NotificationViewModelFactory(
                            EventNotificationInteractor(
                                event,
                                config.provideNotifications,
                                config.notificationsDataSource,
                                config.provideDeviceId,
                                config.eventHandler::onToggleError
                            )
                        )
                    ).get(
                        event.id,
                        EventNotificationViewModel::class.java
                    )
                },
                config.loadingViewProvider
            )
        ).get(EventNotificationsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentEventNotificationsBinding.inflate(inflater, container, false).also {
            it.viewModel = notificationsViewModel
            it.lifecycleOwner = this@EventNotificationsFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.toolbar.setNavigationOnClickListener {
            config.eventHandler.onBackPress(this@EventNotificationsFragment)
        }

        notificationsViewModel.configurationEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { eventId ->
                config.eventHandler.onConfigurationClick(this@EventNotificationsFragment, eventId)
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

}