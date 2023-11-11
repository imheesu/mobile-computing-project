package com.example.running_data_collecting_app.data

import kotlinx.coroutines.flow.Flow

interface InertialSensorRepository {
    suspend fun insertLabel(label: Label)

    suspend fun deleteLabel(label:Label)

    fun getLabels(): Flow<List<Label>>
}