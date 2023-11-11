package com.example.running_data_collecting_app.utils

import com.example.running_data_collecting_app.data.Label

sealed class LabelEvent {
    data class SelectLabel(val label: Label): LabelEvent()
    data class AddLabel(val label: Label): LabelEvent()
    data class DeleteLabel(val label:Label): LabelEvent()
    object onChooseClcik: LabelEvent()
}