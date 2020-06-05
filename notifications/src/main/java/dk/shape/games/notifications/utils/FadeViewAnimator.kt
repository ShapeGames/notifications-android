package dk.shape.games.notifications.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ViewAnimator

class FadeViewAnimator : ViewAnimator {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    init {
        animateFirstView = false
        setInAnimation(context, android.R.anim.fade_in)
        setOutAnimation(context, android.R.anim.fade_out)
    }

}