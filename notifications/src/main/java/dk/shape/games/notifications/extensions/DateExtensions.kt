package dk.shape.games.notifications.extensions

import dk.shape.games.toolbox_library.utils.RelativeDateUtils
import dk.shape.games.uikit.databinding.UIText
import java.util.*

internal fun Date.toDateText(): UIText = UIText.ByContext { context ->
    UIText.Raw.String(
        RelativeDateUtils.getRelativeDateString(context, this, false)
    )
}

internal fun Date.toTimeText(): UIText = UIText.ByContext { context ->
    UIText.Raw.String(RelativeDateUtils.getTimeString(context, this))
}