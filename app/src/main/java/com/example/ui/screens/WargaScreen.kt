package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Warga
import com.example.ui.RTViewModel
import com.example.ui.components.AppSearchBar
import com.example.ui.components.InitialsAvatar
import com.example.ui.components.TagChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WargaScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val wargaList by viewModel.filteredWargaList.collectAsState()
    val allWarga by viewModel.wargaList.collectAsState()
    val kkList by viewModel.kkList.collectAsState()
    val searchQuery by viewModel.wargaSearchQuery.collectAsState()
    val genderFilter by viewModel.wargaGenderFilter.collectAsState()
    val roleFilter by viewModel.wargaRoleFilter.collectAsState()

    var selectedWargaForDetail by remember { mutableStateOf<Warga?>(null) }
    var wargaToEdit by remember { mutableStateOf<Warga?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }
    var wargaToDelete by remember { mutableStateOf<Warga?>(null) }

    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    wargaToEdit = null
                    showFormDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_warga")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Warga")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("warga_screen")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Data Warga RT",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Total ${allWarga.size} Jiwa terdaftar di lingkungan RT 03",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.wargaSearchQuery.value = it },
                placeholder = "Cari Nama, NIK, atau Pekerjaan..."
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = genderFilter == "ALL",
                        onClick = { viewModel.wargaGenderFilter.value = "ALL" },
                        label = { Text("Semua Gender", fontSize = 11.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = genderFilter == "L",
                        onClick = { viewModel.wargaGenderFilter.value = "L" },
                        label = { Text("Laki-laki (${allWarga.count { it.gender == "L" }})", fontSize = 11.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = genderFilter == "P",
                        onClick = { viewModel.wargaGenderFilter.value = "P" },
                        label = { Text("Perempuan (${allWarga.count { it.gender == "P" }})", fontSize = 11.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = roleFilter == "PENGURUS",
                        onClick = {
                            viewModel.wargaRoleFilter.value = if (roleFilter == "PENGURUS") "ALL" else "PENGURUS"
                        },
                        label = { Text("Pengurus RT (${allWarga.count { it.rtRole.isNotBlank() }})", fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (wargaList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada warga yang cocok dengan pencarian",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(wargaList, key = { it.id }) { warga ->
                        WargaCardItem(
                            warga = warga,
                            onClick = { selectedWargaForDetail = warga },
                            onEdit = {
                                wargaToEdit = warga
                                showFormDialog = true
                            },
                            onDelete = { wargaToDelete = warga }
                        )
                    }
                }
            }
        }
    }

    // Detail Bottom Sheet
    selectedWargaForDetail?.let { warga ->
        ModalBottomSheet(
            onDismissRequest = { selectedWargaForDetail = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InitialsAvatar(
                        name = warga.name,
                        backgroundColor = if (warga.gender == "L") Color(0xFF0369A1) else Color(0xFFBE185D),
                        sizeDp = 48
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = warga.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = "NIK: ${warga.nik}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (warga.rtRole.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TagChip(
                        text = "Jabatan: ${warga.rtRole}",
                        color = MaterialTheme.colorScheme.primary,
                        bgColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                DetailItemRow(label = "Status Hubungan", value = warga.status)
                DetailItemRow(label = "Jenis Kelamin", value = if (warga.gender == "L") "Laki-laki" else "Perempuan")
                DetailItemRow(label = "Tempat, Tanggal Lahir", value = "${warga.birthplace}, ${warga.birth}")
                DetailItemRow(label = "No. Kartu Keluarga", value = warga.kk)
                DetailItemRow(label = "Pekerjaan", value = warga.job.ifEmpty { "-" })
                DetailItemRow(label = "No. HP / WhatsApp", value = warga.phone.ifEmpty { "-" })

                if (warga.notes.isNotBlank()) {
                    DetailItemRow(label = "Catatan Khusus", value = warga.notes)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (warga.phone.isNotBlank()) {
                    Button(
                        onClick = {
                            val cleanPhone = warga.phone.replace("-", "").replace(" ", "")
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Hubungi via WhatsApp (${warga.phone})", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Add / Edit Dialog
    if (showFormDialog) {
        var formNik by remember { mutableStateOf(wargaToEdit?.nik ?: "") }
        var formName by remember { mutableStateOf(wargaToEdit?.name ?: "") }
        var formGender by remember { mutableStateOf(wargaToEdit?.gender ?: "L") }
        var formBirthplace by remember { mutableStateOf(wargaToEdit?.birthplace ?: "Depok") }
        var formBirth by remember { mutableStateOf(wargaToEdit?.birth ?: "1990-01-01") }
        var formStatus by remember { mutableStateOf(wargaToEdit?.status ?: "Anggota Keluarga") }
        var formKk by remember { mutableStateOf(wargaToEdit?.kk ?: kkList.firstOrNull()?.no ?: "") }
        var formPhone by remember { mutableStateOf(wargaToEdit?.phone ?: "") }
        var formJob by remember { mutableStateOf(wargaToEdit?.job ?: "Karyawan Swasta") }
        var formRole by remember { mutableStateOf(wargaToEdit?.rtRole ?: "") }
        var formNotes by remember { mutableStateOf(wargaToEdit?.notes ?: "") }

        var expandedKKDropdown by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showFormDialog = false },
            title = {
                Text(
                    text = if (wargaToEdit != null) "Edit Data Warga" else "Tambah Warga Baru",
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
                            value = formNik,
                            onValueChange = { formNik = it },
                            label = { Text("NIK (16 Digit) *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_warga_nik")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formName,
                            onValueChange = { formName = it },
                            label = { Text("Nama Lengkap *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_warga_name")
                        )
                    }
                    item {
                        Text("Jenis Kelamin:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = formGender == "L",
                                onClick = { formGender = "L" }
                            )
                            Text("Laki-laki", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(
                                selected = formGender == "P",
                                onClick = { formGender = "P" }
                            )
                            Text("Perempuan", fontSize = 12.sp)
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedTextField(
                                value = formBirthplace,
                                onValueChange = { formBirthplace = it },
                                label = { Text("Tempat Lahir") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = formBirth,
                                onValueChange = { formBirth = it },
                                label = { Text("Tgl Lahir (YYYY-MM-DD)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = formStatus,
                            onValueChange = { formStatus = it },
                            label = { Text("Status Keluarga (Kepala / Istri / Anak)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formKk,
                            onValueChange = { formKk = it },
                            label = { Text("No. Kartu Keluarga (KK) Terkait *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formPhone,
                            onValueChange = { formPhone = it },
                            label = { Text("Nomor HP / WhatsApp") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formJob,
                            onValueChange = { formJob = it },
                            label = { Text("Pekerjaan") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formRole,
                            onValueChange = { formRole = it },
                            label = { Text("Jabatan RT (Opsional)") },
                            placeholder = { Text("Ketua RT / Sekretaris / Seksi...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formNotes,
                            onValueChange = { formNotes = it },
                            label = { Text("Catatan Tambahan") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (formNik.isNotBlank() && formName.isNotBlank() && formKk.isNotBlank()) {
                            viewModel.saveWarga(
                                id = wargaToEdit?.id,
                                nik = formNik,
                                name = formName,
                                gender = formGender,
                                birthplace = formBirthplace,
                                birth = formBirth,
                                status = formStatus,
                                kk = formKk,
                                phone = formPhone,
                                job = formJob,
                                rtRole = formRole,
                                notes = formNotes
                            )
                            showFormDialog = false
                        } else {
                            viewModel.showToast("NIK, Nama, dan No KK wajib diisi.")
                        }
                    },
                    modifier = Modifier.testTag("submit_save_warga_button")
                ) {
                    Text("Simpan Warga")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFormDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete Confirmation
    wargaToDelete?.let { warga ->
        AlertDialog(
            onDismissRequest = { wargaToDelete = null },
            title = { Text("Hapus Warga") },
            text = { Text("Hapus data warga ${warga.name} (NIK: ${warga.nik})?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteWarga(warga.id)
                        wargaToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { wargaToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun WargaCardItem(
    warga: Warga,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("warga_card_${warga.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialsAvatar(
                name = warga.name,
                backgroundColor = if (warga.gender == "L") Color(0xFF0369A1) else Color(0xFFBE185D),
                sizeDp = 40
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = warga.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                    if (warga.rtRole.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        TagChip(
                            text = warga.rtRole,
                            color = MaterialTheme.colorScheme.primary,
                            bgColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }

                Text(
                    text = "NIK: ${warga.nik} • ${warga.status}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (warga.job.isNotBlank()) {
                    Text(
                        text = "Pekerjaan: ${warga.job}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Row {
                IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
