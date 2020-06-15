package dk.shape.games.notifications.bindings

import dk.shape.danskespil.foundation.entities.PolyIcon
import dk.shape.games.uikit.databinding.UIImage

internal fun PolyIcon.Resource?.toLocalUIImage() = UIImage.byResourceName(underscoreName)

internal val PolyIcon.Resource?.underscoreName: String
    get() = (this?.name ?: "").replace('-', '_')