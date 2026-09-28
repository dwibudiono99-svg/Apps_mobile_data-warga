package com.example.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavPage
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DokumenOfflineScreen
import com.example.ui.screens.FirebaseSyncScreen
import com.example.ui.screens.JobFitAiScreen
import com.example.ui.screens.KKScreen
import com.example.ui.screens.PendukungScreen
import com.example.ui.screens.PengaturanScreen
import com.example.ui.screens.StatistikScreen
import com.example.ui.screens.StrukturScreen
import com.example.ui.screens.SuratScreen
import com.example.ui.screens.UnduhAppScreen
import com.example.ui.screens.WargaScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: RTViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentPage by viewModel.currentPage.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val authUser by viewModel.authUser.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Handle back button on sub-screens to return to Dashboard
    BackHandler(enabled = currentPage != NavPage.DASHBOARD) {
        viewModel.navigateTo(NavPage.DASHBOARD)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(20.dp)
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "RT 003 / RW 007",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Pendataan Warga RT",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kelurahan Sukamaju, Depok",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                    if (authUser.isLoggedIn) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Akun: ${authUser.displayName}",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f)
                ) {
                    val navItems = listOf(
                        Triple(NavPage.DASHBOARD, "Dashboard RT", Icons.Default.Home),
                        Triple(NavPage.KK, "Data Kartu Keluarga", Icons.Default.FamilyRestroom),
                        Triple(NavPage.WARGA, "Data Warga", Icons.Default.People),
                        Triple(NavPage.PENDUKUNG, "Data Pendukung & Bansos", Icons.Default.Handshake),
                        Triple(NavPage.SURAT, "Menu Surat & Sertijab", Icons.Default.Mail),
                        Triple(NavPage.STRUKTUR, "Struktur Organisasi RT", Icons.Default.AccountTree),
                        Triple(NavPage.OFFLINE_DOCS, "Unduhan Dokumen Offline", Icons.Default.Download),
                        Triple(NavPage.FIREBASE_SYNC, "Firebase Auth & Sync", Icons.Default.CloudSync),
                        Triple(NavPage.GRAFIK, "Grafik & Statistik", Icons.Default.BarChart),
                        Triple(NavPage.PENGATURAN, "Pengaturan Kop & Format", Icons.Default.Settings),
                        Triple(NavPage.UNDUH_APP, "Unduh & Play Store", Icons.Default.Shop),
                        Triple(NavPage.JOB_FIT_AI, "JobFit AI Analyzer", Icons.Default.AutoAwesome)
                    )

                    navItems.forEach { (page, label, icon) ->
                        NavigationDrawerItem(
                            icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
                            label = { Text(label, fontSize = 13.sp) },
                            selected = currentPage == page,
                            onClick = {
                                viewModel.navigateTo(page)
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier
                                .padding(vertical = 2.dp)
                                .testTag("nav_drawer_item_${page.id}"),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Divider()
                Text(
                    text = "Arsip Offline Mandiri • Versi 1.0",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = currentPage.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "RT 003 / RW 007 Sukamaju",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("top_bar_menu_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu Navigasi")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.navigateTo(NavPage.UNDUH_APP) },
                            modifier = Modifier.testTag("top_bar_unduh_app_button")
                        ) {
                            Icon(
                                Icons.Default.Shop,
                                contentDescription = "Unduh & Play Store",
                                tint = if (currentPage == NavPage.UNDUH_APP) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(
                            onClick = { viewModel.syncToFirestore() },
                            modifier = Modifier.testTag("top_bar_sync_button")
                        ) {
                            Icon(
                                Icons.Default.Sync,
                                contentDescription = "Sync Cloud",
                                tint = if (isSyncing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 3.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    val bottomItems = listOf(
                        Triple(NavPage.DASHBOARD, "Beranda", Icons.Default.Home),
                        Triple(NavPage.KK, "Data KK", Icons.Default.FamilyRestroom),
                        Triple(NavPage.WARGA, "Warga", Icons.Default.People),
                        Triple(NavPage.SURAT, "Surat", Icons.Default.Mail),
                        Triple(NavPage.OFFLINE_DOCS, "Dokumen", Icons.Default.Download)
                    )

                    bottomItems.forEach { (page, label, icon) ->
                        val selected = currentPage == page
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.navigateTo(page) },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("bottom_nav_${page.id}")
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentPage) {
                    NavPage.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onOpenAddKK = { viewModel.navigateTo(NavPage.KK) },
                        onOpenAddWarga = { viewModel.navigateTo(NavPage.WARGA) },
                        onOpenAddSurat = { viewModel.navigateTo(NavPage.SURAT) }
                    )
                    NavPage.KK -> KKScreen(viewModel = viewModel)
                    NavPage.WARGA -> WargaScreen(viewModel = viewModel)
                    NavPage.PENDUKUNG -> PendukungScreen(viewModel = viewModel)
                    NavPage.SURAT -> SuratScreen(viewModel = viewModel)
                    NavPage.STRUKTUR -> StrukturScreen(viewModel = viewModel)
                    NavPage.OFFLINE_DOCS -> DokumenOfflineScreen(viewModel = viewModel)
                    NavPage.FIREBASE_SYNC -> FirebaseSyncScreen(viewModel = viewModel)
                    NavPage.GRAFIK -> StatistikScreen(viewModel = viewModel)
                    NavPage.PENGATURAN -> PengaturanScreen(viewModel = viewModel)
                    NavPage.UNDUH_APP -> UnduhAppScreen(viewModel = viewModel)
                    NavPage.JOB_FIT_AI -> JobFitAiScreen(viewModel = viewModel)
                }
            }
        }
    }
}
