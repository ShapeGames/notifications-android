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
import dk.shape.games.notifications.actions.EventInfo
import dk.shape.games.notifications.demo.databinding.FragmentMockNotificationsBinding
import dk.shape.games.toolbox_library.configinjection.ConfigFragmentArgs
import dk.shape.games.toolbox_library.configinjection.action
import dk.shape.games.toolbox_library.configinjection.config
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

data class MockEventParentNotificationsConfig(
    val hasGroupNotificationsSupport: suspend (groupId: String) -> Boolean,
    val hasNotificationsSupport: suspend (eventId: String) -> Flow<Boolean>,
    val showNotificationsFragment: (Fragment, MockEventParentData) -> Unit,
    var notificationsEventListener: (hasNotifications: Boolean) -> Unit
)

data class MockEventParentData(
    val eventId: String,
    val groupId: String,
    val eventInfo: EventInfo
)

@Parcelize
object EventParentNotificationsAction : Parcelable

class MockEventNotificationsParentFragment : Fragment() {

    private val mockData = MockEventParentData(
        eventId = "event:1234",
        groupId = "sport:football",
        eventInfo = EventInfo(
            sportIconName = "icon-category-football",
            homeName = "Manchester",
            awayName = "Barcelona",
            startDate = Date(),
            level2Name = "Premier League",
            level3Name = "England"
        )
    )

    object Args : ConfigFragmentArgs<EventParentNotificationsAction, MockEventParentNotificationsConfig>()

    private val config: MockEventParentNotificationsConfig by config()

    private val action: EventParentNotificationsAction by action()

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
                if (config.hasGroupNotificationsSupport(mockData.groupId)) {
                    mockViewModel.hasNotificationsSupport.set(true)
                    config.hasNotificationsSupport(mockData.eventId).collect {
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