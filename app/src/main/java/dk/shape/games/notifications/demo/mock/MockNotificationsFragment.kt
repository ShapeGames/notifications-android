package dk.shape.games.notifications.demo.mock

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenResumed
import androidx.lifecycle.whenStarted
import dk.shape.games.notifications.demo.databinding.FragmentMockNotificationsBinding
import dk.shape.games.notifications.entities.SubjectType
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias NotificationsFragmentProvider = (fragment: Fragment, sportId: String, subjectId: String, subjectName: String, subjectType: SubjectType) -> DialogFragment

data class MocktNotificationsConfig(
    val isNotificationsSupported: suspend (deviceId: String, subjectId: String) -> Flow<Boolean>,
    val showNotificationsFragment: NotificationsFragmentProvider,
    var notificationEventListener: (hasNotifications: Boolean) -> Unit,
    var notificationsClosedListener: (DialogFragment) -> Unit
)

@Parcelize
object MocktNotificationsAction : Parcelable

class MockNotificationsFragment : Fragment() {

    object Args : ConfigFragmentArgs<MocktNotificationsAction, MocktNotificationsConfig>()

    private val config: MocktNotificationsConfig by config()

    private val mockViewModel: MockNotificationsViewModel by lazy {

        MockNotificationsViewModel(
            showNotifications = {
                config.showNotificationsFragment(
                    this,
                    "football:0000",
                    "team:0000",
                    "Manchester United",
                    SubjectType.TEAMS
                )
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch {
            whenStarted { mockViewModel.isLoadingStatus.set(true) }
            whenResumed {
                config.isNotificationsSupported("team:0000", "team:0000").collect {
                    withContext(Dispatchers.Main) {
                        mockViewModel.isLoadingStatus.set(false)
                        mockViewModel.hasNotifications.set(it)
                    }
                }
                config.notificationEventListener = {
                    mockViewModel.hasNotifications.set(it)
                }
                config.notificationsClosedListener = {
                    it.dismiss()
                }
            }
        }
        return FragmentMockNotificationsBinding
            .inflate(layoutInflater)
            .apply {
                viewModel = mockViewModel
            }.root
    }
}