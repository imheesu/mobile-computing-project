package com.example.running_data_collecting_app.data

import com.example.running_data_collecting_app.data.InertialSensorDao
import kotlinx.coroutines.flow.Flow

class InertialSensorRepositoryImpl(
    private val dao: InertialSensorDao
):InertialSensorRepository {
    override suspend fun insertLabel(label: Label) {
       dao.insertLabel(label)
    }

    override suspend fun deleteLabel(label: Label) {
        dao.deleteLabel(label)
    }

    override fun getLabels(): Flow<List<Label>> {
        return dao.getLabels()
    }
}