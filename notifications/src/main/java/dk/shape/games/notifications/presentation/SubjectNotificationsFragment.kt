package dk.shape.games.notifications.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.danskespil.foundation.entities.PolyIcon
import dk.shape.games.notifications.actions.StatsNotificationsAction
import dk.shape.games.notifications.aliases.StatsNotificationGroup
import dk.shape.games.notifications.aliases.StatsNotificationIdentifier
import dk.shape.games.notifications.aliases.StatsNotificationType
import dk.shape.games.notifications.databinding.FragmentSubjectNotificationsBinding
import dk.shape.games.notifications.features.list.StatsNotificationsConfig
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import dk.shape.games.uikit.databinding.UIImage

class SubjectNotificationsFragment : BottomSheetDialogFragment() {

    object SubjectsNotificationsFragmentArgs : ConfigFragmentArgs<StatsNotificationsAction, StatsNotificationsConfig>()

    private val config: StatsNotificationsConfig by config()
    private val action: StatsNotificationsAction by action()

    private val interactor: SubjectNotificationInteractor by lazy {
        SubjectNotificationInteractor(
            coroutineScope = lifecycleScope,
            notificationsDataSource = config.notificationsDataSource,
            notificationsEventHandler = config.eventHandler
        )
    }

    private val subjectNotificationsViewModel by lazy {
        SubjectNotificationViewModel(
            subjectId = action.subjectId,
            subjectType = action.subjectType,
            subjectName = action.subjectName,
            subjectInitialNotificationState = action.hasNotifications,
            notificationTypes = config.provideNotifications().group.toNotificationTypes(
                sportId = action.sportId,
                hasNotifications = action.hasNotifications
            ),
            onClosedPressed = {
                config.eventHandler.onClosed(action)
            },
            onPreferencesSaved = { stateSubject, onFailed, onSuccess ->

            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycleScope.launchWhenResumed {

        }
        return FragmentSubjectNotificationsBinding
            .inflate(layoutInflater)
            .apply {
                viewModel = subjectNotificationsViewModel
            }.root
    }


    private fun List<StatsNotificationGroup>.toNotificationTypes(sportId: String, hasNotifications: Boolean): List<SubjectNotificationTypeViewModel> {
        val notificationGroup = firstOrNull { it.sportId == sportId } ?: return emptyList()

        return notificationGroup.notificationTypes.map { it.toNotificationTypeViewModel(
            hasNotifications = hasNotifications,
            defaultTypeIdentifiers = notificationGroup.defaultNotificationTypeIdentifiers
        ) }
    }

    private fun StatsNotificationType.toNotificationTypeViewModel(
        hasNotifications: Boolean,
        defaultTypeIdentifiers: List<StatsNotificationIdentifier>
    ) = SubjectNotificationTypeViewModel(
        icon = icon.toLocalUIImage(),
        identifier = identifier,
        initialState = defaultTypeIdentifiers.any { it == identifier } && hasNotifications,
        notificationName = name
    )

    private fun PolyIcon.Resource?.toLocalUIImage() = UIImage.byResourceName(underscoreName)

    private val PolyIcon.Resource?.underscoreName: String
        get() = (this?.name ?: "").replace('-', '_')
}