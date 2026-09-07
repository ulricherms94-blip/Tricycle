package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Course
import com.example.data.model.Refueling
import com.example.data.model.Shift
import com.example.data.repository.TricycleRepository
import com.example.location.GpsTracker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class DashboardUiState(
    val activeShift: Shift? = null,
    val courses: List<Course> = emptyList(),
    val refuelings: List<Refueling> = emptyList(),
    val totalRecette: Double = 0.0,
    val totalFuelCost: Double = 0.0,
    val totalFuelLitersAdded: Double = 0.0,
    val totalFuelRemaining: Double = 20.0,
    val totalFuelBurned: Double = 0.0,
    val currentSpeedKmH: Double = 0.0,
    val shiftDurationText: String = "0m 00s",
    val isShiftPaused: Boolean = false,
    val totalDistanceKm: Double = 0.0,
    val targetVersement: Double = 30000.0,
    val versementProgress: Float = 0f,
    val driverNetProfit: Double = 0.0,
    val isTargetReached: Boolean = false,
    val currentDistanceMode: String = "GPS", // "GPS", "ODOMETER", "DIRECT"
    val gpsTrackingActive: Boolean = false,
    val gpsStatusText: String = "",
    val fuelRatePer100Km: Double = 8.4
)

class TricycleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TricycleRepository
    val gpsTracker = GpsTracker(application)

    val allPastShifts: StateFlow<List<Shift>>

    private val prefs = application.getSharedPreferences("tricycle_app_prefs", Context.MODE_PRIVATE)

    private val _fuelRatePer100Km = MutableStateFlow(prefs.getFloat("fuel_rate_per_100km", 8.4f).toDouble())
    val fuelRatePer100Km: StateFlow<Double> = _fuelRatePer100Km.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Dasy, 1: Courses, 2: Tantara, 3: Tatitra
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _showNewShiftDialog = MutableStateFlow(false)
    val showNewShiftDialog: StateFlow<Boolean> = _showNewShiftDialog.asStateFlow()

    private val _showEndShiftDialog = MutableStateFlow(false)
    val showEndShiftDialog: StateFlow<Boolean> = _showEndShiftDialog.asStateFlow()

    private val _showAddCourseDialog = MutableStateFlow(false)
    val showAddCourseDialog: StateFlow<Boolean> = _showAddCourseDialog.asStateFlow()

    private val _showAddRefuelDialog = MutableStateFlow(false)
    val showAddRefuelDialog: StateFlow<Boolean> = _showAddRefuelDialog.asStateFlow()

    private val _simulationMessage = MutableStateFlow<String?>(null)
    val simulationMessage: StateFlow<String?> = _simulationMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TricycleRepository(database.tricycleDao())
        allPastShifts = repository.allShifts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Pre-create an initial shift ONLY once on first launch if DB is empty to make the app ready immediately
        val prefs = application.getSharedPreferences("tricycle_app_prefs", Context.MODE_PRIVATE)
        val hasSampleInitialized = prefs.getBoolean("has_sample_initialized", false)
        if (!hasSampleInitialized) {
            viewModelScope.launch {
                val list = repository.allShifts.first()
                if (list.isEmpty()) {
                    val shiftId = repository.startNewShift(
                        initialFuel = 5.0,
                        startOdometer = 12450.0,
                        targetVersement = 30000.0,
                        ownerPhone = "+261340000000",
                        distanceMode = "GPS"
                    )
                    // Add sample courses to showcase the requested Vibrant Palette design right away
                    repository.addCourse(
                        Course(
                            shiftId = shiftId,
                            departure = "Bazar Be",
                            destination = "Tanambao V",
                            fareAriary = 2000.0,
                            distanceKm = 2.4,
                            passengerCount = 2,
                            notes = "Course tsotra"
                        )
                    )
                    repository.addCourse(
                        Course(
                            shiftId = shiftId,
                            departure = "Gare",
                            destination = "Bord de la mer",
                            fareAriary = 3000.0,
                            distanceKm = 3.5,
                            passengerCount = 1,
                            notes = "Mpandeha maika"
                        )
                    )
                    repository.addCourse(
                        Course(
                            shiftId = shiftId,
                            departure = "Hopitaly Be",
                            destination = "Salazamay",
                            fareAriary = 2500.0,
                            distanceKm = 2.8,
                            passengerCount = 3,
                            notes = "Fianakaviana"
                        )
                    )
                    repository.addCourse(
                        Course(
                            shiftId = shiftId,
                            departure = "Bazar Kely",
                            destination = "Valpinson",
                            fareAriary = 3500.0,
                            distanceKm = 4.1,
                            passengerCount = 2,
                            notes = "Miaraka amin'ny entana"
                        )
                    )
                    repository.addRefueling(
                        Refueling(
                            shiftId = shiftId,
                            liters = 2.0,
                            costAriary = 11800.0,
                            stationName = "Total Bazar Be"
                        )
                    )
                    gpsTracker.addManualGpsKm(14.8)
                }
                prefs.edit().putBoolean("has_sample_initialized", true).apply()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DashboardUiState> = repository.activeShift.flatMapLatest { activeShift ->
        if (activeShift == null) {
            flowOf(DashboardUiState(activeShift = null))
        } else {
            val coursesFlow = repository.getCoursesForShift(activeShift.id)
            val refuelingsFlow = repository.getRefuelingsForShift(activeShift.id)
            val gpsKmFlow = gpsTracker.accumulatedGpsDistanceKm
            val gpsTrackingFlow = gpsTracker.isTracking
            val gpsTextFlow = gpsTracker.currentLocationText
            val gpsSpeedFlow = gpsTracker.currentSpeedKmH
            val gpsPausedFlow = gpsTracker.isPaused

            combine(
                coursesFlow,
                refuelingsFlow,
                gpsKmFlow,
                gpsTrackingFlow,
                gpsTextFlow,
                gpsSpeedFlow,
                gpsPausedFlow,
                _fuelRatePer100Km
            ) { args: Array<Any> ->
                @Suppress("UNCHECKED_CAST")
                val courses = args[0] as List<Course>
                @Suppress("UNCHECKED_CAST")
                val refuels = args[1] as List<Refueling>
                val gpsKm = args[2] as Double
                val isGpsTracking = args[3] as Boolean
                val gpsText = args[4] as String
                val currentSpeed = args[5] as Double
                val isShiftPaused = args[6] as Boolean
                val currentFuelRate = args[7] as Double

                val totalRecette = courses.sumOf { it.fareAriary }
                val totalFuelCost = refuels.sumOf { it.costAriary }
                val totalFuelAdded = refuels.sumOf { it.liters }

                // Distance calculation based on mode
                val calculatedDistance = when (activeShift.distanceMode) {
                    "GPS" -> gpsKm
                    "ODOMETER" -> {
                        val end = if (activeShift.endOdometer > activeShift.startOdometer)
                            activeShift.endOdometer
                        else
                            activeShift.startOdometer + courses.sumOf { it.distanceKm }
                        (end - activeShift.startOdometer).coerceAtLeast(0.0)
                    }
                    "DIRECT" -> activeShift.directDistanceKm
                    else -> gpsKm
                }

                // Fuel estimation based on configured rate (defaults to 8.4L/100km)
                val fuelBurned = calculatedDistance * (currentFuelRate / 100.0)
                val estimatedRemaining = (activeShift.initialFuelLiters + totalFuelAdded - fuelBurned)
                    .coerceAtLeast(0.0)

                val target = activeShift.targetVersementAr.coerceAtLeast(1.0)
                val progress = (totalRecette / target).toFloat().coerceIn(0f, 1f)
                val isReached = totalRecette >= target
                val netProfit = totalRecette - totalFuelCost - activeShift.targetVersementAr

                // Shift duration
                val elapsedMillis = (System.currentTimeMillis() - activeShift.startTime).coerceAtLeast(0L)
                val elapsedMinutes = (elapsedMillis / (1000 * 60)) % 60
                val elapsedHours = elapsedMillis / (1000 * 60 * 60)
                val elapsedSeconds = (elapsedMillis / 1000) % 60
                val durationText = if (elapsedHours > 0) {
                    "${elapsedHours}h ${elapsedMinutes}m"
                } else {
                    "${elapsedMinutes}m ${elapsedSeconds}s"
                }

                DashboardUiState(
                    activeShift = activeShift,
                    courses = courses,
                    refuelings = refuels,
                    totalRecette = totalRecette,
                    totalFuelCost = totalFuelCost,
                    totalFuelLitersAdded = totalFuelAdded,
                    totalFuelRemaining = estimatedRemaining,
                    totalFuelBurned = fuelBurned,
                    currentSpeedKmH = currentSpeed,
                    shiftDurationText = durationText,
                    isShiftPaused = isShiftPaused,
                    totalDistanceKm = calculatedDistance,
                    targetVersement = activeShift.targetVersementAr,
                    versementProgress = progress,
                    driverNetProfit = netProfit,
                    isTargetReached = isReached,
                    currentDistanceMode = activeShift.distanceMode,
                    gpsTrackingActive = isGpsTracking,
                    gpsStatusText = gpsText,
                    fuelRatePer100Km = currentFuelRate
                )
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        DashboardUiState()
    )

    fun setTab(index: Int) {
        _selectedTab.value = index
    }

    fun openNewShiftDialog() { _showNewShiftDialog.value = true }
    fun closeNewShiftDialog() { _showNewShiftDialog.value = false }

    fun openEndShiftDialog() { _showEndShiftDialog.value = true }
    fun closeEndShiftDialog() { _showEndShiftDialog.value = false }

    fun openAddCourseDialog() { _showAddCourseDialog.value = true }
    fun closeAddCourseDialog() { _showAddCourseDialog.value = false }

    fun openAddRefuelDialog() { _showAddRefuelDialog.value = true }
    fun closeAddRefuelDialog() { _showAddRefuelDialog.value = false }

    fun clearSimulationMessage() { _simulationMessage.value = null }

    fun startNewShift(
        initialFuel: Double,
        startOdo: Double,
        targetVersement: Double,
        ownerPhone: String,
        mode: String
    ) {
        viewModelScope.launch {
            gpsTracker.resetGpsDistance()
            repository.startNewShift(
                initialFuel = initialFuel,
                startOdometer = startOdo,
                targetVersement = targetVersement,
                ownerPhone = ownerPhone,
                distanceMode = mode
            )
            _showNewShiftDialog.value = false
            if (mode == "GPS") {
                gpsTracker.startTracking()
            }
        }
    }

    fun endCurrentShift(remainingFuel: Double, endOdo: Double) {
        val current = uiState.value.activeShift ?: return
        viewModelScope.launch {
            gpsTracker.stopTracking()
            repository.endShift(current.id, remainingFuel, endOdo)
            _showEndShiftDialog.value = false
        }
    }

    fun setDistanceMode(mode: String) {
        val current = uiState.value.activeShift ?: return
        viewModelScope.launch {
            repository.updateShift(current.copy(distanceMode = mode))
            if (mode == "GPS") {
                gpsTracker.startTracking()
            } else {
                gpsTracker.stopTracking()
            }
        }
    }

    fun updateOdometer(startOdo: Double, endOdo: Double) {
        val current = uiState.value.activeShift ?: return
        viewModelScope.launch {
            repository.updateShift(
                current.copy(
                    startOdometer = startOdo,
                    endOdometer = endOdo
                )
            )
        }
    }

    fun updateDirectDistance(distanceKm: Double) {
        val current = uiState.value.activeShift ?: return
        viewModelScope.launch {
            repository.updateShift(current.copy(directDistanceKm = distanceKm))
        }
    }

    fun updateTargetVersement(target: Double) {
        val current = uiState.value.activeShift ?: return
        viewModelScope.launch {
            repository.updateShift(current.copy(targetVersementAr = target))
        }
    }

    fun toggleGps() {
        if (gpsTracker.isTracking.value) {
            gpsTracker.stopTracking()
        } else {
            gpsTracker.startTracking()
        }
    }

    fun togglePauseShift() {
        gpsTracker.togglePause()
    }

    fun saveCurrentProgress() {
        val current = uiState.value.activeShift ?: return
        viewModelScope.launch {
            val dist = uiState.value.totalDistanceKm
            val remainingFuel = uiState.value.totalFuelRemaining
            val updatedShift = current.copy(
                directDistanceKm = dist,
                endOdometer = if (current.endOdometer > 0) current.endOdometer else current.startOdometer + dist,
                remainingFuelLiters = remainingFuel
            )
            repository.updateShift(updatedShift)
            _simulationMessage.value = "Voatahiry soa aman-tsara ny halavirana (%.1f km) sy ny asa ankehitriny!".format(Locale.FRENCH, dist)
        }
    }

    fun updateFuelRatePer100Km(newRate: Double) {
        if (newRate <= 0.0) return
        _fuelRatePer100Km.value = newRate
        prefs.edit().putFloat("fuel_rate_per_100km", newRate.toFloat()).apply()
        _simulationMessage.value = "Voaova ho %.1f L/100km ny taham-panjifana solika!".format(
            Locale.FRENCH, newRate
        )
    }

    fun resetGpsDistance() {
        gpsTracker.resetGpsDistance()
        val current = uiState.value.activeShift
        if (current != null) {
            viewModelScope.launch {
                repository.updateShift(current.copy(directDistanceKm = 0.0))
            }
        }
        _simulationMessage.value = "Naverina 0 km ny halavirana GPS."
    }

    fun addCourse(departure: String, destination: String, fare: Double, distance: Double, passengers: Int, notes: String) {
        val current = uiState.value.activeShift ?: return
        viewModelScope.launch {
            repository.addCourse(
                Course(
                    shiftId = current.id,
                    departure = departure,
                    destination = destination,
                    fareAriary = fare,
                    distanceKm = distance,
                    passengerCount = passengers,
                    notes = notes
                )
            )
            if (current.distanceMode == "GPS") {
                gpsTracker.addManualGpsKm(distance)
            } else if (current.distanceMode == "DIRECT") {
                repository.updateShift(current.copy(directDistanceKm = current.directDistanceKm + distance))
            } else if (current.distanceMode == "ODOMETER") {
                repository.updateShift(current.copy(endOdometer = current.endOdometer + distance))
            }
            _showAddCourseDialog.value = false
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
        }
    }

    fun addRefuel(liters: Double, cost: Double, station: String) {
        val current = uiState.value.activeShift ?: return
        viewModelScope.launch {
            repository.addRefueling(
                Refueling(
                    shiftId = current.id,
                    liters = liters,
                    costAriary = cost,
                    stationName = station
                )
            )
            _showAddRefuelDialog.value = false
        }
    }

    fun deleteRefuel(refueling: Refueling) {
        viewModelScope.launch {
            repository.deleteRefueling(refueling)
        }
    }

    fun deleteShift(shift: Shift) {
        viewModelScope.launch {
            if (shift.isActive || shift.id == uiState.value.activeShift?.id) {
                gpsTracker.stopTracking()
                gpsTracker.resetGpsDistance()
            }
            repository.deleteShift(shift)
            _simulationMessage.value = "Voafafa soa aman-tsara ny andro niasana (Shift)."
        }
    }

    // Realistic simulation button requested by the user
    fun simulateNewCourse() {
        val current = uiState.value.activeShift ?: return

        val realisticRoutes = listOf(
            Triple("Bazar Be", "Tanambao V", 2.4 to 2500.0),
            Triple("Gare", "Bord de la mer", 3.2 to 3000.0),
            Triple("Hopitaly Be", "Mangarivotra", 1.9 to 2000.0),
            Triple("Salazamay", "Bazar Kely", 2.1 to 2000.0),
            Triple("Campus Barikadimy", "Boulevard Joffre", 4.5 to 4500.0),
            Triple("Port", "Ankirihiry", 3.6 to 3500.0),
            Triple("Morafeno", "Bazar Be", 2.2 to 2000.0),
            Triple("Valpinson", "Hopitaly Be", 3.8 to 4000.0)
        )

        val selected = realisticRoutes.random()
        val departure = selected.first
        val destination = selected.second
        val baseDistance = selected.third.first
        val fare = selected.third.second
        val passengers = Random.nextInt(1, 4)

        viewModelScope.launch {
            repository.addCourse(
                Course(
                    shiftId = current.id,
                    departure = departure,
                    destination = destination,
                    fareAriary = fare,
                    distanceKm = baseDistance,
                    passengerCount = passengers,
                    notes = "Course simulation automatique"
                )
            )

            // Update distance depending on current mode
            when (current.distanceMode) {
                "GPS" -> {
                    gpsTracker.addManualGpsKm(baseDistance)
                    gpsTracker.setSimulatedSpeed(Random.nextDouble(18.0, 32.0))
                }
                "DIRECT" -> repository.updateShift(current.copy(directDistanceKm = current.directDistanceKm + baseDistance))
                "ODOMETER" -> repository.updateShift(current.copy(endOdometer = (if (current.endOdometer > 0) current.endOdometer else current.startOdometer) + baseDistance))
            }

            val format = NumberFormat.getNumberInstance(Locale.FRENCH)
            _simulationMessage.value = "Tafiditra ny course: $departure ➔ $destination (+${format.format(fare)} Ar, +$baseDistance km)"
        }
    }

    fun generateReportText(state: DashboardUiState): String {
        val shift = state.activeShift ?: return "Tsy misy andro iasana misokatra."
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH)
        val dateStr = dateFormat.format(Date(shift.startTime))
        val numberFormat = NumberFormat.getNumberInstance(Locale.FRENCH)

        val versementStatus = if (state.isTargetReached) {
            "✅ TRATRY NY TANJONA! (+${numberFormat.format((state.totalRecette - state.targetVersement).coerceAtLeast(0.0))} Ar)"
        } else {
            "⏳ Mbola manantena Ar ${numberFormat.format((state.targetVersement - state.totalRecette).coerceAtLeast(0.0))}"
        }

        return """
🛺 *TATITRA ANDRO IASANA - TRICYCLE PRO* 🇲🇬
----------------------------------------
📅 Daty: $dateStr
⏱️ Fomba KM: ${shift.distanceMode}

💰 *FITANTANAM-BOLA:*
• Recette (Vola maty): Ar ${numberFormat.format(state.totalRecette)}
• Solika novidiana: Ar ${numberFormat.format(state.totalFuelCost)} (${"%.1f".format(state.totalFuelLitersAdded)} L)
• Tanjona Versement: Ar ${numberFormat.format(state.targetVersement)}
  👉 $versementStatus
• *Tombony madio mpamily:* Ar ${numberFormat.format(state.driverNetProfit)}

📍 *KILOMETATRA SY COURSES:*
• Kilometatra tontaly: ${"%.1f".format(state.totalDistanceKm)} km
• Isan'ny courses vita: ${state.courses.size}
• Solika lany tombanana: ${"%.2f".format(state.totalFuelBurned)} L (${"%.1f".format(state.fuelRatePer100Km)} L/100km)
• Solika tavela tombanana: ${"%.1f".format(state.totalFuelRemaining)} L

----------------------------------------
Voaangona tamin'ny alalan'ny Tricycle Pro (Offline)
        """.trimIndent()
    }
}
