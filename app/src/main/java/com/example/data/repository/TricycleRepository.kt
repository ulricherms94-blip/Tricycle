package com.example.data.repository

import com.example.data.dao.TricycleDao
import com.example.data.model.Course
import com.example.data.model.Refueling
import com.example.data.model.Shift
import kotlinx.coroutines.flow.Flow

class TricycleRepository(private val dao: TricycleDao) {

    val activeShift: Flow<Shift?> = dao.getActiveShift()
    val allShifts: Flow<List<Shift>> = dao.getAllShifts()

    fun getCoursesForShift(shiftId: Long): Flow<List<Course>> = dao.getCoursesForShift(shiftId)
    fun getRefuelingsForShift(shiftId: Long): Flow<List<Refueling>> = dao.getRefuelingsForShift(shiftId)

    suspend fun getShiftById(id: Long): Shift? = dao.getShiftById(id)

    suspend fun startNewShift(
        initialFuel: Double,
        startOdometer: Double,
        targetVersement: Double,
        ownerPhone: String,
        distanceMode: String
    ): Long {
        val shift = Shift(
            startTime = System.currentTimeMillis(),
            isActive = true,
            initialFuelLiters = initialFuel,
            remainingFuelLiters = initialFuel,
            startOdometer = startOdometer,
            endOdometer = startOdometer,
            directDistanceKm = 0.0,
            distanceMode = distanceMode,
            targetVersementAr = targetVersement,
            ownerPhoneNumber = ownerPhone
        )
        return dao.insertShift(shift)
    }

    suspend fun updateShift(shift: Shift) {
        dao.updateShift(shift)
    }

    suspend fun endShift(shiftId: Long, remainingFuel: Double, endOdometer: Double) {
        val current = dao.getShiftById(shiftId) ?: return
        val updated = current.copy(
            isActive = false,
            endTime = System.currentTimeMillis(),
            remainingFuelLiters = remainingFuel,
            endOdometer = if (endOdometer > 0) endOdometer else current.endOdometer
        )
        dao.updateShift(updated)
    }

    suspend fun addCourse(course: Course): Long {
        return dao.insertCourse(course)
    }

    suspend fun deleteCourse(course: Course) {
        dao.deleteCourse(course)
    }

    suspend fun addRefueling(refueling: Refueling): Long {
        return dao.insertRefueling(refueling)
    }

    suspend fun deleteRefueling(refueling: Refueling) {
        dao.deleteRefueling(refueling)
    }

    suspend fun deleteShift(shift: Shift) {
        dao.deleteShift(shift)
    }
}
