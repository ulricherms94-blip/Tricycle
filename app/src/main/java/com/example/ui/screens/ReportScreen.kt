package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DashboardUiState
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoContainer
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

@Composable
fun ReportScreen(
    state: DashboardUiState,
    reportText: String
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    fun copyToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Tatitra Tricycle Pro", reportText)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Voakopia ny tatitra!", Toast.LENGTH_SHORT).show()
    }

    fun sendViaSms() {
        val phone = state.activeShift?.ownerPhoneNumber ?: ""
        val uri = if (phone.isNotBlank()) Uri.parse("smsto:$phone") else Uri.parse("smsto:")
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", reportText)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Tsy afaka mandefa SMS", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendViaWhatsApp() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, reportText)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to general share intent if WhatsApp is not installed
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, reportText)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Zarao ny tatitra"))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Offline Status Badge
        Card(
            colors = CardDefaults.cardColors(containerColor = EmeraldLight),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldSuccess),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Miasa 100% na tsy misy Internet aza",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Tsy very ny kaonty, voatahiry ao amin'ny finday avokoa ny vola sy ny solika.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate700,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // WhatsApp button
            Button(
                onClick = { sendViaWhatsApp() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f).height(50.dp).testTag("button_send_whatsapp")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }

            // SMS button
            Button(
                onClick = { sendViaSms() },
                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.weight(1f).height(50.dp).testTag("button_send_sms")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SMS", fontWeight = FontWeight.Bold, color = PureWhite)
                }
            }
        }

        // Copy button
        OutlinedButton(
            onClick = { copyToClipboard() },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("button_copy_report")
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
                tint = IndigoPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Adikao ny tatitra (Copier)", fontWeight = FontWeight.Bold, color = IndigoPrimary)
        }

        // Preview of the formatted text
        Card(
            colors = CardDefaults.cardColors(containerColor = PureWhite),
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Slate100, Slate200))),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "JEREN-TODEHA NY TATITRA (APERCU)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Slate500,
                        letterSpacing = 1.sp
                    )
                    Surface(
                        color = IndigoContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Malagasy",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = Slate50,
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Text(
                        text = reportText,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = Slate900,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    )
                }
            }
        }
    }
}
