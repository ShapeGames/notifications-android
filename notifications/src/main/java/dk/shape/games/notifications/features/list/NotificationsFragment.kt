package dk.shape.games.notifications.features.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dk.shape.games.notifications.actions.NotificationsAction
import dk.shape.games.notifications.databinding.FragmentNotificationsBinding
import dk.shape.games.notifications.usecases.NotificationInteractor
import dk.shape.games.notifications.usecases.NotificationsInteractor
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.android.synthetic.main.fragment_notifications.view.*

class NotificationsFragment : Fragment() {

    object Args : ConfigFragmentArgs<NotificationsAction, NotificationsConfig>()

    private val config: NotificationsConfig by config()

    private val action: NotificationsAction by action()

    private val notificationsViewModel: NotificationsViewModel by lazy {
        ViewModelProvider(
            this@NotificationsFragment,
            NotificationsViewModelFactory(
                NotificationsInteractor(
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
                        this@NotificationsFragment,
                        NotificationViewModelFactory(
                            NotificationInteractor(
                                event,
                                config.provideNotifications,
                                config.notificationsDataSource,
                                config.provideDeviceId,
                                config.eventHandler::onToggleError
                            )
                        )
                    ).get(
                        event.id,
                        NotificationViewModel::class.java
                    )
                },
                config.loadingViewProvider
            )
        ).get(NotificationsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentNotificationsBinding.inflate(inflater, container, false).also {
            it.viewModel = notificationsViewModel
            it.lifecycleOwner = this@NotificationsFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.toolbar.setNavigationOnClickListener {
            config.eventHandler.onBackPress(this@NotificationsFragment)
        }

        notificationsViewModel.configurationEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { eventId ->
                config.eventHandler.onConfigurationClick(this@NotificationsFragment, eventId)
            }
        })
        super.onViewCreated(view, savedInstanceState)
    }

}