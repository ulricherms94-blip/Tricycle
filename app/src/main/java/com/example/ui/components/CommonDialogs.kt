package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate700
import com.example.ui.theme.VibrantOrange

@Composable
fun NewShiftDialog(
    onDismiss: () -> Unit,
    onConfirm: (initialFuel: Double, startOdo: Double, targetVersement: Double, ownerPhone: String, mode: String) -> Unit
) {
    var fuelText by remember { mutableStateOf("5.0") }
    var odoText by remember { mutableStateOf("12500") }
    var versementText by remember { mutableStateOf("30000") }
    var phoneText by remember { mutableStateOf("+261340000000") }
    var selectedMode by remember { mutableStateOf("GPS") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Andro Iasana Vaovao (Shift)",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Ampidiro ny fampahalalana momba ny fiaingana anio:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )

                OutlinedTextField(
                    value = fuelText,
                    onValueChange = { fuelText = it },
                    label = { Text("Solika fiaingana (Litatra)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_initial_fuel"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = odoText,
                    onValueChange = { odoText = it },
                    label = { Text("Kaontera Odomètre fiaingana (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_start_odo"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = versementText,
                    onValueChange = { versementText = it },
                    label = { Text("Tanjona Versement tompony (Ar)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_target_versement"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phoneText,
                    onValueChange = { phoneText = it },
                    label = { Text("Laharana findain'ny tompony") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("input_owner_phone"),
                    singleLine = true
                )

                Text("Fomba fikajiana kilometatra:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("GPS", "ODOMETER", "DIRECT").forEach { mode ->
                        val isSelected = selectedMode == mode
                        val label = when (mode) {
                            "GPS" -> "GPS"
                            "ODOMETER" -> "Kaontera"
                            else -> "Mivantana"
                        }
                        Button(
                            onClick = { selectedMode = mode },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Slate700
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(label, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fuel = fuelText.toDoubleOrNull() ?: 5.0
                    val odo = odoText.toDoubleOrNull() ?: 0.0
                    val target = versementText.toDoubleOrNull() ?: 30000.0
                    onConfirm(fuel, odo, target, phoneText, selectedMode)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("confirm_new_shift")
            ) {
                Text("Manomboka")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Aoka ihany")
            }
        }
    )
}

@Composable
fun EndShiftDialog(
    estimatedFuelRemaining: Double,
    currentOdometer: Double,
    onDismiss: () -> Unit,
    onConfirm: (remainingFuel: Double, endOdo: Double) -> Unit
) {
    var fuelText by remember { mutableStateOf("%.1f".format(estimatedFuelRemaining)) }
    var odoText by remember { mutableStateOf(if (currentOdometer > 0) "%.1f".format(currentOdometer) else "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Famaranana ny Andro Iasana", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Hamarino ny solika sisa sy ny kaontera farany alohan'ny hanakatona ny andro:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )

                OutlinedTextField(
                    value = fuelText,
                    onValueChange = { fuelText = it },
                    label = { Text("Solika tavela marina (Litatra)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_end_fuel"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = odoText,
                    onValueChange = { odoText = it },
                    label = { Text("Kaontera Odomètre fahatongavana (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_end_odo"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val fuel = fuelText.toDoubleOrNull() ?: estimatedFuelRemaining
                    val odo = odoText.toDoubleOrNull() ?: currentOdometer
                    onConfirm(fuel, odo)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VibrantOrange),
                modifier = Modifier.testTag("confirm_end_shift")
            ) {
                Text("Akatony ny Andro")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Aoka ihany")
            }
        }
    )
}

@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit,
    onConfirm: (departure: String, destination: String, fare: Double, distance: Double, passengers: Int, notes: String) -> Unit
) {
    var departure by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var fareText by remember { mutableStateOf("2500") }
    var distanceText by remember { mutableStateOf("2.5") }
    var passengers by remember { mutableStateOf("1") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Ampidiro ny Course", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = departure,
                    onValueChange = { departure = it },
                    label = { Text("Toerana fiaingana (Depart)") },
                    placeholder = { Text("Ohatra: Bazar Be") },
                    modifier = Modifier.fillMaxWidth().testTag("input_course_departure"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Toerana hahatongavana (Destination)") },
                    placeholder = { Text("Ohatra: Tanambao V") },
                    modifier = Modifier.fillMaxWidth().testTag("input_course_destination"),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fareText,
                        onValueChange = { fareText = it },
                        label = { Text("Sandany (Ariary)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_course_fare"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = distanceText,
                        onValueChange = { distanceText = it },
                        label = { Text("Elanelana (km)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("input_course_distance"),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = passengers,
                        onValueChange = { passengers = it },
                        label = { Text("Isan'ny mpandeha") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Pitsopitsony (Notes)") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dep = departure.ifBlank { "Fiaingana" }
                    val dest = destination.ifBlank { "Fahatongavana" }
                    val fare = fareText.toDoubleOrNull() ?: 2000.0
                    val dist = distanceText.toDoubleOrNull() ?: 2.0
                    val pass = passengers.toIntOrNull() ?: 1
                    onConfirm(dep, dest, fare, dist, pass, notes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("confirm_add_course")
            ) {
                Text("Tehirizo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Aoka ihany")
            }
        }
    )
}

@Composable
fun AddRefuelDialog(
    onDismiss: () -> Unit,
    onConfirm: (liters: Double, cost: Double, station: String) -> Unit
) {
    var litersText by remember { mutableStateOf("2.0") }
    var costText by remember { mutableStateOf("11800") }
    var stationText by remember { mutableStateOf("Station Total") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Fampidirana Solika Fanampiny", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Ravitaillement solika mandritra ny andro iasana:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )

                OutlinedTextField(
                    value = litersText,
                    onValueChange = { litersText = it },
                    label = { Text("Habetsany (Litatra)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_refuel_liters"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it },
                    label = { Text("Vidiny (Ariary - MGA)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_refuel_cost"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = stationText,
                    onValueChange = { stationText = it },
                    label = { Text("Tobin-tsolika (Station)") },
                    modifier = Modifier.fillMaxWidth().testTag("input_refuel_station"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val liters = litersText.toDoubleOrNull() ?: 2.0
                    val cost = costText.toDoubleOrNull() ?: 11800.0
                    onConfirm(liters, cost, stationText)
                },
                colors = ButtonDefaults.buttonColors(containerColor = VibrantOrange),
                modifier = Modifier.testTag("confirm_add_refuel")
            ) {
                Text("Ampidiro")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Aoka ihany")
            }
        }
    )
}

@Composable
fun EditOdometerDialog(
    initialStartOdo: Double,
    initialEndOdo: Double,
    onDismiss: () -> Unit,
    onConfirm: (startOdo: Double, endOdo: Double) -> Unit
) {
    var startText by remember { mutableStateOf("%.1f".format(initialStartOdo)) }
    var endText by remember { mutableStateOf(if (initialEndOdo > 0) "%.1f".format(initialEndOdo) else "%.1f".format(initialStartOdo)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kaontera Odomètre", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Fikajiana ny kilometatra miainga amin'ny kaontera (Début sy Fin):",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )
                OutlinedTextField(
                    value = startText,
                    onValueChange = { startText = it },
                    label = { Text("Kaontera fiaingana - Début (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_odo_start"),
                    singleLine = true
                )
                OutlinedTextField(
                    value = endText,
                    onValueChange = { endText = it },
                    label = { Text("Kaontera ankehitriny - Fin (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_odo_end"),
                    singleLine = true
                )
                val start = startText.toDoubleOrNull() ?: initialStartOdo
                val end = endText.toDoubleOrNull() ?: initialEndOdo
                val diff = (end - start).coerceAtLeast(0.0)
                Text(
                    "Elanelana voakajy: %.1f km".format(diff),
                    fontWeight = FontWeight.Bold,
                    color = IndigoPrimary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val start = startText.toDoubleOrNull() ?: initialStartOdo
                    val end = endText.toDoubleOrNull() ?: initialEndOdo
                    onConfirm(start, end)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("confirm_edit_odo")
            ) {
                Text("Eny, tehirizo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Aoka ihany") }
        }
    )
}

@Composable
fun EditDirectKmDialog(
    currentKm: Double,
    onDismiss: () -> Unit,
    onConfirm: (km: Double) -> Unit
) {
    var kmText by remember { mutableStateOf("%.1f".format(currentKm)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fampidirana KM Mivantana", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Ampidiro mivantana ny tontalin'ny kilometatra natao androany:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate700
                )
                OutlinedTextField(
                    value = kmText,
                    onValueChange = { kmText = it },
                    label = { Text("Kilometatra tontaly (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("input_edit_direct_km"),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val km = kmText.toDoubleOrNull() ?: currentKm
                    onConfirm(km)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                modifier = Modifier.testTag("confirm_edit_direct_km")
            ) {
                Text("Eny, tehirizo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Aoka ihany") }
        }
    )
}
