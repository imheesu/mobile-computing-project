package com.example.running_data_collecting_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.running_data_collecting_app.data.InertialSensorDao

@Database(
    entities = [Label::class],
    version = 1,
)
abstract class InertialSensorDatabase():RoomDatabase() {
    abstract val dao: InertialSensorDao
}
