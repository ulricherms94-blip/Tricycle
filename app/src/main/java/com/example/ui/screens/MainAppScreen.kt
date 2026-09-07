package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.TricycleViewModel
import com.example.ui.components.AddCourseDialog
import com.example.ui.components.AddRefuelDialog
import com.example.ui.components.EndShiftDialog
import com.example.ui.components.FuelRateSettingsDialog
import com.example.ui.components.NewShiftDialog
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoDeepNavy
import com.example.ui.theme.IndigoLightBg
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: TricycleViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val allShifts by viewModel.allPastShifts.collectAsStateWithLifecycle()
    val simulationMessage by viewModel.simulationMessage.collectAsStateWithLifecycle()

    val showNewShiftDialog by viewModel.showNewShiftDialog.collectAsStateWithLifecycle()
    val showEndShiftDialog by viewModel.showEndShiftDialog.collectAsStateWithLifecycle()
    val showAddCourseDialog by viewModel.showAddCourseDialog.collectAsStateWithLifecycle()
    val showAddRefuelDialog by viewModel.showAddRefuelDialog.collectAsStateWithLifecycle()

    var showFuelRateSettingsDialog by remember { mutableStateOf(false) }

    if (showFuelRateSettingsDialog) {
        FuelRateSettingsDialog(
            currentRate = uiState.fuelRatePer100Km,
            onDismiss = { showFuelRateSettingsDialog = false },
            onConfirm = { rate ->
                viewModel.updateFuelRatePer100Km(rate)
                showFuelRateSettingsDialog = false
            }
        )
    }

    // Dialogs
    if (showNewShiftDialog) {
        NewShiftDialog(
            onDismiss = { viewModel.closeNewShiftDialog() },
            onConfirm = { fuel, odo, target, phone, mode ->
                viewModel.startNewShift(fuel, odo, target, phone, mode)
            }
        )
    }

    if (showEndShiftDialog && uiState.activeShift != null) {
        EndShiftDialog(
            estimatedFuelRemaining = uiState.totalFuelRemaining,
            currentOdometer = if (uiState.activeShift!!.endOdometer > 0) uiState.activeShift!!.endOdometer else uiState.activeShift!!.startOdometer + uiState.totalDistanceKm,
            onDismiss = { viewModel.closeEndShiftDialog() },
            onConfirm = { fuel, odo ->
                viewModel.endCurrentShift(fuel, odo)
            }
        )
    }

    if (showAddCourseDialog) {
        AddCourseDialog(
            onDismiss = { viewModel.closeAddCourseDialog() },
            onConfirm = { dep, dest, fare, dist, pass, notes ->
                viewModel.addCourse(dep, dest, fare, dist, pass, notes)
            }
        )
    }

    if (showAddRefuelDialog) {
        AddRefuelDialog(
            onDismiss = { viewModel.closeAddRefuelDialog() },
            onConfirm = { liters, cost, station ->
                viewModel.addRefuel(liters, cost, station)
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = IndigoLightBg,
        topBar = {
            // Header adhering strictly to Vibrant Palette design
            Surface(
                color = PureWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo + App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.TwoWheeler,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Tricycle Pro",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = IndigoDeepNavy
                            )
                            val isActive = uiState.activeShift != null && uiState.activeShift!!.isActive
                            Text(
                                text = if (isActive) "Am-piasana (Active)" else "Nitsahatra (Inactif)",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) EmeraldSuccess else Slate400,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { showFuelRateSettingsDialog = true },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Slate50)
                                .testTag("top_action_fuel_rate")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Paramètre Solika (L/100km)",
                                tint = IndigoPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                if (uiState.activeShift != null) {
                                    viewModel.openEndShiftDialog()
                                } else {
                                    viewModel.openNewShiftDialog()
                                }
                            },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Slate50)
                                .testTag("top_action_shift")
                        ) {
                            Icon(
                                imageVector = if (uiState.activeShift != null) Icons.Default.Settings else Icons.Default.PlayArrow,
                                contentDescription = "Fandrindrana",
                                tint = Slate700,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Navigation bar matching Vibrant Palette styling
            NavigationBar(
                containerColor = PureWhite,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                tonalElevation = 8.dp
            ) {
                val navItems = listOf(
                    Triple(0, "Dasy", Icons.Default.Home),
                    Triple(1, "Courses", Icons.Default.DirectionsCar),
                    Triple(2, "Tantara", Icons.Default.History),
                    Triple(3, "SMS/WA", Icons.Default.Message)
                )

                navItems.forEach { (index, title, icon) ->
                    val isSelected = selectedTab == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(index) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = IndigoContainer,
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate400
                        ),
                        modifier = Modifier.testTag("nav_tab_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    state = uiState,
                    simulationMessage = simulationMessage,
                    onClearSimulationMessage = { viewModel.clearSimulationMessage() },
                    onStartNewShiftClick = { viewModel.openNewShiftDialog() },
                    onEndShiftClick = { viewModel.openEndShiftDialog() },
                    onAddRefuelClick = { viewModel.openAddRefuelDialog() },
                    onAddCourseClick = { viewModel.openAddCourseDialog() },
                    onSimulateCourseClick = { viewModel.simulateNewCourse() },
                    onSelectDistanceMode = { mode -> viewModel.setDistanceMode(mode) },
                    onUpdateOdometer = { start, end -> viewModel.updateOdometer(start, end) },
                    onUpdateDirectKm = { km -> viewModel.updateDirectDistance(km) },
                    onToggleGps = { viewModel.toggleGps() },
                    onTogglePauseShift = { viewModel.togglePauseShift() },
                    onResetGpsDistance = { viewModel.resetGpsDistance() },
                    onSaveProgress = { viewModel.saveCurrentProgress() },
                    onUpdateFuelRate = { rate -> viewModel.updateFuelRatePer100Km(rate) }
                )
                1 -> CoursesScreen(
                    courses = uiState.courses,
                    onAddCourseClick = { viewModel.openAddCourseDialog() },
                    onDeleteCourse = { course -> viewModel.deleteCourse(course) }
                )
                2 -> HistoryScreen(
                    shifts = allShifts,
                    onDeleteShift = { shift -> viewModel.deleteShift(shift) }
                )
                3 -> ReportScreen(
                    state = uiState,
                    reportText = viewModel.generateReportText(uiState)
                )
            }
        }
    }
}
