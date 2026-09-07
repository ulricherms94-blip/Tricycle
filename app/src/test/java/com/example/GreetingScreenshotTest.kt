package com.example

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.screens.DashboardScreen
import com.example.ui.DashboardUiState
import com.example.data.model.Shift
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleShift = Shift(
      id = 1,
      startTime = 1725526800000L,
      initialFuelLiters = 5.0,
      remainingFuelLiters = 3.2,
      startOdometer = 12450.0,
      endOdometer = 12464.8,
      directDistanceKm = 14.8,
      targetVersementAr = 30000.0
    )
    val state = DashboardUiState(
      activeShift = sampleShift,
      totalRecette = 42500.0,
      totalFuelCost = 11800.0,
      totalFuelRemaining = 3.2,
      totalDistanceKm = 14.8,
      targetVersement = 30000.0,
      versementProgress = 0.75f,
      driverNetProfit = 12500.0,
      isTargetReached = true
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        DashboardScreen(
          state = state,
          simulationMessage = null,
          onClearSimulationMessage = {},
          onStartNewShiftClick = {},
          onEndShiftClick = {},
          onAddRefuelClick = {},
          onAddCourseClick = {},
          onSimulateCourseClick = {},
          onSelectDistanceMode = {},
          onUpdateOdometer = { _, _ -> },
          onUpdateDirectKm = {},
          onToggleGps = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
