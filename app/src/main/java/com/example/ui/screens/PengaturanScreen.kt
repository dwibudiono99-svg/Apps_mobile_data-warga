package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RTSettings
import com.example.ui.RTViewModel

@Composable
fun PengaturanScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val kkList by viewModel.kkList.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()

    var formTitle by remember(settings) { mutableStateOf(settings.title) }
    var formSubtitle by remember(settings) { mutableStateOf(settings.subtitle) }
    var formAddress by remember(settings) { mutableStateOf(settings.address) }
    var formContact by remember(settings) { mutableStateOf(settings.contact) }
    var formFooter by remember(settings) { mutableStateOf(settings.footer) }
    var formPaperSize by remember(settings) { mutableStateOf(settings.paperSize) }

    var showResetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("pengaturan_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Pengaturan Kop & Format RT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Kustomisasi kepala surat resmi, alamat sekretariat RT, dan format arsip",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Identitas Surat & Kop Resmi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = formTitle,
                        onValueChange = { formTitle = it },
                        label = { Text("Judul Kop Surat") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("input_settings_title")
                    )

                    OutlinedTextField(
                        value = formSubtitle,
                        onValueChange = { formSubtitle = it },
                        label = { Text("Sub-judul RT / RW & Kelurahan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formAddress,
                        onValueChange = { formAddress = it },
                        label = { Text("Alamat Sekretariat Balai Warga") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formContact,
                        onValueChange = { formContact = it },
                        label = { Text("Nomor Kontak Resmi Pengurus") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = formFooter,
                        onValueChange = { formFooter = it },
                        label = { Text("Catatan Kaki (Footer Dokumen)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            viewModel.updateSettings(
                                RTSettings(
                                    title = formTitle,
                                    subtitle = formSubtitle,
                                    address = formAddress,
                                    contact = formContact,
                                    footer = formFooter,
                                    paperSize = formPaperSize
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth().testTag("save_settings_button"),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Simpan Perubahan Kop")
                    }
                }
            }
        }

        // Backup & Export JSON
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Ekspor & Cadangan Cadangan Data",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ekspor ringkasan sensus warga dalam bentuk teks untuk arsip eksternal",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                val exportData = buildString {
                                    appendLine("=== DATA SENSUS WARGA RT 003 / RW 007 ===")
                                    appendLine("Waktu: 2026-09-27")
                                    appendLine("Total KK: ${kkList.size}")
                                    appendLine("Total Warga: ${wargaList.size}")
                                    appendLine("\n--- DAFTAR KEPALA KELUARGA ---")
                                    kkList.forEachIndexed { i, kk ->
                                        appendLine("${i + 1}. No KK: ${kk.no} | Kepala: ${kk.kepala} | Alamat: ${kk.address}")
                                    }
                                    appendLine("\n--- DAFTAR SELURUH WARGA ---")
                                    wargaList.forEachIndexed { i, w ->
                                        appendLine("${i + 1}. NIK: ${w.nik} | ${w.name} (${w.gender}) | ${w.status} | KK: ${w.kk} | HP: ${w.phone}")
                                    }
                                }

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TITLE, "Backup Data Warga RT")
                                    putExtra(Intent.EXTRA_TEXT, exportData)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Ekspor Data Warga RT"))
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bagikan Teks Rekap", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Data", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset ke Data Percontohan") },
            text = { Text("Apakah Anda yakin ingin memulihkan seluruh data KK, warga, dan surat ke data percontohan standar RT 003?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
