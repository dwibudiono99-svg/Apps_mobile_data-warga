package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SuratRT
import com.example.ui.RTViewModel
import com.example.ui.components.OfficialLetterheadKop
import com.example.ui.components.TagChip
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuratScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val suratList by viewModel.suratList.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()
    val settings by viewModel.settings.collectAsState()

    var selectedSuratForPreview by remember { mutableStateOf<SuratRT?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var suratToDelete by remember { mutableStateOf<SuratRT?>(null) }
    var typeFilter by remember { mutableStateOf("ALL") }

    val sheetState = rememberModalBottomSheetState()

    val filteredSurat = if (typeFilter == "ALL") suratList else suratList.filter { it.type == typeFilter }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_create_surat")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Surat Baru")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .testTag("surat_screen")
        ) {
            Text(
                text = "Pelayanan Surat & Sertijab RT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Buat surat pengantar resmi RT, domisili, undangan, dan berita acara",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Tabs
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    FilterChip(
                        selected = typeFilter == "ALL",
                        onClick = { typeFilter = "ALL" },
                        label = { Text("Semua (${suratList.size})", fontSize = 11.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = typeFilter == "PENGANTAR",
                        onClick = { typeFilter = "PENGANTAR" },
                        label = { Text("Surat Pengantar", fontSize = 11.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = typeFilter == "DOMISILI",
                        onClick = { typeFilter = "DOMISILI" },
                        label = { Text("Domisili", fontSize = 11.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = typeFilter == "UNDANGAN",
                        onClick = { typeFilter = "UNDANGAN" },
                        label = { Text("Undangan Warga", fontSize = 11.sp) }
                    )
                }
                item {
                    FilterChip(
                        selected = typeFilter == "SERTIJAB",
                        onClick = { typeFilter = "SERTIJAB" },
                        label = { Text("Berita Acara Sertijab", fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredSurat.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada arsip surat pada kategori ini",
                            fontSize = 13.sp,
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
                    items(filteredSurat, key = { it.id }) { surat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSuratForPreview = surat }
                                .testTag("surat_card_${surat.id}"),
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
                                    TagChip(
                                        text = surat.type,
                                        color = MaterialTheme.colorScheme.primary,
                                        bgColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                    Row {
                                        IconButton(
                                            onClick = { selectedSuratForPreview = surat },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Visibility,
                                                contentDescription = "Lihat",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { suratToDelete = surat },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Delete,
                                                contentDescription = "Hapus",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = surat.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Nomor: ${surat.letterNumber}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Warga: ${surat.wargaName} • Keperluan: ${surat.purpose}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Tujuan: ${surat.recipient}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = surat.date,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Surat Detail / Official Preview Bottom Sheet
    selectedSuratForPreview?.let { surat ->
        ModalBottomSheet(
            onDismissRequest = { selectedSuratForPreview = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Official Kop
                OfficialLetterheadKop(
                    settings = settings,
                    nomorSurat = surat.letterNumber,
                    perihal = surat.title
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Letter Body Preview
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFAFAFA))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Yang bertanda tangan di bawah ini Pengurus RT 003 / RW 007 Kelurahan Sukamaju menerangkan bahwa:",
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(text = "• Nama Lengkap : ${surat.wargaName}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = "• NIK / Identitas : ${surat.wargaNik}", fontSize = 11.sp)
                    Text(text = "• Tujuan Lembaga : ${surat.recipient}", fontSize = 11.sp)
                    Text(text = "• Keperluan : ${surat.purpose}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = surat.content,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Sekretaris RT 03", fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(28.dp))
                            Text(text = "( Budi Santoso )", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Depok, ${surat.date}", fontSize = 10.sp)
                            Text(text = "Ketua RT 003 / RW 007", fontSize = 10.sp)
                            Spacer(modifier = Modifier.height(28.dp))
                            Text(text = "( Ahmad Suryanto )", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val shareText = """
                                KOP SURAT RT 003 / RW 007 SUKAMAJU
                                Nomor: ${surat.letterNumber}
                                Perihal: ${surat.title}
                                
                                Warga: ${surat.wargaName} (NIK: ${surat.wargaNik})
                                Keperluan: ${surat.purpose}
                                Tujuan: ${surat.recipient}
                                
                                Isi Surat:
                                ${surat.content}
                                
                                Ditetapkan di Depok, ${surat.date}
                                Ketua RT 003 / RW 007: Ahmad Suryanto
                            """.trimIndent()

                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TITLE, surat.title)
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Bagikan / Cetak Surat"))
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bagikan Teks")
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.showToast("Mempersiapkan dokumen siap cetak...")
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Cetak Surat")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Add New Surat Dialog
    if (showCreateDialog) {
        val nextNum = suratList.size + 48
        val currentMonthRoman = "III"
        val currentYear = "2026"
        val generatedNo = String.format("%03d/RT.03-RW.07/SK/%s/%s", nextNum, currentMonthRoman, currentYear)

        var formType by remember { mutableStateOf("PENGANTAR") }
        var formNo by remember { mutableStateOf(generatedNo) }
        var formTitle by remember { mutableStateOf("Surat Pengantar RT") }
        var formRecipient by remember { mutableStateOf("Kantor Kelurahan Sukamaju") }
        var formWargaName by remember { mutableStateOf(wargaList.firstOrNull()?.name ?: "Warga RT") }
        var formWargaNik by remember { mutableStateOf(wargaList.firstOrNull()?.nik ?: "3201011205800001") }
        var formPurpose by remember { mutableStateOf("Permohonan Pengurusan KTP / KK") }
        var formContent by remember {
            mutableStateOf("Menerangkan bahwa warga yang bersangkutan adalah penduduk warga kami dan berkelakuan baik di lingkungan RT.")
        }
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text("Buat Surat Resmi RT Baru", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = formNo,
                            onValueChange = { formNo = it },
                            label = { Text("Nomor Registrasi Surat *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_surat_no")
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formTitle,
                            onValueChange = { formTitle = it },
                            label = { Text("Perihal / Judul Surat *") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formRecipient,
                            onValueChange = { formRecipient = it },
                            label = { Text("Instansi / Pihak Penerima") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formWargaName,
                            onValueChange = { formWargaName = it },
                            label = { Text("Nama Warga Pemohon") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formWargaNik,
                            onValueChange = { formWargaNik = it },
                            label = { Text("NIK Warga") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formPurpose,
                            onValueChange = { formPurpose = it },
                            label = { Text("Tujuan / Keperluan Surat") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = formContent,
                            onValueChange = { formContent = it },
                            label = { Text("Isi Keterangan Surat") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (formNo.isNotBlank() && formTitle.isNotBlank()) {
                            viewModel.saveSurat(
                                id = null,
                                type = formType,
                                letterNumber = formNo,
                                title = formTitle,
                                recipient = formRecipient,
                                wargaNik = formWargaNik,
                                wargaName = formWargaName,
                                purpose = formPurpose,
                                content = formContent,
                                date = todayDate
                            )
                            showCreateDialog = false
                        } else {
                            viewModel.showToast("Nomor dan Judul Surat wajib diisi.")
                        }
                    },
                    modifier = Modifier.testTag("submit_create_surat_button")
                ) {
                    Text("Buat & Arsipkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Delete confirmation
    suratToDelete?.let { surat ->
        AlertDialog(
            onDismissRequest = { suratToDelete = null },
            title = { Text("Hapus Arsip Surat") },
            text = { Text("Hapus surat nomor ${surat.letterNumber} (${surat.title}) dari arsip?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSurat(surat.id)
                        suratToDelete = null
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { suratToDelete = null }) {
                    Text("Batal")
                }
            }
        )
    }
}
