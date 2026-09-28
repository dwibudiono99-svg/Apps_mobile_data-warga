package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.firebase.FirebaseService
import com.example.model.NavPage
import com.example.model.Warga
import com.example.ui.RTViewModel
import com.example.ui.components.AppKopHeader
import com.example.ui.components.AppSearchBar
import com.example.ui.components.InitialsAvatar
import com.example.ui.components.StatCard
import com.example.ui.components.TagChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: RTViewModel,
    onOpenAddKK: () -> Unit,
    onOpenAddWarga: () -> Unit,
    onOpenAddSurat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()
    val authUser by viewModel.authUser.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val isFetchingDashboard by viewModel.isFetchingDashboard.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()

    val kkList by viewModel.kkList.collectAsState()
    val rawWargaList by viewModel.wargaList.collectAsState()
    val pendukungList by viewModel.pendukungList.collectAsState()
    val firestoreResidentsRaw by viewModel.firestoreResidentsRaw.collectAsState()

    // Residents fetched from Firestore with Search and Filter applied
    val dashboardResidents by viewModel.dashboardResidents.collectAsState()
    val searchQuery by viewModel.dashboardSearchQuery.collectAsState()
    val genderFilter by viewModel.dashboardGenderFilter.collectAsState()
    val roleFilter by viewModel.dashboardRoleFilter.collectAsState()

    var selectedWargaForDetail by remember { mutableStateOf<Warga?>(null) }
    val sheetState = rememberModalBottomSheetState()

    val totalMale = rawWargaList.count { it.gender == "L" }
    val totalFemale = rawWargaList.count { it.gender == "P" }
    val totalBansos = pendukungList.count { it.aid != "Tidak Ada / Mandiri" }
    val isCloudLive = firestoreResidentsRaw != null

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Kop Banner
        item {
            AppKopHeader(settings = settings)
        }

        // Hero Community Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(145.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_community_banner),
                        contentDescription = "Suasana Guyub Lingkungan Rukun Tetangga",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xCC052E16)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Surface(
                            color = Color(0xFF15803D),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "SISTEM PENDATAAN DIGITAL RT",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dashboard Direktori Warga RT",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Terhubung dengan Google Cloud Firestore (${FirebaseService.DEFAULT_ADMIN_EMAIL})",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Metrics Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    title = "Total Warga",
                    value = "${rawWargaList.size}",
                    subtitle = "$totalMale L / $totalFemale P",
                    icon = Icons.Default.People,
                    containerColor = Color(0xFFE0F2FE),
                    iconTint = Color(0xFF0369A1),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(NavPage.WARGA) }
                )
                StatCard(
                    title = "Kartu Keluarga",
                    value = "${kkList.size}",
                    subtitle = "KK Terdata",
                    icon = Icons.Default.FamilyRestroom,
                    containerColor = Color(0xFFDCFCE7),
                    iconTint = Color(0xFF15803D),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(NavPage.KK) }
                )
                StatCard(
                    title = "Penerima Bansos",
                    value = "$totalBansos",
                    subtitle = "PKH / SKTM",
                    icon = Icons.Default.Handshake,
                    containerColor = Color(0xFFFCE7F3),
                    iconTint = Color(0xFFBE185D),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo(NavPage.PENDUKUNG) }
                )
            }
        }

        // Quick Actions Row
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenAddWarga,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dashboard_add_warga_btn"),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Warga", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onOpenAddKK,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("dashboard_add_kk_btn"),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ KK", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onOpenAddSurat,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("dashboard_add_surat_btn"),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Surat RT", fontSize = 11.sp)
                    }
                }
            }
        }

        // Unduhan Aplikasi & Play Store Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(NavPage.UNDUH_APP) }
                    .testTag("dashboard_playstore_banner"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF0284C7),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Shop,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Unduh di Google Play Store",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF15803D)
                            ) {
                                Text(
                                    text = "v1.0.0",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Unduh file APK resmi atau buka tautan Play Store aplikasi warga",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Buka",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // JobFit AI Analyzer Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(NavPage.JOB_FIT_AI) }
                    .testTag("dashboard_jobfit_banner"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1B4B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF6366F1),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "JobFit AI - Resume & Job Analyzer",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF4338CA)
                            ) {
                                Text(
                                    text = "Gemini",
                                    color = Color(0xFFA5B4FC),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Analisis kecocokan teks resume CV & deskripsi lowongan kerja",
                            color = Color(0xFFC7D2FE),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Buka",
                        tint = Color(0xFFA5B4FC),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // ==========================================
        // MAIN SECTION: RESIDENTS LIST FROM FIRESTORE
        // ==========================================
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("firestore_residents_section"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Section Header with Live Firestore Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isCloudLive) Color(0xFF16A34A) else Color(0xFF2563EB))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Daftar Warga RT (Cloud Firestore)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Refresh Button
                        if (isFetchingDashboard || isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                modifier = Modifier.clickable { viewModel.refreshDashboardFromFirestore() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Segarkan",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Sync Cloud",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Data tersinkronisasi otomatis dengan basis data Firestore RT",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Bar to filter residents by name
                    AppSearchBar(
                        query = searchQuery,
                        onQueryChange = { viewModel.dashboardSearchQuery.value = it },
                        placeholder = "Cari nama warga RT...",
                        modifier = Modifier.testTag("dashboard_resident_search_bar")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter chips row
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = genderFilter == "ALL" && roleFilter == "ALL",
                                onClick = {
                                    viewModel.dashboardGenderFilter.value = "ALL"
                                    viewModel.dashboardRoleFilter.value = "ALL"
                                },
                                label = { Text("Semua (${rawWargaList.size})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = genderFilter == "L",
                                onClick = {
                                    viewModel.dashboardGenderFilter.value = if (genderFilter == "L") "ALL" else "L"
                                },
                                label = { Text("Laki-laki ($totalMale)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFE0F2FE),
                                    selectedLabelColor = Color(0xFF0369A1)
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = genderFilter == "P",
                                onClick = {
                                    viewModel.dashboardGenderFilter.value = if (genderFilter == "P") "ALL" else "P"
                                },
                                label = { Text("Perempuan ($totalFemale)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFFCE7F3),
                                    selectedLabelColor = Color(0xFFBE185D)
                                )
                            )
                        }
                        item {
                            FilterChip(
                                selected = roleFilter == "PENGURUS",
                                onClick = {
                                    viewModel.dashboardRoleFilter.value = if (roleFilter == "PENGURUS") "ALL" else "PENGURUS"
                                },
                                label = { Text("Pengurus RT", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFDCFCE7),
                                    selectedLabelColor = Color(0xFF15803D)
                                )
                            )
                        }
                    }

                    // Result count indicator
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Menampilkan ${dashboardResidents.size} warga",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (searchQuery.isNotBlank() || genderFilter != "ALL" || roleFilter != "ALL") {
                            Text(
                                text = "Reset Filter",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable {
                                    viewModel.dashboardSearchQuery.value = ""
                                    viewModel.dashboardGenderFilter.value = "ALL"
                                    viewModel.dashboardRoleFilter.value = "ALL"
                                }
                            )
                        }
                    }
                }
            }
        }

        // Empty state when search yields no residents
        if (dashboardResidents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .testTag("dashboard_empty_residents_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tidak ada warga bernama \"$searchQuery\"",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Periksa kembali ejaan nama atau kata kunci pencarian",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                viewModel.dashboardSearchQuery.value = ""
                                viewModel.dashboardGenderFilter.value = "ALL"
                                viewModel.dashboardRoleFilter.value = "ALL"
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Tampilkan Semua Warga", fontSize = 11.sp)
                        }
                    }
                }
            }
        } else {
            // Residents list items fetched from Firestore
            items(dashboardResidents, key = { it.id }) { resident ->
                DashboardResidentItemCard(
                    resident = resident,
                    onClick = { selectedWargaForDetail = resident },
                    onContact = { phone ->
                        val cleanPhone = phone.replace("-", "").replace(" ", "")
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }

    // Resident Detail Modal Bottom Sheet
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
                        text = "Jabatan RT: ${warga.rtRole}",
                        color = MaterialTheme.colorScheme.primary,
                        bgColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider()
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Status Keluarga", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = warga.status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Jenis Kelamin", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = if (warga.gender == "L") "Laki-laki" else "Perempuan", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Tempat, Tgl Lahir", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "${warga.birthplace}, ${warga.birth}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Nomor KK", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = warga.kk, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Pekerjaan", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = warga.job.ifEmpty { "-" }, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                if (warga.notes.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Catatan RT", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = warga.notes, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
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
}

@Composable
fun DashboardResidentItemCard(
    resident: Warga,
    onClick: () -> Unit,
    onContact: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("dashboard_resident_item_${resident.id}"),
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
                name = resident.name,
                backgroundColor = if (resident.gender == "L") Color(0xFF0369A1) else Color(0xFFBE185D),
                sizeDp = 42
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = resident.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    TagChip(
                        text = if (resident.gender == "L") "L" else "P",
                        color = if (resident.gender == "L") Color(0xFF0369A1) else Color(0xFFBE185D),
                        bgColor = if (resident.gender == "L") Color(0xFFE0F2FE) else Color(0xFFFCE7F3)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "NIK: ${resident.nik} • ${resident.status}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (resident.rtRole.isNotBlank()) {
                        TagChip(
                            text = resident.rtRole,
                            color = MaterialTheme.colorScheme.primary,
                            bgColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                    if (resident.job.isNotBlank()) {
                        Text(
                            text = resident.job,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action: Contact or View
            if (resident.phone.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFDCFCE7),
                    modifier = Modifier.clickable { onContact(resident.phone) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "WhatsApp",
                        tint = Color(0xFF15803D),
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = "Lihat Detail",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
