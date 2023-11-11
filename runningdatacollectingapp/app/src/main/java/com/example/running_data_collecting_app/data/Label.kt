package com.example.running_data_collecting_app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Label(
    @PrimaryKey  val name: String,
)
