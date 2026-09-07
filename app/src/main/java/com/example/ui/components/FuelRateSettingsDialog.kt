package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import java.util.Locale

@Composable
fun FuelRateSettingsDialog(
    currentRate: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var rateInput by remember { mutableStateOf(String.format(Locale.FRENCH, "%.1f", currentRate)) }
    var isError by remember { mutableStateOf(false) }

    val presetRates = listOf(3.5, 4.5, 6.0, 8.4, 10.0)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = Slate100,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Column {
                    Text(
                        text = "Fandrindrana Solika",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Consommation L/100km",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Ampidiro eto ny tahan'ny solika lanin'ny Tricycle / Bajaj anao isaky ny 100 km:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate700,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = rateInput,
                    onValueChange = {
                        rateInput = it
                        val parsed = it.replace(',', '.').toDoubleOrNull()
                        isError = parsed == null || parsed <= 0.0
                    },
                    label = { Text("Taham-pisotroana (L/100km)") },
                    trailingIcon = {
                        Text(
                            text = "L / 100km",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocalGasStation,
                            contentDescription = null,
                            tint = IndigoPrimary
                        )
                    },
                    isError = isError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IndigoPrimary,
                        unfocusedBorderColor = Slate500.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_fuel_rate_100km")
                )

                if (isError) {
                    Text(
                        text = "Mampidira tarehimarika marina (oh: 8.4)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Safidy vonona matetika:",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate500,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetRates.forEach { rate ->
                        val isSelected = rateInput.replace(',', '.').toDoubleOrNull() == rate
                        Card(
                            onClick = {
                                rateInput = String.format(Locale.FRENCH, "%.1f", rate)
                                isError = false
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) IndigoPrimary else Slate100
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "%.1f".format(Locale.FRENCH, rate),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) PureWhite else Slate700
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = rateInput.replace(',', '.').toDoubleOrNull()
                    if (parsed != null && parsed > 0) {
                        onConfirm(parsed)
                    }
                },
                enabled = !isError && rateInput.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess)
            ) {
                Text("Ekeo sy Tehirizo", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Aoka ihany", color = Slate700)
            }
        },
        containerColor = PureWhite,
        shape = RoundedCornerShape(20.dp)
    )
}
