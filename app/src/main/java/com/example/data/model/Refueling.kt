package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "refuelings",
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
data class Refueling(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shiftId: Long,
    val liters: Double,
    val costAriary: Double,
    val stationName: String = "Station",
    val timestamp: Long = System.currentTimeMillis()
)
