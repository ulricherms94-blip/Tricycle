package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DashboardUiState
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import com.example.ui.components.EditDirectKmDialog
import com.example.ui.components.EditOdometerDialog
import com.example.ui.components.FuelRateSettingsDialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoDark
import com.example.ui.theme.IndigoDeepNavy
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.VibrantOrange
import com.example.ui.theme.VibrantOrangeDark
import com.example.ui.theme.VibrantOrangeLight
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    state: DashboardUiState,
    simulationMessage: String?,
    onClearSimulationMessage: () -> Unit,
    onStartNewShiftClick: () -> Unit,
    onEndShiftClick: () -> Unit,
    onAddRefuelClick: () -> Unit,
    onAddCourseClick: () -> Unit,
    onSimulateCourseClick: () -> Unit,
    onSelectDistanceMode: (String) -> Unit,
    onUpdateOdometer: (Double, Double) -> Unit,
    onUpdateDirectKm: (Double) -> Unit,
    onToggleGps: () -> Unit,
    onTogglePauseShift: () -> Unit = {},
    onResetGpsDistance: () -> Unit = {},
    onSaveProgress: () -> Unit = {},
    onUpdateFuelRate: (Double) -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.FRENCH) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.FRENCH) }

    var showEditOdoDialog by remember { mutableStateOf(false) }
    var showEditDirectKmDialog by remember { mutableStateOf(false) }
    var showFuelRateSettingsDialog by remember { mutableStateOf(false) }

    if (showFuelRateSettingsDialog) {
        FuelRateSettingsDialog(
            currentRate = state.fuelRatePer100Km,
            onDismiss = { showFuelRateSettingsDialog = false },
            onConfirm = { rate ->
                onUpdateFuelRate(rate)
                showFuelRateSettingsDialog = false
            }
        )
    }

    if (showEditOdoDialog && state.activeShift != null) {
        EditOdometerDialog(
            initialStartOdo = state.activeShift.startOdometer,
            initialEndOdo = state.activeShift.endOdometer,
            onDismiss = { showEditOdoDialog = false },
            onConfirm = { start, end ->
                onUpdateOdometer(start, end)
                showEditOdoDialog = false
            }
        )
    }

    if (showEditDirectKmDialog && state.activeShift != null) {
        EditDirectKmDialog(
            currentKm = state.activeShift.directDistanceKm,
            onDismiss = { showEditDirectKmDialog = false },
            onConfirm = { km ->
                onUpdateDirectKm(km)
                showEditDirectKmDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Simulation Alert Toast
        AnimatedVisibility(
            visible = simulationMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (simulationMessage != null) {
                Surface(
                    color = EmeraldLight,
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(EmeraldSuccess, EmeraldGlow))),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClearSimulationMessage() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldSuccess,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = simulationMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate900,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        if (state.activeShift == null) {
            // Empty State: No active shift
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Slate100, Slate200))),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_no_active_shift")
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(IndigoContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        "Tsy misy andro iasana mandeha",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        "Tsindrio eto ambany raha hanomboka ny shift anio (solika fiaingana, kaontera, versement).",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = onStartNewShiftClick,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("button_start_shift")
                    ) {
                        Text("Manomboka Andro Iasana", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // CARD 1: ANDRO IASANA (SHIFT TRACKER)
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Slate100, Slate200))),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_shift_tracker")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ANDRO IASANA (SHIFT)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate500,
                            letterSpacing = 1.sp
                        )

                        val startTimeStr = timeFormat.format(Date(state.activeShift.startTime))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                onClick = { showFuelRateSettingsDialog = true },
                                color = Slate100,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("button_open_fuel_rate_settings")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = "Paramètre solika",
                                        tint = IndigoPrimary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = "%.1fL/100km".format(Locale.FRENCH, state.fuelRatePer100Km),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoDeepNavy
                                    )
                                }
                            }

                            Surface(
                                color = IndigoContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$startTimeStr fiaingana",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = IndigoDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Fuel 3-column row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Left: Fiaingana
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Fiaingana",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "%.1f L".format(state.activeShift.initialFuelLiters),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        }

                        // Center: Fuel Gauge
                        Column(
                            modifier = Modifier.weight(1.5f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val fuelRatio = (state.totalFuelRemaining / (state.activeShift.initialFuelLiters + state.totalFuelLitersAdded).coerceAtLeast(1.0))
                                .toFloat()
                                .coerceIn(0.05f, 1f)

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(Slate100)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fuelRatio)
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(VibrantOrange, VibrantOrangeDark)
                                            )
                                        )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Solika sisa: %.1f L".format(state.totalFuelRemaining),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = VibrantOrangeDark,
                                fontSize = 11.sp
                            )
                        }

                        // Right: Tavela
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "Tavela",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate400,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "%.1f L".format(state.totalFuelRemaining),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        }
                    }

                    // Ravitaillement & Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = onAddRefuelClick,
                            color = VibrantOrangeLight,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("button_add_refuel")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalGasStation,
                                    contentDescription = null,
                                    tint = VibrantOrangeDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "+ Solika fanampiny",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantOrangeDark
                                )
                            }
                        }

                        Surface(
                            onClick = onEndShiftClick,
                            color = Slate50,
                            border = CardDefaults.outlinedCardBorder(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("button_end_shift")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stop,
                                    contentDescription = null,
                                    tint = Slate700,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Akatony",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate700
                                )
                            }
                        }
                    }
                }
            }

            // CARD 2: FITANTANAM-BOLA (FINANCIAL OVERVIEW - VIBRANT PALETTE DARK NAVY)
            Card(
                colors = CardDefaults.cardColors(containerColor = IndigoDeepNavy),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_finance")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top: Recette & Driver Profit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Fitantanam-bola (Recette)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC7D2FE), // indigo-200
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ar ${numberFormat.format(state.totalRecette)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = PureWhite,
                                letterSpacing = (-0.5).sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Tombony (Driver)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC7D2FE),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ar ${numberFormat.format(state.driverNetProfit)}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldGlow
                            )
                        }
                    }

                    // Progress section: Versement Tompony
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Versement ho an'ny tompony",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = PureWhite
                            )

                            val percentage = (state.versementProgress * 100).toInt()
                            Surface(
                                color = if (state.isTargetReached) EmeraldSuccess.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (state.isTargetReached) "100% Tratry ny tanjona!" else "$percentage%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isTargetReached) EmeraldGlow else Color(0xFFE0E7FF),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(state.versementProgress)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (state.isTargetReached)
                                            Brush.horizontalGradient(listOf(EmeraldSuccess, EmeraldGlow))
                                        else
                                            Brush.horizontalGradient(listOf(IndigoPrimary, Color(0xFF818CF8)))
                                    )
                            )
                        }

                        // Scale indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Ar 0",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC7D2FE),
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Loha: Ar ${numberFormat.format(state.targetVersement)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFC7D2FE),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // CARD 3: KILOMETATRA (3 MODES: GPS, ODOMETER, DIRECT)
            Card(
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                shape = RoundedCornerShape(24.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Slate100, Slate200))),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth().testTag("card_distance")
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "KILOMETATRA (DISTANCE)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )

                    // 3 Mode selection buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // GPS Auto
                        val isGps = state.currentDistanceMode == "GPS"
                        Surface(
                            onClick = {
                                onSelectDistanceMode("GPS")
                                if (!state.gpsTrackingActive) onToggleGps()
                            },
                            color = if (isGps) IndigoPrimary else Slate50,
                            shape = RoundedCornerShape(16.dp),
                            border = if (!isGps) CardDefaults.outlinedCardBorder() else null,
                            modifier = Modifier.weight(1f).testTag("mode_gps")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = if (isGps) PureWhite else Slate700,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "GPS AUTO",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isGps) PureWhite else Slate700,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Odomètre
                        val isOdo = state.currentDistanceMode == "ODOMETER"
                        Surface(
                            onClick = {
                                onSelectDistanceMode("ODOMETER")
                                showEditOdoDialog = true
                            },
                            color = if (isOdo) IndigoPrimary else Slate50,
                            shape = RoundedCornerShape(16.dp),
                            border = if (!isOdo) CardDefaults.outlinedCardBorder() else null,
                            modifier = Modifier.weight(1f).testTag("mode_odometer")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = if (isOdo) PureWhite else Slate700,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "ODOMÈTRE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOdo) PureWhite else Slate700,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Mivantana (Direct)
                        val isDirect = state.currentDistanceMode == "DIRECT"
                        Surface(
                            onClick = {
                                onSelectDistanceMode("DIRECT")
                                showEditDirectKmDialog = true
                            },
                            color = if (isDirect) IndigoPrimary else Slate50,
                            shape = RoundedCornerShape(16.dp),
                            border = if (!isDirect) CardDefaults.outlinedCardBorder() else null,
                            modifier = Modifier.weight(1f).testTag("mode_direct")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = if (isDirect) PureWhite else Slate700,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "MIVANTANA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDirect) PureWhite else Slate700,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Main KM display
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "%.1f".format(state.totalDistanceKm),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Black,
                                color = Slate900,
                                fontSize = 38.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "KM",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate400
                            )
                        }
                        Text(
                            text = "Tontaly androany",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = IndigoPrimary
                        )

                        if (state.currentDistanceMode == "GPS") {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (state.gpsTrackingActive) {
                                    if (state.isShiftPaused) "🟡 GPS natsahatra kely (En pause)" else "🟢 GPS mandeha • ${state.gpsStatusText}"
                                } else "⚪ GPS nitsahatra",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (state.gpsTrackingActive) (if (state.isShiftPaused) VibrantOrangeDark else EmeraldSuccess) else Slate400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // 3 Real-time GPS Metric Badges: Hafainganana (Speed), Solika lany (Fuel), Faharetana (Time)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 1. Speed (Hafainganana)
                                Surface(
                                    color = Slate50,
                                    shape = RoundedCornerShape(12.dp),
                                    border = CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Speed,
                                            contentDescription = null,
                                            tint = IndigoPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "%.0f km/h".format(state.currentSpeedKmH),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                        Text(
                                            text = "Hafainganana",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate400,
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                // 2. Fuel burned (Solika lany) with edit affordance
                                Surface(
                                    onClick = { showFuelRateSettingsDialog = true },
                                    color = Slate50,
                                    shape = RoundedCornerShape(12.dp),
                                    border = CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier.weight(1f).testTag("badge_fuel_burned")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalGasStation,
                                            contentDescription = "Hanova taham-panjifana solika",
                                            tint = VibrantOrangeDark,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "%.2f L".format(state.totalFuelBurned),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                        Text(
                                            text = "Solika (%.1f/100)".format(Locale.FRENCH, state.fuelRatePer100Km),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = IndigoPrimary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // 3. Elapsed Time (Faharetana)
                                Surface(
                                    color = Slate50,
                                    shape = RoundedCornerShape(12.dp),
                                    border = CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = EmeraldSuccess,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = state.shiftDurationText,
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Slate900
                                        )
                                        Text(
                                            text = "Faharetana",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Slate400,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons: Pause/Resume GPS, Enregistrer (Save), Reset GPS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // 1. Pause / Resume
                                Surface(
                                    onClick = onTogglePauseShift,
                                    color = if (state.isShiftPaused) VibrantOrangeLight else Slate100,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f).testTag("button_pause_gps")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp, horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (state.isShiftPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                            contentDescription = null,
                                            tint = if (state.isShiftPaused) VibrantOrangeDark else Slate700,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = if (state.isShiftPaused) "Tohizo" else "Pause",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (state.isShiftPaused) VibrantOrangeDark else Slate700,
                                            maxLines = 1
                                        )
                                    }
                                }

                                // 2. Enregistrer (Tehirizo ny asa vita)
                                Surface(
                                    onClick = onSaveProgress,
                                    color = EmeraldLight,
                                    shape = RoundedCornerShape(12.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(EmeraldSuccess, EmeraldGlow))),
                                    modifier = Modifier.weight(1.35f).testTag("button_save_gps_progress")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp, horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Save,
                                            contentDescription = "Enregistrer",
                                            tint = EmeraldSuccess,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Enregistrer",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Black,
                                            color = EmeraldSuccess,
                                            maxLines = 1
                                        )
                                    }
                                }

                                // 3. Avereno 0
                                Surface(
                                    onClick = onResetGpsDistance,
                                    color = Slate50,
                                    shape = RoundedCornerShape(12.dp),
                                    border = CardDefaults.outlinedCardBorder(),
                                    modifier = Modifier.weight(0.95f).testTag("button_reset_gps")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 9.dp, horizontal = 4.dp),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = null,
                                            tint = Slate500,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Avereno 0",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Slate700,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        } else if (state.currentDistanceMode == "ODOMETER") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Début: %.1f km • Fin: %.1f km".format(state.activeShift.startOdometer, state.activeShift.endOdometer),
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate500,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                onClick = onSaveProgress,
                                color = EmeraldLight,
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(EmeraldSuccess, EmeraldGlow))),
                                modifier = Modifier.fillMaxWidth().testTag("button_save_odometer_progress")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 9.dp, horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Enregistrer",
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tehirizo ny kilometatra (Enregistrer)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = EmeraldSuccess
                                    )
                                }
                            }
                        } else if (state.currentDistanceMode == "DIRECT") {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Kilometatra nampidirina mivantana",
                                style = MaterialTheme.typography.labelSmall,
                                color = Slate500,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                onClick = onSaveProgress,
                                color = EmeraldLight,
                                shape = RoundedCornerShape(12.dp),
                                border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(EmeraldSuccess, EmeraldGlow))),
                                modifier = Modifier.fillMaxWidth().testTag("button_save_direct_progress")
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 9.dp, horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = "Enregistrer",
                                        tint = EmeraldSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Tehirizo ny kilometatra (Enregistrer)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = EmeraldSuccess
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SIMULATION ACTION BUTTON (ORANGE - VIBRANT PALETTE)
            Button(
                onClick = onSimulateCourseClick,
                colors = ButtonDefaults.buttonColors(containerColor = VibrantOrange),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("button_simulation_course")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SIMULATION COURSE VAOVAO",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = PureWhite
                    )
                }
            }

            // Secondary: Add Custom Course manually
            Surface(
                onClick = onAddCourseClick,
                color = PureWhite,
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder(),
                modifier = Modifier.fillMaxWidth().testTag("button_custom_course")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ampidiro tanana ny course (Depart/Arrivee)",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )
                }
            }
        }
    }
}
