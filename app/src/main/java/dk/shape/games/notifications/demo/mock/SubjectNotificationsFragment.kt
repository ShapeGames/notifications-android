package dk.shape.games.notifications.demo.mock

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

data class MockNotificationsConfig(
    val hasSportNotificationsSupport: suspend (sportId: String) -> Boolean,
    val hasNotificationsSupport: suspend (subjectId: String) -> Flow<Boolean>,
    val showNotificationsFragment: (Fragment, MockData) -> Unit,
    var notificationsEventListener: (hasNotifications: Boolean) -> Unit
)

data class MockData(
    val sportId: String,
    val subjectId: String,
    val subjectName: String,
    val subjectType: SubjectType
)

@Parcelize
object SubjectNotificationsAction : Parcelable

class SubjectNotificationsFragment : Fragment() {

    private val mockData = MockData(
        sportId = "football:0000",
        subjectId = "team:0000",
        subjectName = "Manchester United",
        subjectType = SubjectType.TEAMS
    )

    object Args : ConfigFragmentArgs<SubjectNotificationsAction, MockNotificationsConfig>()

    private val config: MockNotificationsConfig by config()

    private val mockViewModel: MockNotificationsViewModel by lazy {

        MockNotificationsViewModel(
            showNotifications = {
                config.showNotificationsFragment(this, mockData)
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
                if (config.hasSportNotificationsSupport(mockData.sportId)) {
                    mockViewModel.hasNotificationsSupport.set(true)
                    config.hasNotificationsSupport(mockData.subjectId).collect {
                        withContext(Dispatchers.Main) {
                            mockViewModel.isLoadingStatus.set(false)
                            mockViewModel.hasNotifications.set(it)
                        }
                    }
                }
                config.notificationsEventListener = {
                    mockViewModel.hasNotifications.set(it)
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