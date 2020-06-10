package dk.shape.games.notifications.presentation

import android.view.View
import android.widget.CompoundButton
import dk.shape.games.notifications.aliases.PreferencesSaveAction
import dk.shape.games.notifications.entities.SubjectType

internal data class SubjectNotificationViewModel(
    val subjectId: String,
    val subjectName: String,
    val subjectType: SubjectType,
    val subjectInitialNotificationState: Boolean,
    val notificationTypes: List<SubjectNotificationTypeViewModel>,
    private val onClosedPressed: () -> Unit,
    private val onPreferencesSaved: PreferencesSaveAction
) {
    private val stateDataSubject: () -> SubjectNotificationStateData = {
        SubjectNotificationStateData(
            subjectId = subjectId,
            subjectType = subjectType,
            isNotificationActive = isActivated,
            notificationTypeIdentifiers = notificationTypes.filter { it.isActivated }
                .map { it.identifier }
        )
    }

    var isActivated: Boolean = subjectInitialNotificationState

    val onStateChangeListener = CompoundButton.OnCheckedChangeListener { _, isChecked ->
        isActivated = isChecked
    }

    val onClosePressedListener = View.OnClickListener {
        onClosedPressed()
    }

    val onPreferencesSavedListener = View.OnClickListener {

        onPreferencesSaved(stateDataSubject(), {

        }, {
            onClosedPressed()
        })
    }
}