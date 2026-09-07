package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shifts")
data class Shift(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val isActive: Boolean = true,
    val initialFuelLiters: Double = 5.0,
    val remainingFuelLiters: Double = 5.0,
    val startOdometer: Double = 0.0,
    val endOdometer: Double = 0.0,
    val directDistanceKm: Double = 0.0,
    val distanceMode: String = "GPS", // "GPS", "ODOMETER", "DIRECT"
    val targetVersementAr: Double = 30000.0, // Tanjona versement ho an'ny tompony
    val ownerPhoneNumber: String = "",
    val notes: String = ""
)
