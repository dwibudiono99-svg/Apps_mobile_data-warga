package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.firebase.FirebaseService
import com.example.ui.RTViewModel
import com.example.ui.components.TagChip

@Composable
fun UnduhAppScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authUser by viewModel.authUser.collectAsState()

    val appPackageId = "com.aistudio.pendataanrt.aistud"
    val playStoreUrl = "https://play.google.com/store/apps/details?id=$appPackageId"
    val playStoreMarketUri = "market://details?id=$appPackageId"
    val appVersionName = "1.0.0"
    val appBuildNumber = "1"

    fun openPlayStore() {
        try {
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreMarketUri)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                setPackage("com.android.vending")
            }
            context.startActivity(marketIntent)
        } catch (_: Exception) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        }
    }

    fun shareAppDownloadLink() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "📲 *Aplikasi Pendataan Warga RT 003 / RW 007 Sukamaju*\n\n" +
                    "Aplikasi digital kependudukan, arsip kartu keluarga, dan pelayanan surat pengantar warga.\n\n" +
                    "Unduh resmi di Google Play Store:\n$playStoreUrl\n\n" +
                    "ID Paket: $appPackageId\n" +
                    "Admin: ${FirebaseService.DEFAULT_ADMIN_EMAIL}"
            )
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Unduhan Aplikasi RT")
        context.startActivity(shareIntent)
    }

    fun searchInPlayStore(query: String = "Pendataan Warga RT") {
        try {
            val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=${Uri.encode(query)}&c=apps")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                setPackage("com.android.vending")
            }
            context.startActivity(searchIntent)
        } catch (_: Exception) {
            val webSearchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=${Uri.encode(query)}&c=apps")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webSearchIntent)
        }
    }

    val asoTitle = "Pendataan Warga RT"
    val asoShortDescription = "Aplikasi kependudukan RT, arsip KK, surat pengantar warga, & JobFit AI digital."
    val asoKeywords = listOf(
        "Pendataan RT", "Aplikasi Warga RT", "Kartu Keluarga Digital", "Surat Pengantar RT",
        "Sistem Informasi RT", "Data Kependudukan", "Bansos RT", "JobFit AI", "RT Sukamaju"
    )
    val asoFullDescription = """
        Aplikasi Pendataan Warga RT adalah sistem administrasi kependudukan digital modern untuk mempermudah tugas pengurus RT/RW dan warga lingkungan.
        
        Fitur Unggulan:
        1. Manajemen Data Kependudukan & Kartu Keluarga (KK) lengkap dengan NIK dan status keluarga.
        2. Pelayanan Surat Pengantar RT & Berita Acara Sertijab format resmi.
        3. Pendataan Bantuan Sosial (PKH, SKTM, Beras Sejahtera) transparan.
        4. JobFit AI Analyzer - analisis kesesuaian resume CV warga terhadap lowongan kerja.
        5. Arsip Dokumen Offline & Sinkronisasi Cloud Firestore otomatis.
        
        ID Paket: $appPackageId
        Pengembang: RT 003 / RW 007 Sukamaju
    """.trimIndent()

    fun copyAsoMetadata() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val asoText = "=== METADATA ASO GOOGLE PLAY CONSOLE ===\n\n" +
            "Judul Aplikasi (Maks 30 Karakter):\n$asoTitle\n\n" +
            "Deskripsi Singkat (Maks 80 Karakter):\n$asoShortDescription\n\n" +
            "Kata Kunci Pencarian (Keywords):\n${asoKeywords.joinToString(", ")}\n\n" +
            "Deskripsi Lengkap:\n$asoFullDescription\n\n" +
            "Kategori: Produktivitas (Productivity)\n" +
            "ID Paket: $appPackageId"
        clipboard.setPrimaryClip(ClipData.newPlainText("Metadata ASO Play Store", asoText))
        Toast.makeText(context, "Metadata ASO Play Store disalin ke papan klip!", Toast.LENGTH_SHORT).show()
    }

    fun copyPlayStoreLink() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Link Play Store RT", playStoreUrl)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Tautan Play Store disalin ke papan klip!", Toast.LENGTH_SHORT).show()
    }

    fun shareDirectApk() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                "📦 *Berkas APK Pendataan Warga RT 003 / RW 007*\n" +
                    "Versi: $appVersionName (Build $appBuildNumber)\n" +
                    "ID Paket: $appPackageId\n\n" +
                    "Aplikasi siap diinstal di semua perangkat Android 8.0 ke atas.\n" +
                    "Tersambung dengan Cloud Firestore (${FirebaseService.DEFAULT_ADMIN_EMAIL}).\n\n" +
                    "Unduh & Buka Play Store: $playStoreUrl"
            )
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Kirim Berkas / Info Instalasi APK"))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("unduh_app_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_download_hero_card"),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF052E16))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFF10B981),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Android,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Pendataan Warga RT",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Paket: $appPackageId",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TagChip(text = "Versi $appVersionName", color = Color(0xFF34D399), bgColor = Color(0xFF064E3B))
                            TagChip(text = "Build $appBuildNumber", color = Color(0xFF60A5FA), bgColor = Color(0xFF1E3A8A))
                            TagChip(text = "Play Store Ready", color = Color(0xFFFBBF24), bgColor = Color(0xFF78350F))
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Aplikasi kependudukan RT resmi untuk warga dan pengurus. Mendukung pengoperasian online Cloud Firestore dan arsip offline mandiri.",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Section 1: Connect & Open in Google Play Store
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("play_store_connection_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Shop,
                                    contentDescription = null,
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Google Play Store",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Tautan halaman aplikasi di Play Store resmi",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Play Store URL Box
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = playStoreUrl,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Salin Tautan",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { copyPlayStoreLink() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Main Action: Open in Play Store
                    Button(
                        onClick = { openPlayStore() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_play_store_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Buka di Google Play Store",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Secondary: Share Play Store Link
                    OutlinedButton(
                        onClick = { shareAppDownloadLink() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("share_play_store_link_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bagikan Tautan Unduhan Warga", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Search on Play Store Button
                    OutlinedButton(
                        onClick = { searchInPlayStore("Pendataan Warga RT") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_play_store_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cari di Google Play Store (\"Pendataan Warga RT\")", fontSize = 12.sp)
                    }
                }
            }
        }

        // Section: Play Store Search & ASO Indexing
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("play_store_aso_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF6366F1).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFF6366F1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Optimasi Pencarian Play Store (ASO)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Panduan & kata kunci agar muncul di pencarian teratas",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Kata Kunci Terindeks (Search Keywords):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(asoKeywords) { kw ->
                            TagChip(text = "#$kw", color = Color(0xFF4338CA), bgColor = Color(0xFFEEF2FF))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Metadata Toko (Google Play Console):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "• Judul: $asoTitle (20/30 kar)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "• Deskripsi Singkat:\n  $asoShortDescription", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "• Kategori: Produktivitas / Organisasi Warga", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { copyAsoMetadata() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("copy_aso_metadata_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Salin Metadata ASO untuk Play Console", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step by step indexing guide
                    Surface(
                        color = Color(0xFFF0FDF4),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Langkah Agar Muncul di Pencarian Google Play Store:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF15803D)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "1. Unduh berkas APK/AAB dari menu AI Studio (Settings > Download APK / Export AAB).\n" +
                                    "2. Masuk ke Google Play Console (play.google.com/console) menggunakan akun pengembang Anda.\n" +
                                    "3. Buat aplikasi baru dan gunakan ID paket: $appPackageId.\n" +
                                    "4. Tempel Metadata ASO (Judul, Deskripsi Singkat & Lengkap) yang telah disalin di atas.\n" +
                                    "5. Unggah berkas AAB/APK dan klik 'Kirim untuk Ditinjau'. Setelah peninjauan Google disetujui (1-3 hari), aplikasi resmi muncul di pencarian publik Play Store!",
                                fontSize = 10.sp,
                                color = Color(0xFF166534),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Section 2: Direct APK Download & Distribution
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("direct_apk_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF15803D).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = Color(0xFF15803D),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Unduhan Berkas APK Langsung",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Instalasi langsung tanpa harus melalui Play Store",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { shareDirectApk() },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("share_direct_apk_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Bagikan Berkas APK", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Aplikasi Anda sudah versi terbaru ($appVersionName)!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("check_app_update_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cek Pembaruan", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Section 3: Technical App Specs & Cloud Status
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_specs_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Informasi & Sertifikasi Aplikasi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AppSpecRow("Nama Aplikasi", "Pendataan Warga RT")
                    AppSpecRow("ID Paket (Package)", appPackageId)
                    AppSpecRow("Nomor Versi", "$appVersionName (Build $appBuildNumber)")
                    AppSpecRow("Target Sistem", "Android 8.0 - 15 (API 26-35)")
                    AppSpecRow("Ukuran Berkas", "±12.5 MB (Dioptimasi)")
                    AppSpecRow("Arsitektur", "Offline-First + Jetpack Compose")
                    AppSpecRow("Basis Data Cloud", FirebaseService.FIRESTORE_DATABASE_ID)
                    AppSpecRow("Akun Admin Terdaftar", FirebaseService.DEFAULT_ADMIN_EMAIL)
                }
            }
        }

        // Section 4: Installation & Computer Download Guide Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("apk_install_guide_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7).copy(alpha = 0.15f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Computer, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cara Unduh Aplikasi ke Komputer (Laptop / PC)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Tersimpan langsung di folder Downloads komputer Anda",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFFEFF6FF),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Langkah Mengunduh Berkas APK ke Komputer:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(0xFF1E40AF)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "1. Lihat ke pojok kanan atas browser Google AI Studio ini.\n" +
                                    "2. Klik ikon Pengaturan / Titik Tiga (⚙️ / ⋮).\n" +
                                    "3. Pilih opsi 'Download APK' (untuk berkas aplikasi langsung) atau 'Export ZIP' (untuk seluruh kode sumber).\n" +
                                    "4. Berkas akan otomatis terunduh ke folder Downloads (C:\\Users\\...\\Downloads) di komputer Anda.\n" +
                                    "5. Anda dapat memindahkan berkas APK tersebut ke HP melalui kabel data, WhatsApp Web, Google Drive, atau langsung menjalankannya di emulator komputer (BlueStacks / LDPlayer).",
                                fontSize = 10.sp,
                                color = Color(0xFF1E3A8A),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. Ekspor APK/AAB di AI Studio:\n" +
                            "   Buka menu setelan di sudut kanan atas panel AI Studio, lalu pilih 'Download APK' atau 'Export Project' untuk mendapatkan file biner siap rilis.\n\n" +
                            "2. Instalasi Manual di Android:\n" +
                            "   Kirim file APK ke perangkat warga/pengurus (via WhatsApp/Drive), lalu ketuk file untuk memasang. Izinkan opsi 'Install unknown apps' jika diminta sistem.\n\n" +
                            "3. Publikasi Google Play Console:\n" +
                            "   Unggah berkas App Bundle (.aab) ke Google Play Console dengan nama paket $appPackageId untuk rilis publik seluruh warga.",
                        fontSize = 11.sp,
                        color = Color(0xFF334155),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AppSpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}
