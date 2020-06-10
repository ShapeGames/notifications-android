package dk.shape.games.notifications.presentation.viewmodels.state

import dk.shape.danskespil.module.ui.ModuleDiffInterface
import dk.shape.games.notifications.R
import dk.shape.games.uikit.databinding.UIImage
import dk.shape.games.uikit.databinding.UIText

data class ErrorViewModel(
    val title: UIText = UIText.Raw.Resource(R.string.error_title),
    val buttonText: UIText = UIText.Raw.Resource(R.string.error_try_again),
    val description: UIText? = null,
    val icon: UIImage = UIImage.Raw.Resource(R.drawable.ic_signal_icon),
    val onRetryClick: (() -> Unit)? = null
) : ModuleDiffInterface {

    override fun compareString(): String = "ERROR_VIEW_MODEL"
    override fun compareContentString(): String = "ERROR_VIEW_MODEL"
}