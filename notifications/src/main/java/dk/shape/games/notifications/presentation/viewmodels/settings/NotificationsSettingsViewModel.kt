package dk.shape.games.notifications.presentation.viewmodels.settings

import dk.shape.games.notifications.presentation.viewmodels.state.ErrorMessageViewModel

data class NotificationsSettingsViewModel(
        val toolbarViewModel: NotificationsToolbarViewModel,
        val switcherViewModel: NotificationsSettingsSwitcherViewModel,
        val errorMessageViewModel: ErrorMessageViewModel
)