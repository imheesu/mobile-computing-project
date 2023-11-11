package com.example.running_data_collecting_app.utils

sealed class UiEvent {
    data class ShowSnackBar(val message: String, val action: String? = null): UiEvent()
}
