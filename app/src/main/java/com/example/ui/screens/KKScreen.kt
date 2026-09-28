package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KartuKeluarga
import com.example.model.OfflineDocumentItem
import com.example.model.Warga
import com.example.ui.RTViewModel
import com.example.ui.components.AppSearchBar
import com.example.ui.components.InitialsAvatar
import com.example.ui.components.TagChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KKScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val kkList by viewModel.filteredKKList.collectAsState()
    val rawKKList by viewModel.kkList.collectAsState()
    val allWarga by viewModel.wargaList.collectAsState()
    val searchQuery by viewModel.kkSearchQuery.collectAsState()

    var selectedKKForDetail by remember { mutableStateOf<KartuKeluarga?>(null) }
    var kkToEdit by remember { mutableStateOf<KartuKeluarga?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }
    var kkToDelete by remember { mutableStateOf<KartuKeluarga?>(null) }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    kkToEdit = null
                    showFormDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_kk")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah KK")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("kk_screen")
        ) {
            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Data Kartu Keluarga",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Total ${rawKKList.size} Kepala Keluarga terdata di RT",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.kkSearchQuery.value = it },
                placeholder = "Cari No. KK, Nama Kepala, atau Alamat..."
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (kkList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.FamilyRestroom,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada data Kartu Keluarga",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(kkList, key = { it.id }) { kk ->
                        val familyMembers = allWarga.filter { it.kk == kk.no }
                        KKCardItem(
                            kk = kk,
                            familyCount = familyMembers.size,
                            onClick = { selectedKKForDetail = kk },
                            onEdit = {
                                kkToEdit = kk
                                showFormDialog = true
                            },
                            onDelete = { kkToDelete = kk }
                        )
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedKKForDetail?.let { kk ->
        val familyMembers = allWarga.filter { it.kk == kk.no }
        ModalBottomSheet(
            onDismissRequest = { selectedKKForDetail = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(
                            name = kk.kepala,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            sizeDp = 44
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = kk.kepala,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "No KK: ${kk.no}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    TagChip(
                        text = "${familyMembers.size} Jiwa",
                        color = MaterialTheme.colorScheme.primary,
                        bgColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Alamat:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "${kk.address}, ${kk.rtrw}", fontSize = 13.sp, fontWeight = FontWeight.Medium)

                if (kk.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Catatan Lingkungan:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = kk.notes, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Daftar Anggota Keluarga (${familyMembers.size})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                familyMembers.forEach { member ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = member.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                Text(
                                    text = "${member.status} • NIK: ${member.nik}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            TagChip(
                                text = if (member.gender == "L") "Laki-laki" else "Perempuan",
                                color = if (member.gender == "L") Color(0xFF0369A1) else Color(0xFFBE185D),
                                bgColor = if (member.gender == "L") Color(0xFFE0F2FE) else Color(0xFFFCE7F3)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Dokumen Pendukung Offline",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.downloadDocument(
                                context,
                                OfflineDocumentItem(
                                    id = "kk-${kk.id}",
                                    category = "KK",
                                    categoryLabel = "Kartu Keluarga",
                                    title = "Scan KK ${kk.kepala}",
                                    fileName = kk.kkFileName.ifEmpty { "KK_${kk.no}.pdf" },
                                    fileType = "pdf",
                                    ownerName = kk.kepala,
                                    kkNo = kk.no,
                                    fileData = "REPUBLIK INDONESIA - KARTU KELUARGA\nNo: ${kk.no}\nKepala: ${kk.kepala}\nAlamat: ${kk.address}\nRT/RW: ${kk.rtrw}\nJumlah Anggota: ${familyMembers.size} Jiwa\nCatatan: ${kk.notes}",
                                    estimatedSizeKb = 345
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unduh Scan KK", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.downloadDocument(
                                context,
                                OfflineDocumentItem(
                                    id = "ktp-${kk.id}",
                                    category = "KTP",
                                    categoryLabel = "KTP Kepala Keluarga",
                                    title = "Scan e-KTP ${kk.kepala}",
                                    fileName = kk.ktpFileName.ifEmpty { "KTP_${kk.no}.png" },
                                    fileType = "image",
                                    ownerName = kk.kepala,
                                    kkNo = kk.no,
                                    fileData = "PROVINSI JAWA BARAT - KOTA DEPOK\nNIK: ${kk.no.take(16)}\nNama: ${kk.kepala}\nAlamat: ${kk.address}",
                                    estimatedSizeKb = 180
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unduh KTP", fontSize = 11.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Add / Edit Dialog
    if (showFormDialog) {
        var formNo by remember { mutableStateOf(kkToEdit?.no ?: "") }
        var formKepala by remember { mutableStateOf(kkToEdit?.kepala ?: "") }
        var formAddress by remember { mutableStateOf(kkToEdit?.address ?: "") }
        var formRtRw by remember { mutableStateOf(kkToEdit?.rtrw ?: "RT 003 / RW 007") }
        var formNotes by remember { mutableStateOf(kkToEdit?.notes ?: "") }

        AlertDialog(
            onDismissRequest = { showFormDialog = false },
            title = {
                Text(
                    text = if (kkToEdit != null) "Edit Kartu Keluarga" else "Tambah Kartu Keluarga Baru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = formNo,
                        onValueChange = { formNo = it },
                        label = { Text("Nomor KK (16 Digit) *") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_kk_no")
                    )
                    OutlinedTextField(
                        value = formKepala,
                        onValueChange = { formKepala = it },
                        label = { Text("Nama Kepala Keluarga *") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_kk_kepala")
                    )
                    OutlinedTextField(
                        value = formAddress,
                        onValueChange = { formAddress = it },
                        label = { Text("Alamat Rumah") },
                        placeholder = { Text("Contoh: Jl. Kenanga No. 12") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formRtRw,
                        onValueChange = { formRtRw = it },
                        label = { Text("RT / RW") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = formNotes,
                        onValueChange = { formNotes = it },
                        label = { Text("Catatan Lingkungan") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (formNo.isNotBlank() && formKepala.isNotBlank()) {
                            viewModel.saveKK(
                                id = kkToEdit?.id,
                                no = formNo,
                                kepala = formKepala,
                                address = formAddress,
                                rtrw = formRtRw,
                                notes = formNotes
                            )
                            showFormDialog = false
                        } else {
                            viewModel.showToast("Nomor KK dan Nama Kepala Keluarga wajib diisi.")
                        }
                    },
                    modifier = Modifier.testTag("submit_save_kk_button")
                ) {
                    Text("Simpan KK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFormDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete confirmation
    kkToDelete?.let { kk ->
        AlertDialog(
            onDismissRequest = { kkToDelete = null },
            title = { Text("Hapus Kartu Keluarga") },
            text = { Text("Apakah Anda yakin ingin menghapus data KK ${kk.kepala} (No: ${kk.no})?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteKK(kk.id)
                        kkToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { kkToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun KKCardItem(
    kk: KartuKeluarga,
    familyCount: Int,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("kk_card_${kk.id}"),
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    InitialsAvatar(
                        name = kk.kepala,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        sizeDp = 40
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = kk.kepala,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "No. KK: ${kk.no}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit KK",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus KK",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${kk.address}, ${kk.rtrw}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TagChip(
                    text = "$familyCount Anggota",
                    color = MaterialTheme.colorScheme.secondary,
                    bgColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }
    }
}
