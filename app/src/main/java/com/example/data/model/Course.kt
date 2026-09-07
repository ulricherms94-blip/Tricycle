package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "courses",
    foreignKeys = [
        ForeignKey(
            entity = Shift::class,
            parentColumns = ["id"],
            childColumns = ["shiftId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["shiftId"])]
)
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shiftId: Long,
    val departure: String,
    val destination: String,
    val fareAriary: Double,
    val distanceKm: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val passengerCount: Int = 1,
    val notes: String = ""
)
