package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.firebase.FirebaseService
import com.example.ui.RTViewModel
import com.example.ui.components.InitialsAvatar
import com.example.ui.components.TagChip

@Composable
fun FirebaseSyncScreen(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authUser by viewModel.authUser.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val lastSyncTime by viewModel.lastSyncTime.collectAsState()
    val kkList by viewModel.kkList.collectAsState()
    val wargaList by viewModel.wargaList.collectAsState()
    val pendukungList by viewModel.pendukungList.collectAsState()
    val suratList by viewModel.suratList.collectAsState()

    var emailInput by remember { mutableStateOf(FirebaseService.DEFAULT_ADMIN_EMAIL) }
    var passwordInput by remember { mutableStateOf(FirebaseService.DEFAULT_ADMIN_PASSWORD) }
    var isRegisterMode by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("firebase_sync_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Firebase Auth & Cloud Firestore",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Koneksi data warga RT ke akun Gmail resmi & Google Cloud Firestore",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Dedicated Account Connection Banner for dwibudiono99@admin.sma.belajar.id
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (authUser.isLoggedIn && authUser.email == FirebaseService.DEFAULT_ADMIN_EMAIL)
                        MaterialTheme.colorScheme.primaryContainer
                    else Color(0xFFF0FDF4)
                ),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF15803D))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Akun Resmi Pengurus RT",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color(0xFF14532D)
                            )
                            Text(
                                text = "Gmail: ${FirebaseService.DEFAULT_ADMIN_EMAIL}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF166534)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kata sandi: sama dengan email (${FirebaseService.DEFAULT_ADMIN_PASSWORD})",
                        fontSize = 11.sp,
                        color = Color(0xFF1F2937)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (authUser.isLoggedIn && authUser.email == FirebaseService.DEFAULT_ADMIN_EMAIL) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Status: Terhubung & Aktif",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF14532D)
                                    )
                                    Text(
                                        text = "Data tersambung dengan Cloud Firestore",
                                        fontSize = 10.sp,
                                        color = Color(0xFF166534)
                                    )
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.connectDefaultAdminAccount() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("connect_admin_account_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF15803D)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hubungkan Akun Resmi Ini Sekarang", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Account / Auth State Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Kelola Akun (Firebase Authentication)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (authUser.isLoggedIn) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            InitialsAvatar(
                                name = authUser.displayName ?: "Admin RT",
                                backgroundColor = MaterialTheme.colorScheme.primary,
                                sizeDp = 44
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = authUser.displayName ?: "Dwi Budiono",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = authUser.email ?: FirebaseService.DEFAULT_ADMIN_EMAIL,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                TagChip(
                                    text = if (authUser.isAnonymous) "Akses Tamu / Offline" else "Firebase Auth Terverifikasi",
                                    color = MaterialTheme.colorScheme.primary,
                                    bgColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            }

                            IconButton(
                                onClick = { viewModel.signOut() },
                                modifier = Modifier.testTag("logout_button")
                            ) {
                                Icon(Icons.Default.Logout, contentDescription = "Keluar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Google Sign-In Button
                            Button(
                                onClick = { viewModel.signInWithGoogle() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("google_signin_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF1E293B)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Masuk dengan Akun Google", fontWeight = FontWeight.SemiBold)
                            }

                            Divider(modifier = Modifier.padding(vertical = 4.dp))

                            // Email & Password Fields
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Pengurus RT") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("input_auth_email")
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Kata Sandi") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("input_auth_password")
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        if (isRegisterMode) {
                                            viewModel.signUpWithEmail(emailInput, passwordInput)
                                        } else {
                                            viewModel.signInWithEmail(emailInput, passwordInput)
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(if (isRegisterMode) "Daftar Akun" else "Masuk Email")
                                }

                                OutlinedButton(
                                    onClick = { viewModel.signInAnonymously() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Mode Tamu")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Firestore Cloud Database Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cloud Firestore Persistence",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Project: ${FirebaseService.FIREBASE_PROJECT_ID}\nDatabase ID: ${FirebaseService.FIRESTORE_DATABASE_ID}\nDokumen: /rt_warga_data/rt003_rw007_sukamaju",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (lastSyncTime != null) "Sinkronisasi Terakhir: $lastSyncTime" else "Belum pernah sinkronisasi cloud dalam sesi ini",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isSyncing) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sedang menyelaraskan data dengan Cloud Firestore...", fontSize = 11.sp)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.syncToFirestore() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("upload_to_firestore_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload ke Cloud", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.syncFromFirestore() },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("download_from_firestore_button"),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Unduh dari Cloud", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Summary of Data to Sync
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Ringkasan Data Tersinkronkan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    SyncSummaryRow("Data Kartu Keluarga (KK)", "${kkList.size} berkas")
                    SyncSummaryRow("Data Warga & Anggota Keluarga", "${wargaList.size} jiwa")
                    SyncSummaryRow("Data Pendukung KK & Bansos", "${pendukungList.size} catatan")
                    SyncSummaryRow("Arsip Surat Resmi & Sertijab", "${suratList.size} surat")
                }
            }
        }

        // Dedicated Web Drive Firebase Addresses (Clean, without Google AI Studio domain)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("firebase_web_drive_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0369A1).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.FolderShared,
                                    contentDescription = null,
                                    tint = Color(0xFF0369A1),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Alamat Web Drive & Cloud Storage",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Akses langsung drive berkas & database tanpa domain AI Studio",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Web Portal Sederhana
                    FirebaseWebLinkItem(
                        title = "1. Web Drive Portal (Sederhana)",
                        url = "https://gen-lang-client-0294820785.web.app",
                        badge = "Web App",
                        badgeColor = Color(0xFF15803D),
                        badgeBg = Color(0xFFDCFCE7),
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Web Drive RT", "https://gen-lang-client-0294820785.web.app"))
                            Toast.makeText(context, "Tautan Web Drive disalin!", Toast.LENGTH_SHORT).show()
                        },
                        onOpen = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gen-lang-client-0294820785.web.app"))
                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Direct Storage Bucket
                    FirebaseWebLinkItem(
                        title = "2. Direct Storage Bucket (Drive Dokumen)",
                        url = "https://storage.googleapis.com/gen-lang-client-0294820785.firebasestorage.app",
                        badge = "Cloud Storage",
                        badgeColor = Color(0xFF0369A1),
                        badgeBg = Color(0xFFE0F2FE),
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Direct Storage", "https://storage.googleapis.com/gen-lang-client-0294820785.firebasestorage.app"))
                            Toast.makeText(context, "Tautan Storage disalin!", Toast.LENGTH_SHORT).show()
                        },
                        onOpen = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://storage.googleapis.com/gen-lang-client-0294820785.firebasestorage.app"))
                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Google Cloud Storage Browser
                    FirebaseWebLinkItem(
                        title = "3. Google Cloud Console (Drive Explorer)",
                        url = "https://console.cloud.google.com/storage/browser/gen-lang-client-0294820785.firebasestorage.app",
                        badge = "GCP Console",
                        badgeColor = Color(0xFF7C3AED),
                        badgeBg = Color(0xFFF3E8FF),
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Console Drive", "https://console.cloud.google.com/storage/browser/gen-lang-client-0294820785.firebasestorage.app"))
                            Toast.makeText(context, "Tautan Console disalin!", Toast.LENGTH_SHORT).show()
                        },
                        onOpen = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://console.cloud.google.com/storage/browser/gen-lang-client-0294820785.firebasestorage.app"))
                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Firebase Console Storage
                    FirebaseWebLinkItem(
                        title = "4. Firebase Console (Drive & File)",
                        url = "https://console.firebase.google.com/project/gen-lang-client-0294820785/storage",
                        badge = "Firebase",
                        badgeColor = Color(0xFFEA580C),
                        badgeBg = Color(0xFFFFEDD5),
                        onCopy = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Firebase Storage", "https://console.firebase.google.com/project/gen-lang-client-0294820785/storage"))
                            Toast.makeText(context, "Tautan Firebase disalin!", Toast.LENGTH_SHORT).show()
                        },
                        onOpen = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://console.firebase.google.com/project/gen-lang-client-0294820785/storage"))
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FirebaseWebLinkItem(
    title: String,
    url: String,
    badge: String,
    badgeColor: Color,
    badgeBg: Color,
    onCopy: () -> Unit,
    onOpen: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TagChip(text = badge, color = badgeColor, bgColor = badgeBg)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = url,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Salin URL", fontSize = 10.sp)
                }

                Button(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Buka Web", fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun SyncSummaryRow(label: String, count: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = count, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
