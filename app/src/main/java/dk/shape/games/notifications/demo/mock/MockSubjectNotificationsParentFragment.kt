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
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MockSubjectNotificationsConfig(
    val hasSportNotificationsSupport: suspend (sportId: String) -> Boolean,
    val hasNotificationsSupport: suspend (subjectId: String) -> Flow<Boolean>,
    val showNotificationsFragment: (Fragment, MockSubjectData) -> Unit,
    var notificationsEventListener: (hasNotifications: Boolean) -> Unit
)

data class MockSubjectData(
    val sportId: String,
    val subjectId: String,
    val subjectName: String,
    val subjectType: SubjectType
)

@Parcelize
data class SubjectNotificationsAction (
    val isError: Boolean = false
): Parcelable

class MockSubjectNotificationsParentFragment : Fragment() {

    private val mockData = MockSubjectData(
        sportId = "football:0000",
        subjectId = "team:0000",
        subjectName = "Manchester United",
        subjectType = SubjectType.TEAMS
    )

    private val mockErrorData = MockSubjectData(
        sportId = "",
        subjectId = "",
        subjectName = "Subject name",
        subjectType = SubjectType.TEAMS
    )

    object Args : ConfigFragmentArgs<SubjectNotificationsAction, MockSubjectNotificationsConfig>()

    private val config: MockSubjectNotificationsConfig by config()

    private val action: SubjectNotificationsAction by action()

    private val mockViewModel: MockNotificationsViewModel by lazy {

        MockNotificationsViewModel(
            showNotifications = {
                if(action.isError){
                    config.showNotificationsFragment(this, mockErrorData)
                } else config.showNotificationsFragment(this, mockData)
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