package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OfflineDocumentItem
import com.example.model.PendukungKK
import com.example.ui.RTViewModel
import com.example.ui.components.TagChip

@Composable
fun PendukungScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val pendukungList by viewModel.pendukungList.collectAsState()
    val kkList by viewModel.kkList.collectAsState()

    var itemToEdit by remember { mutableStateOf<PendukungKK?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    itemToEdit = null
                    showFormDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_pendukung")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Data Pendukung")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("pendukung_screen")
        ) {
            Text(
                text = "Data Pendukung KK & Bansos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Kondisi tempat tinggal, bantuan sosial pemerintah, dan utilitas warga",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pendukungList, key = { it.id }) { item ->
                    val ownerKK = kkList.find { it.no == item.kk }
                    val ownerName = ownerKK?.kepala ?: "Keluarga No: ${item.kk}"

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pendukung_card_${item.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ownerName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "No. KK: ${item.kk} • Rumah: ${item.house}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        itemToEdit = item
                                        showFormDialog = true
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TagChip(
                                    text = "Status: ${item.houseStatus}",
                                    color = MaterialTheme.colorScheme.primary,
                                    bgColor = MaterialTheme.colorScheme.primaryContainer
                                )
                                TagChip(
                                    text = item.aid,
                                    color = if (item.aid.contains("PKH") || item.aid.contains("BPNT")) Color(0xFFBE185D) else Color(0xFF0F766E),
                                    bgColor = if (item.aid.contains("PKH") || item.aid.contains("BPNT")) Color(0xFFFCE7F3) else Color(0xFFCCFBF1)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFD97706))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Listrik: ${item.electricity}", fontSize = 11.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF0284C7))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Air: ${item.water}", fontSize = 11.sp)
                                }
                            }

                            if (item.asset.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Aset: ${item.asset}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (item.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Catatan: ${item.notes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Download buttons if files exist
                            if (item.aidDocName.isNotBlank() || item.houseDocName.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (item.aidDocName.isNotBlank()) {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.downloadDocument(
                                                    context,
                                                    OfflineDocumentItem(
                                                        id = "aid-${item.id}",
                                                        category = "BANTUAN",
                                                        categoryLabel = "Bansos",
                                                        title = "Bukti Bansos ${item.aid} - $ownerName",
                                                        fileName = item.aidDocName,
                                                        fileType = "image",
                                                        ownerName = ownerName,
                                                        kkNo = item.kk,
                                                        fileData = "DOKUMEN PENETAPAN BANTUAN SOSIAL\nJenis: ${item.aid}\nNama: $ownerName\nKK: ${item.kk}\nKeterangan: ${item.notes}",
                                                        estimatedSizeKb = 260
                                                    )
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                                        ) {
                                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Unduh Bansos", fontSize = 10.sp)
                                        }
                                    }

                                    if (item.houseDocName.isNotBlank()) {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.downloadDocument(
                                                    context,
                                                    OfflineDocumentItem(
                                                        id = "house-${item.id}",
                                                        category = "RUMAH",
                                                        categoryLabel = "Rumah/PBB",
                                                        title = "Bukti Tempat Tinggal - $ownerName",
                                                        fileName = item.houseDocName,
                                                        fileType = "image",
                                                        ownerName = ownerName,
                                                        kkNo = item.kk,
                                                        fileData = "DOKUMEN KEPEMILIKAN / PBB / SEWA\nStatus: ${item.houseStatus}\nRumah: ${item.house}\nNo KK: ${item.kk}",
                                                        estimatedSizeKb = 420
                                                    )
                                                )
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                                        ) {
                                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Unduh Berkas PBB", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showFormDialog) {
        var formKk by remember { mutableStateOf(itemToEdit?.kk ?: kkList.firstOrNull()?.no ?: "") }
        var formHouse by remember { mutableStateOf(itemToEdit?.house ?: "") }
        var formPhone by remember { mutableStateOf(itemToEdit?.phone ?: "") }
        var formStatus by remember { mutableStateOf(itemToEdit?.houseStatus ?: "Milik Sendiri") }
        var formAid by remember { mutableStateOf(itemToEdit?.aid ?: "Tidak Ada / Mandiri") }
        var formWater by remember { mutableStateOf(itemToEdit?.water ?: "PDAM & Sumur Bor") }
        var formElectricity by remember { mutableStateOf(itemToEdit?.electricity ?: "1300 VA") }
        var formAsset by remember { mutableStateOf(itemToEdit?.asset ?: "") }
        var formNotes by remember { mutableStateOf(itemToEdit?.notes ?: "") }

        AlertDialog(
            onDismissRequest = { showFormDialog = false },
            title = {
                Text(
                    text = if (itemToEdit != null) "Edit Data Pendukung KK" else "Tambah Data Pendukung KK",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = formKk,
                            onValueChange = { formKk = it },
                            label = { Text("Nomor KK Terkait *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_pendukung_kk")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formHouse,
                            onValueChange = { formHouse = it },
                            label = { Text("Alamat / Blok Rumah") },
                            placeholder = { Text("Contoh: No. 12 / Blok B4") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formStatus,
                            onValueChange = { formStatus = it },
                            label = { Text("Status Tempat Tinggal (Milik Sendiri / Kontrak / Sewa / Menumpang)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formAid,
                            onValueChange = { formAid = it },
                            label = { Text("Status Bansos (PKH / BPNT / KIS / Mandiri)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = formElectricity,
                                onValueChange = { formElectricity = it },
                                label = { Text("Daya Listrik") },
                                placeholder = { Text("900 VA / 1300 VA") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = formWater,
                                onValueChange = { formWater = it },
                                label = { Text("Sumber Air") },
                                placeholder = { Text("PDAM / Sumur") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = formAsset,
                            onValueChange = { formAsset = it },
                            label = { Text("Aset Utama Keluarga") },
                            placeholder = { Text("Sepeda motor, kulkas, peralatan...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formNotes,
                            onValueChange = { formNotes = it },
                            label = { Text("Catatan Verifikasi Petugas RT") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (formKk.isNotBlank()) {
                            viewModel.savePendukung(
                                id = itemToEdit?.id,
                                kk = formKk,
                                house = formHouse,
                                phone = formPhone,
                                houseStatus = formStatus,
                                aid = formAid,
                                water = formWater,
                                electricity = formElectricity,
                                asset = formAsset,
                                notes = formNotes
                            )
                            showFormDialog = false
                        } else {
                            viewModel.showToast("Nomor KK wajib diisi.")
                        }
                    },
                    modifier = Modifier.testTag("submit_save_pendukung_button")
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFormDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
