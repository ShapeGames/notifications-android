package dk.shape.games.notifications.extensions

import android.os.Build
import android.util.TypedValue
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import dk.shape.games.notifications.R
import kotlin.math.abs

private const val DEBOUNCE_DELAY_MS = 300L
private val DEBOUNCE_CLICK_TAG = "DEBOUNCE_CLICK".hashCode()

@BindingAdapter("visible")
internal fun View.setVisible(visible: Boolean?) {
    this.visibility = if (visible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("onStateChange")
internal fun AppCompatCheckBox.onStateChange(onStateChange: CompoundButton.OnCheckedChangeListener) {
    this.setOnCheckedChangeListener(onStateChange)
}

@BindingAdapter("onStateChange")
internal fun SwitchCompat.onStateChange(onStateChange: CompoundButton.OnCheckedChangeListener) {
    this.setOnCheckedChangeListener(onStateChange)
}


@BindingAdapter(
    value = ["app:showRipple", "app:borderlessRipple", "app:backgroundRipple"],
    requireAll = false
)
internal fun View.setShowRipple(
    showRipple: Boolean = false,
    borderless: Boolean = false,
    backgroundRipple: Boolean = false
) {
    if (showRipple && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val outValue = TypedValue()
        val attribute = if (borderless) R.attr.selectableItemBackgroundBorderless else R.attr.selectableItemBackground

        context.theme.resolveAttribute(attribute, outValue, true)

        if (backgroundRipple) {
            background = ContextCompat.getDrawable(context, outValue.resourceId)
        } else {
            foreground = ContextCompat.getDrawable(context, outValue.resourceId)
        }
    }
}

@BindingAdapter("onDebounceClick")
fun View.setOnDebounceClick(onDebounceClick: (() -> Unit)?) {
    if (onDebounceClick == null) return

    if (!hasOnClickListeners()) {
        setOnClickListener {
            val currentTimestamp = System.currentTimeMillis()
            val previousClickTimestamp = it.getTag(DEBOUNCE_CLICK_TAG) as? Long?

            val elapsedTime = abs(currentTimestamp - (previousClickTimestamp ?: currentTimestamp))

            if (previousClickTimestamp == null || elapsedTime >= DEBOUNCE_DELAY_MS) {
                it.setTag(DEBOUNCE_CLICK_TAG, currentTimestamp + DEBOUNCE_DELAY_MS)
                onDebounceClick()
            }
        }
    }
}
