package dk.shape.games.notifications.features.types

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dk.shape.games.notifications.actions.NotificationTypesAction
import dk.shape.games.notifications.databinding.FragmentNotificationTypesBinding
import dk.shape.games.notifications.usecases.EventNotificationTypeInteractor
import dk.shape.games.notifications.usecases.EventNotificationTypesInteractor
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.android.synthetic.main.fragment_notification_types.view.*

class NotificationTypesFragment : Fragment() {

    object Args : ConfigFragmentArgs<NotificationTypesAction, NotificationTypesConfig>()

    private val config: NotificationTypesConfig by config()

    private val action: NotificationTypesAction by action()

    private val notificationTypesViewModel: NotificationTypesViewModel by lazy {
        ViewModelProvider(
            this@NotificationTypesFragment,
            NotificationTypesViewModelFactory(
                EventNotificationTypesInteractor(
                    action.eventId,
                    config.notificationsDataSource,
                    config.provideNotifications,
                    config.provideDeviceId,
                    config.eventRepository,
                    config.eventHandler::onToggleError
                ),
                config.loadingViewProvider,
                { eventId, notificationTypeId ->
                    ViewModelProvider(
                        this@NotificationTypesFragment,
                        NotificationTypeViewModelFactory(
                            EventNotificationTypeInteractor(
                                eventId,
                                notificationTypeId,
                                config.provideNotifications,
                                config.provideDeviceId,
                                config.eventRepository,
                                config.notificationsDataSource
                            )
                        )
                    ).get(
                        "$eventId|$notificationTypeId",
                        NotificationTypeViewModel::class.java
                    )
                }
            )
        ).get(NotificationTypesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentNotificationTypesBinding.inflate(inflater, container, false).also {
            it.viewModel = notificationTypesViewModel
            it.lifecycleOwner = this@NotificationTypesFragment
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.toolbar.setNavigationOnClickListener {
            config.eventHandler.onBackPress(this@NotificationTypesFragment)
        }

        super.onViewCreated(view, savedInstanceState)
    }

}