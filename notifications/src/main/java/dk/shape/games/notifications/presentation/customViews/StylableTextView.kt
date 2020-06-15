package dk.shape.games.notifications.presentation.customViews

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import dk.shape.games.notifications.R

class StylableTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var normalFont: String? = null
    private var activeFont: String? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StylableTextView)
        try {
            normalFont = typedArray.getString(R.styleable.StylableTextView_normal_state_font)
            activeFont = typedArray.getString(R.styleable.StylableTextView_active_state_font)
        } finally {
            typedArray.recycle()
        }
    }

    fun setIsActive(isActive: Boolean) {
        typeface = if (isActive) {
            Typeface.create(activeFont, Typeface.NORMAL)
        } else {
            Typeface.create(normalFont, Typeface.NORMAL)
        }
    }
}