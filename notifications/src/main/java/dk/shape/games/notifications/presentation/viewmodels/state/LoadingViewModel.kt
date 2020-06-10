package dk.shape.games.notifications.presentation.viewmodels.state

import dk.shape.danskespil.module.ui.ModuleDiffInterface

class LoadingViewModel : ModuleDiffInterface {
    override fun compareString(): String = "LOADING_VIEW_MODEL"
    override fun compareContentString(): String = "LOADING_VIEW_MODEL"
}