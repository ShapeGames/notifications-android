package dk.shape.games.notifications.usecases

import dk.shape.componentkit.bridge.coroutines.await
import dk.shape.componentkit2.ComponentKit
import dk.shape.games.notifications.aliases.Notifications
import dk.shape.games.notifications.repositories.EventNotificationsDataSource
import dk.shape.games.sportsbook.offerings.generics.event.data.EventRepository
import dk.shape.games.sportsbook.offerings.modules.event.data.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class EventNotificationTypesInteractor(
    private val eventId: String,
    private val notificationsDataSource: EventNotificationsDataSource,
    private val provideNotifications: suspend () -> Notifications,
    private val provideDeviceId: suspend () -> String,
    private val eventRepository: EventRepository,
    private val onMainToggleError: (e: Throwable) -> Unit
) : EventNotificationTypesUseCases {

    private val mutableState: BroadcastChannel<EventNotificationTypesState> =
        BroadcastChannel(Channel.CONFLATED)
    override val state: Flow<EventNotificationTypesState> = mutableState.asFlow()

    private var enabledNotificationTypes: Set<String>? = null
    private val enabledNotificationTypesMutex = Mutex(false)

    private var currentUpdateJob: Job? = null

    override suspend fun toggleNotificationType(
        notificationTypeId: String,
        enabled: Boolean
    ) {
        try {
            withContext(Dispatchers.Default) {
                if (enabledNotificationTypes == null) {
                    loadEnabledNotificationTypes(getEvent().notificationConfigurationId)
                }
                val currentlyEnabledTypes = enabledNotificationTypes ?: emptySet()

                enabledNotificationTypesMutex.withLock {
                    enabledNotificationTypes = if (enabled) {
                        currentlyEnabledTypes + notificationTypeId
                    } else {
                        currentlyEnabledTypes - notificationTypeId
                    }
                }

                if (currentUpdateJob?.isActive == true) currentUpdateJob?.cancel()
                currentUpdateJob = async(Dispatchers.IO) {
                    val newNotificationTypeIds = enabledNotificationTypes ?: emptySet()
                    notificationsDataSource.updateEventSubscriptions(
                        provideDeviceId(),
                        eventId,
                        newNotificationTypeIds.toSet()
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onMainToggleError(e)
            }
            // To make it simple, we just reload our screen if an error happens to stay in sync
            loadNotificationTypes()
        }
    }

    override suspend fun loadNotificationTypes() {
        mutableState.sendBlocking(EventNotificationTypesState.Loading)
        try {
            withContext(Dispatchers.IO) {
                val notifications = provideNotifications()
                val event = getEvent()
                val eventNotificationTypes = notifications.group
                    .find { it.groupId == event.notificationConfigurationId }
                    ?.notificationTypes?.toSet() ?: emptySet()
                loadEnabledNotificationTypes(getEvent().notificationConfigurationId)
                mutableState.sendBlocking(
                    EventNotificationTypesState.Content.create(
                        event,
                        eventNotificationTypes
                    )
                )
            }
        } catch (e: Exception) {
            mutableState.sendBlocking(EventNotificationTypesState.Error)
        }
    }

    private suspend fun getEvent(): Event {
        return eventRepository.getData(eventId).await(ComponentKit.createBackgroundExecutor())
    }

    private suspend fun loadEnabledNotificationTypes(notificationConfigurationId: String) {
        val deviceId = provideDeviceId()
        val eventNotificationTypes = provideNotifications().group
            .find { it.groupId == notificationConfigurationId }?.notificationTypes
        enabledNotificationTypes = (notificationsDataSource.getSubscriptions(deviceId).first()
            .find { it.eventId == eventId }
            ?.types?.mapNotNull { type -> eventNotificationTypes?.find { it.identifier == type } }
            ?: emptyList())
            .map { it.identifier }.toSet()
    }

}