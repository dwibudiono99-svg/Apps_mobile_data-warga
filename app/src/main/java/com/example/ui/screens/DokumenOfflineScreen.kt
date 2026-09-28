package com.example.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OfflineDocumentItem
import com.example.ui.RTViewModel
import com.example.ui.components.TagChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DokumenOfflineScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val docList by viewModel.offlineDocuments.collectAsState()
    val categoryFilter by viewModel.docCategoryFilter.collectAsState()

    var selectedDocForPreview by remember { mutableStateOf<OfflineDocumentItem?>(null) }
    val sheetState = rememberModalBottomSheetState()

    val totalKb = docList.sumOf { it.estimatedSizeKb }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("offline_docs_screen")
    ) {
        // Header
        Text(
            text = "Pusat Unduhan Dokumen Offline",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Kelola & simpan seluruh scan berkas KK, e-KTP, dan dokumen warga secara lokal",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Offline Banner card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Akses Dokumen Tanpa Internet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Tersedia ${docList.size} Berkas siap unduh (~$totalKb KB)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.showToast("Membundel seluruh ${docList.size} berkas ke folder Download lokal...")
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("download_all_zip_button")
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Unduh Semua (.ZIP)", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Categories
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                FilterChip(
                    selected = categoryFilter == "ALL",
                    onClick = { viewModel.docCategoryFilter.value = "ALL" },
                    label = { Text("Semua Kategori", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = categoryFilter == "KK",
                    onClick = { viewModel.docCategoryFilter.value = "KK" },
                    label = { Text("Scan KK", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = categoryFilter == "KTP",
                    onClick = { viewModel.docCategoryFilter.value = "KTP" },
                    label = { Text("Scan e-KTP", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = categoryFilter == "BANTUAN",
                    onClick = { viewModel.docCategoryFilter.value = "BANTUAN" },
                    label = { Text("Bansos & SKTM", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = categoryFilter == "RUMAH",
                    onClick = { viewModel.docCategoryFilter.value = "RUMAH" },
                    label = { Text("Rumah & PBB", fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = categoryFilter == "SURAT",
                    onClick = { viewModel.docCategoryFilter.value = "SURAT" },
                    label = { Text("Arsip Surat", fontSize = 11.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(docList, key = { it.id }) { doc ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedDocForPreview = doc }
                        .testTag("doc_card_${doc.id}"),
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
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    when (doc.category) {
                                        "KK" -> Color(0xFFDCFCE7)
                                        "KTP" -> Color(0xFFE0F2FE)
                                        "BANTUAN" -> Color(0xFFFCE7F3)
                                        "RUMAH" -> Color(0xFFFEF3C7)
                                        else -> Color(0xFFF1F5F9)
                                    },
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (doc.fileType == "pdf") Icons.Default.PictureAsPdf else Icons.Default.FileOpen,
                                contentDescription = null,
                                tint = when (doc.category) {
                                    "KK" -> Color(0xFF15803D)
                                    "KTP" -> Color(0xFF0369A1)
                                    "BANTUAN" -> Color(0xFFBE185D)
                                    "RUMAH" -> Color(0xFFB45309)
                                    else -> Color(0xFF475569)
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doc.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "${doc.fileName} • ${doc.estimatedSizeKb} KB",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Pemilik: ${doc.ownerName} (KK: ${doc.kkNo})",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }

                        Button(
                            onClick = { viewModel.downloadDocument(context, doc) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Unduh", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Preview Bottom Sheet
    selectedDocForPreview?.let { doc ->
        ModalBottomSheet(
            onDismissRequest = { selectedDocForPreview = null },
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
                    Column {
                        Text(text = doc.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(
                            text = doc.fileName,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    TagChip(
                        text = doc.categoryLabel,
                        color = MaterialTheme.colorScheme.primary,
                        bgColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Pratinjau Dokumen Mandiri:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = doc.fileData,
                        color = Color(0xFF4ADE80),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.downloadDocument(context, doc)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Simpan / Bagikan Berkas (${doc.fileName})")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
