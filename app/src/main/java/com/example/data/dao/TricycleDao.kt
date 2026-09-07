package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Course
import com.example.data.model.Refueling
import com.example.data.model.Shift
import kotlinx.coroutines.flow.Flow

@Dao
interface TricycleDao {
    // Shifts
    @Query("SELECT * FROM shifts WHERE isActive = 1 ORDER BY startTime DESC LIMIT 1")
    fun getActiveShift(): Flow<Shift?>

    @Query("SELECT * FROM shifts ORDER BY startTime DESC")
    fun getAllShifts(): Flow<List<Shift>>

    @Query("SELECT * FROM shifts WHERE id = :id")
    suspend fun getShiftById(id: Long): Shift?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: Shift): Long

    @Update
    suspend fun updateShift(shift: Shift)

    @Delete
    suspend fun deleteShift(shift: Shift)

    // Courses
    @Query("SELECT * FROM courses WHERE shiftId = :shiftId ORDER BY timestamp DESC")
    fun getCoursesForShift(shiftId: Long): Flow<List<Course>>

    @Query("SELECT * FROM courses ORDER BY timestamp DESC")
    fun getAllCourses(): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course): Long

    @Delete
    suspend fun deleteCourse(course: Course)

    // Refuelings
    @Query("SELECT * FROM refuelings WHERE shiftId = :shiftId ORDER BY timestamp DESC")
    fun getRefuelingsForShift(shiftId: Long): Flow<List<Refueling>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefueling(refueling: Refueling): Long

    @Delete
    suspend fun deleteRefueling(refueling: Refueling)
}
