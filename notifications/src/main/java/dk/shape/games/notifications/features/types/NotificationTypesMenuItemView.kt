package dk.shape.games.notifications.features.types

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatImageButton
import dk.shape.games.notifications.R
import dk.shape.games.sportsbook.offerings.uiutils.navigation.Key
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * A custom view for notifications [MenuItem] that facilitates displaying notifications icon in
 * a [Menu] within Offerings framework only when notifications are available.
 *
 * It was made in order to make it easy to implement Notifications feature into Event Details, use
 * this custom view to display Notifications icon inside Event Details.
 */
class NotificationTypesMenuItemView : AppCompatImageButton {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(
        context,
        attributeSet,
        defStyle
    )

    private val availabilityCheckCoroutineScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + Dispatchers.IO
    }
    private var availabilityCheckJob: Job? = null

    init {
        val outValue = TypedValue()
        context.theme
            .resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
        setBackgroundResource(outValue.resourceId)
        setImageResource(R.drawable.ic_notifications)
    }

    /**
     * Sets up the icon for the toolbar.
     *
     * @param keyLifecycle [Key.KeyLifecycle] is commonly used inside Offerings framework. Event Details uses it to notify about lifecycle events. You should obtain it in the menu builder (Event Details menu builder will provide you this).
     * @param refreshToolbar because [Menu] might not display layout changes, like visibility changes of this icon unless you rebuild the [Menu], this method should provide implementation for rebuilding the menu. Event Details will provide you this method in the menu builder API.
     * @param checkAvailable a suspending function which allows to determine if the notification icon should be displayed or not
     * @param provideMenuItem a method which returns current instance of [MenuItem] associated with this custom view.
     */
    fun setup(
        keyLifecycle: Key.KeyLifecycle,
        refreshToolbar: (context: Context) -> Unit,
        checkAvailable: suspend () -> Boolean,
        provideMenuItem: () -> MenuItem
    ) {
        keyLifecycle.setOnLifecycleEvent {
            when (it) {
                Key.LifecycleEvent.FOREGROUND -> {
                    availabilityCheckJob = availabilityCheckCoroutineScope.launch(
                        CoroutineExceptionHandler { _, _ ->
                            setIsAvailable(false, refreshToolbar, provideMenuItem)
                        }
                    ) {
                        setIsAvailable(checkAvailable(), refreshToolbar, provideMenuItem)
                    }
                }
                Key.LifecycleEvent.BACKGROUND -> {
                    if (availabilityCheckJob?.isActive == true) availabilityCheckJob?.cancel()
                }
                else -> {
                }
            }
        }
    }

    private fun setIsAvailable(
        isAvailable: Boolean,
        refreshToolbar: ((context: Context) -> Unit)? = null,
        provideMenuItem: () -> MenuItem
    ) {
        provideMenuItem().let {
            val changed = isAvailable != it.isVisible
            if (changed) {
                it.isVisible = isAvailable
                refreshToolbar?.invoke(context)
            }

        }
    }

}