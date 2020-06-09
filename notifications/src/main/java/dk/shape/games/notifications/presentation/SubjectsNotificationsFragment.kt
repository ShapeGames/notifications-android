package dk.shape.games.notifications.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.shape.games.notifications.actions.NotificationsAction
import dk.shape.games.notifications.databinding.FragmentSubjectNotificationsBinding
import dk.shape.games.notifications.features.list.NotificationsConfig
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config

object SubjectsNotificationsFragmentArgs : ConfigFragmentArgs<NotificationsAction, NotificationsConfig>()

class SubjectsNotificationsFragment : BottomSheetDialogFragment() {

    private val config: NotificationsConfig by config()

    private val action: NotificationsAction by action()

    private val subjectNotificationsViewModel by lazy {
        SubjectNotificationViewModel(

        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSubjectNotificationsBinding
            .inflate(layoutInflater)
            .apply {
                viewModel = subjectNotificationsViewModel
            }.root
    }


    companion object {
        fun newInstance(): SubjectsNotificationsFragment {
            return SubjectsNotificationsFragment()
        }
    }
}