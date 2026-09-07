package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GpsTracker(private val context: Context) {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _currentSpeedKmH = MutableStateFlow(0.0)
    val currentSpeedKmH: StateFlow<Double> = _currentSpeedKmH.asStateFlow()

    private val _accumulatedGpsDistanceKm = MutableStateFlow(0.0)
    val accumulatedGpsDistanceKm: StateFlow<Double> = _accumulatedGpsDistanceKm.asStateFlow()

    private val _currentLocationText = MutableStateFlow("Tsy mbola misy famantarana GPS")
    val currentLocationText: StateFlow<String> = _currentLocationText.asStateFlow()

    private var lastLocation: Location? = null

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (_isPaused.value) return

            val last = lastLocation
            if (last != null) {
                val distanceInMeters = last.distanceTo(location)
                // Filter out small jitter (< 5 meters)
                if (distanceInMeters > 4.0 && distanceInMeters < 500.0) {
                    val additionalKm = distanceInMeters / 1000.0
                    _accumulatedGpsDistanceKm.value += additionalKm
                }
            }
            lastLocation = location

            // Speed in km/h from GPS (m/s * 3.6)
            val speed = if (location.hasSpeed()) {
                (location.speed.toDouble() * 3.6).coerceAtLeast(0.0)
            } else {
                0.0
            }
            _currentSpeedKmH.value = speed

            _currentLocationText.value = "Lat: %.4f, Lon: %.4f (±%.1fm)".format(location.latitude, location.longitude, location.accuracy)
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    @SuppressLint("MissingPermission")
    fun startTracking(): Boolean {
        try {
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                _currentLocationText.value = "Mikatona ny GPS"
                return false
            }

            if (isGpsEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000L, // 3s
                    5.0f,  // 5m
                    locationListener,
                    Looper.getMainLooper()
                )
            } else if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    3000L,
                    5.0f,
                    locationListener,
                    Looper.getMainLooper()
                )
            }

            _isTracking.value = true
            return true
        } catch (e: SecurityException) {
            _currentLocationText.value = "Mila alalana GPS (Permission)"
            _isTracking.value = false
            return false
        } catch (e: Exception) {
            _currentLocationText.value = "Tsy afaka mandeha ny GPS"
            _isTracking.value = false
            return false
        }
    }

    fun stopTracking() {
        try {
            locationManager.removeUpdates(locationListener)
        } catch (e: Exception) {
            // ignore
        }
        _isTracking.value = false
        _isPaused.value = false
        _currentSpeedKmH.value = 0.0
        lastLocation = null
    }

    fun pauseTracking() {
        _isPaused.value = true
        _currentSpeedKmH.value = 0.0
    }

    fun resumeTracking() {
        _isPaused.value = false
    }

    fun togglePause(): Boolean {
        _isPaused.value = !_isPaused.value
        if (_isPaused.value) {
            _currentSpeedKmH.value = 0.0
        }
        return _isPaused.value
    }

    fun addManualGpsKm(km: Double) {
        _accumulatedGpsDistanceKm.value += km
    }

    fun setSimulatedSpeed(speed: Double) {
        _currentSpeedKmH.value = speed
    }

    fun resetGpsDistance() {
        _accumulatedGpsDistanceKm.value = 0.0
        _currentSpeedKmH.value = 0.0
        _isPaused.value = false
        lastLocation = null
    }
}
