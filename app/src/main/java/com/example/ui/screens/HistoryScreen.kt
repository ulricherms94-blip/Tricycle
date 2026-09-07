package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Shift
import com.example.ui.theme.EmeraldGlow
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoContainer
import com.example.ui.theme.IndigoDark
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.PureWhite
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.VibrantOrangeDark
import com.example.ui.theme.VibrantOrangeLight
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    shifts: List<Shift>,
    onDeleteShift: (Shift) -> Unit
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.FRENCH) }
    val dateFormat = remember { SimpleDateFormat("EEEE dd MMMM yyyy", Locale.FRENCH) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.FRENCH) }

    var shiftToDelete by remember { mutableStateOf<Shift?>(null) }

    if (shiftToDelete != null) {
        val targetShift = shiftToDelete!!
        val dateStr = dateFormat.format(Date(targetShift.startTime)).replaceFirstChar { it.uppercase() }
        AlertDialog(
            onDismissRequest = { shiftToDelete = null },
            title = {
                Text(
                    text = "Hamafa ity andro iasana ity?",
                    fontWeight = FontWeight.Bold,
                    color = Slate900
                )
            },
            text = {
                Text(
                    text = "Tena hofafana tokoa ve ny andro $dateStr? Ho voafafa miaraka aminy koa ny courses sy ny solika rehetra voarakitra tao.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate700
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteShift(targetShift)
                        shiftToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFDC2626))
                ) {
                    Text("Eny, Fafao", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { shiftToDelete = null }) {
                    Text("Aoka ihany", color = Slate500)
                }
            },
            containerColor = PureWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Storage banner
        Surface(
            color = PureWhite,
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(IndigoContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = IndigoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column {
                    Text(
                        text = "Tahiry ao amin'ny finday (Offline)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Voatahiry ao amin'ny Room Database ny andro iasana rehetra na tsy misy connexion aza.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate500,
                        fontSize = 11.sp
                    )
                }
            }
        }

        if (shifts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Slate100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tsy mbola misy tantara voarakitra",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate700
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(shifts, key = { it.id }) { shift ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        shape = RoundedCornerShape(20.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Slate100, Slate200))),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth().testTag("history_shift_${shift.id}")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Date and status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = dateFormat.format(Date(shift.startTime)).replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                    val startStr = timeFormat.format(Date(shift.startTime))
                                    val endStr = shift.endTime?.let { timeFormat.format(Date(it)) } ?: "Ankehitriny"
                                    Text(
                                        text = "Ora: $startStr - $endStr",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate500
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (shift.isActive) {
                                        Surface(
                                            color = EmeraldLight,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Am-piasana",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldSuccess,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            color = Slate100,
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "Vita",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Slate700,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { shiftToDelete = shift },
                                        modifier = Modifier
                                            .padding(start = 6.dp)
                                            .size(36.dp)
                                            .testTag("delete_shift_${shift.id}")
                                    ) {
                                        Surface(
                                            color = Color(0xFFFEE2E2),
                                            shape = CircleShape,
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Fafao ity shift ity",
                                                    tint = Color(0xFFDC2626),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Stats grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Tanjona Versement",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "Ar ${numberFormat.format(shift.targetVersementAr)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Solika nampiasaina",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "%.1f L -> %.1f L".format(shift.initialFuelLiters, shift.remainingFuelLiters),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantOrangeDark
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Fomba KM",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = shift.distanceMode,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Slate700
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
