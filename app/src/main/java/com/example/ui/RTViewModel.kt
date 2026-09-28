package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.JobFitAiService
import com.example.data.firebase.FirebaseService
import com.example.data.local.AppDatabase
import com.example.data.repository.RTRepository
import com.example.data.sample.DefaultData
import com.example.model.AuthUserState
import com.example.model.JobFitAnalysisResult
import com.example.model.KartuKeluarga
import com.example.model.NavPage
import com.example.model.OfflineDocumentItem
import com.example.model.PendukungKK
import com.example.model.RTSettings
import com.example.model.SuratRT
import com.example.model.Warga
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class RTViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val firebaseService = FirebaseService(application)
    val repository = RTRepository(database, firebaseService)
    val jobFitAiService = JobFitAiService(firebaseService)

    // JobFit AI State
    val resumeInput = MutableStateFlow("")
    val jobDescInput = MutableStateFlow("")
    val jobTitleInput = MutableStateFlow("")
    val isAnalyzingJobFit = MutableStateFlow(false)
    val jobFitResult = MutableStateFlow<JobFitAnalysisResult?>(null)
    val jobFitError = MutableStateFlow<String?>(null)

    fun loadSampleResumeAndJob() {
        jobTitleInput.value = "Senior Android Developer"
        resumeInput.value = """
            Pengembang Android berpengalaman 4 tahun menguasai Kotlin, Jetpack Compose, Coroutines, Flow, dan Clean Architecture MVVM.
            
            Keahlian Utama:
            - Kotlin, Java, Jetpack Compose, XML
            - REST API (Retrofit, OkHttp, Moshi)
            - Room Database, Offline-First Architecture
            - Firebase (Authentication, Cloud Firestore, Cloud Messaging)
            - Unit Testing dengan Robolectric dan JUnit
            - Git, GitHub Actions, CI/CD
            
            Pengalaman Kerja:
            - Membangun aplikasi kependudukan dan manajemen RT digital untuk 500+ pengguna.
            - Mengoptimalkan waktu startup aplikasi sebesar 35% dan mereduksi ukuran APK hingga 20%.
            - Mengintegrasikan autentikasi Google Sign-In dan sinkronisasi data cloud real-time.
        """.trimIndent()

        jobDescInput.value = """
            Kami mencari Senior Android Developer yang berbakat untuk bergabung dengan tim engineering kami.
            
            Tanggung Jawab:
            - Mengembangkan aplikasi Android modern berskala tinggi menggunakan Kotlin dan Jetpack Compose.
            - Merancang arsitektur aplikasi yang scalable, modular, dan maintainable (MVVM / Clean Architecture).
            - Mengintegrasikan REST API, WebSocket, dan layanan backend Firebase.
            - Menulis unit test yang andal dan mengelola pipeline rilis aplikasi di Google Play Console.
            
            Kualifikasi:
            - Minimal 3 tahun pengalaman profesional dalam pengembangan Android native.
            - Mahir dalam Kotlin, Coroutines, Flow, dan Jetpack Compose.
            - Pengalaman kuat dengan Git, Room Database, dan Firebase Firestore.
            - Memiliki kemampuan komunikasi yang baik dan pola pikir pemecahan masalah (Problem Solving).
        """.trimIndent()
    }

    fun analyzeJobFit() {
        val resume = resumeInput.value.trim()
        val jobDesc = jobDescInput.value.trim()

        if (resume.isBlank()) {
            showToast("Harap masukkan teks resume / CV Anda")
            return
        }
        if (jobDesc.isBlank()) {
            showToast("Harap masukkan deskripsi lowongan pekerjaan")
            return
        }

        viewModelScope.launch {
            isAnalyzingJobFit.value = true
            jobFitError.value = null
            val result = jobFitAiService.analyzeResumeAgainstJob(
                resumeText = resume,
                jobDescription = jobDesc,
                jobTitle = jobTitleInput.value.trim()
            )
            isAnalyzingJobFit.value = false
            if (result.isSuccess) {
                jobFitResult.value = result.getOrNull()
                showToast("Analisis AI selesai! Skor kecocokan: ${jobFitResult.value?.matchScore}%")
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Gagal menganalisis resume"
                jobFitError.value = err
                showToast(err)
            }
        }
    }

    fun clearJobFit() {
        resumeInput.value = ""
        jobDescInput.value = ""
        jobTitleInput.value = ""
        jobFitResult.value = null
        jobFitError.value = null
    }

    private val _currentPage = MutableStateFlow(NavPage.DASHBOARD)
    val currentPage: StateFlow<NavPage> = _currentPage.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTime = MutableStateFlow<String?>(null)
    val lastSyncTime: StateFlow<String?> = _lastSyncTime.asStateFlow()

    // Auth State
    val authUser: StateFlow<AuthUserState> = firebaseService.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthUserState())

    // Raw Data
    val kkList: StateFlow<List<KartuKeluarga>> = repository.allKK
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wargaList: StateFlow<List<Warga>> = repository.allWarga
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendukungList: StateFlow<List<PendukungKK>> = repository.allPendukung
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suratList: StateFlow<List<SuratRT>> = repository.allSurat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<RTSettings> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DefaultData.defaultSettings)

    // Filter states
    val kkSearchQuery = MutableStateFlow("")
    val wargaSearchQuery = MutableStateFlow("")
    val wargaGenderFilter = MutableStateFlow("ALL") // "ALL", "L", "P"
    val wargaRoleFilter = MutableStateFlow("ALL") // "ALL", "PENGURUS", "WARGA"
    val docCategoryFilter = MutableStateFlow("ALL") // "ALL", "KK", "KTP", "BANTUAN", "RUMAH", "FOTO", "SURAT"

    // Dashboard Firestore Resident states
    val dashboardSearchQuery = MutableStateFlow("")
    val dashboardGenderFilter = MutableStateFlow("ALL")
    val dashboardRoleFilter = MutableStateFlow("ALL")
    val isFetchingDashboard = MutableStateFlow(false)

    val firestoreResidentsRaw: StateFlow<List<Warga>?> = repository.firestoreResidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dashboardResidents: StateFlow<List<Warga>> = combine(
        firestoreResidentsRaw, wargaList, dashboardSearchQuery, dashboardGenderFilter, dashboardRoleFilter
    ) { cloudWarga, localWarga, query, gender, role ->
        val sourceList = if (!cloudWarga.isNullOrEmpty()) cloudWarga else localWarga
        sourceList.filter { w ->
            val matchQuery = query.isBlank() ||
                w.name.contains(query, ignoreCase = true) ||
                w.nik.contains(query, ignoreCase = true) ||
                w.job.contains(query, ignoreCase = true) ||
                w.rtRole.contains(query, ignoreCase = true)

            val matchGender = gender == "ALL" || w.gender == gender
            val matchRole = role == "ALL" || (if (role == "PENGURUS") w.rtRole.isNotBlank() else w.rtRole.isBlank())

            matchQuery && matchGender && matchRole
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refreshDashboardFromFirestore() {
        viewModelScope.launch {
            isFetchingDashboard.value = true
            val res = repository.fetchResidentsFromFirestore()
            isFetchingDashboard.value = false
            if (res.isSuccess) {
                val list = res.getOrNull() ?: emptyList()
                showToast("Memuat ${list.size} data warga dari Cloud Firestore.")
            } else {
                showToast("Menggunakan data lokal RT (Offline mode).")
            }
        }
    }

    // Filtered lists
    val filteredKKList = combine(kkList, kkSearchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.no.contains(query, ignoreCase = true) ||
            it.kepala.contains(query, ignoreCase = true) ||
            it.address.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredWargaList = combine(
        wargaList, wargaSearchQuery, wargaGenderFilter, wargaRoleFilter
    ) { list, query, gender, role ->
        list.filter { w ->
            val matchQuery = query.isBlank() ||
                w.name.contains(query, ignoreCase = true) ||
                w.nik.contains(query, ignoreCase = true) ||
                w.job.contains(query, ignoreCase = true)

            val matchGender = gender == "ALL" || w.gender == gender
            val matchRole = role == "ALL" || (if (role == "PENGURUS") w.rtRole.isNotBlank() else w.rtRole.isBlank())

            matchQuery && matchGender && matchRole
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Extracted Offline Documents
    val offlineDocuments = combine(
        kkList, wargaList, pendukungList, suratList, docCategoryFilter
    ) { kks, wargas, pendukungs, surats, cat ->
        val list = mutableListOf<OfflineDocumentItem>()

        // KK & KTP files
        kks.forEach { kk ->
            if (kk.kkFileName.isNotBlank()) {
                list.add(
                    OfflineDocumentItem(
                        id = "doc-kk-${kk.id}",
                        category = "KK",
                        categoryLabel = "Kartu Keluarga",
                        title = "Berkas Scan KK - ${kk.kepala}",
                        fileName = kk.kkFileName,
                        fileType = if (kk.kkFileName.endsWith(".pdf")) "pdf" else "image",
                        ownerName = kk.kepala,
                        kkNo = kk.no,
                        fileData = "SCAN KARTU KELUARGA\nNo: ${kk.no}\nKepala: ${kk.kepala}\nAlamat: ${kk.address}\nRT/RW: ${kk.rtrw}\nCatatan: ${kk.notes}",
                        estimatedSizeKb = 345
                    )
                )
            }
            if (kk.ktpFileName.isNotBlank()) {
                list.add(
                    OfflineDocumentItem(
                        id = "doc-ktp-${kk.id}",
                        category = "KTP",
                        categoryLabel = "e-KTP Kepala Keluarga",
                        title = "Scan e-KTP - ${kk.kepala}",
                        fileName = kk.ktpFileName,
                        fileType = "image",
                        ownerName = kk.kepala,
                        kkNo = kk.no,
                        fileData = "IDENTITAS e-KTP KEPALA KELUARGA\nNama: ${kk.kepala}\nNo KK: ${kk.no}\nAlamat: ${kk.address}",
                        estimatedSizeKb = 180
                    )
                )
            }
        }

        // Aid & House Documents
        pendukungs.forEach { p ->
            val ownerKK = kks.find { it.no == p.kk }
            val owner = ownerKK?.kepala ?: "Warga RT"
            if (p.aidDocName.isNotBlank()) {
                list.add(
                    OfflineDocumentItem(
                        id = "doc-aid-${p.id}",
                        category = "BANTUAN",
                        categoryLabel = "Bantuan Sosial / SKTM",
                        title = "Bukti Bansos (${p.aid}) - $owner",
                        fileName = p.aidDocName,
                        fileType = "image",
                        ownerName = owner,
                        kkNo = p.kk,
                        fileData = "SURAT BUKTI KEPESERTAAN BANTUAN SOSIAL\nJenis: ${p.aid}\nKepala Keluarga: $owner\nNo KK: ${p.kk}\nKeterangan: ${p.notes}",
                        estimatedSizeKb = 260
                    )
                )
            }
            if (p.houseDocName.isNotBlank()) {
                list.add(
                    OfflineDocumentItem(
                        id = "doc-house-${p.id}",
                        category = "RUMAH",
                        categoryLabel = "Bukti Rumah / PBB",
                        title = "Bukti Tempat Tinggal - $owner",
                        fileName = p.houseDocName,
                        fileType = "image",
                        ownerName = owner,
                        kkNo = p.kk,
                        fileData = "DOKUMEN KEPEMILIKAN / SEWA RUMAH\nStatus: ${p.houseStatus}\nRumah: ${p.house}\nListrik: ${p.electricity}\nAir: ${p.water}",
                        estimatedSizeKb = 420
                    )
                )
            }
        }

        // Official Letters
        surats.forEach { s ->
            list.add(
                OfflineDocumentItem(
                    id = "doc-surat-${s.id}",
                    category = "SURAT",
                    categoryLabel = "Arsip Surat RT",
                    title = "${s.title} (${s.letterNumber})",
                    fileName = "Arsip_Surat_${s.letterNumber.replace('/', '_')}.txt",
                    fileType = "text",
                    ownerName = s.wargaName,
                    kkNo = s.wargaNik,
                    fileData = "ARSIP PERSURATAN RT 003 / RW 007\nNomor: ${s.letterNumber}\nPerihal: ${s.title}\nTujuan: ${s.recipient}\nNama: ${s.wargaName}\nKeperluan: ${s.purpose}\nIsi:\n${s.content}\nTanggal: ${s.date}",
                    estimatedSizeKb = 15
                )
            )
        }

        if (cat == "ALL") list
        else list.filter { it.category == cat }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
            // Auto-connect with user's specified Firebase Gmail account dwibudiono99@admin.sma.belajar.id
            firebaseService.initAdminSessionQuietly()
        }
    }

    fun connectDefaultAdminAccount() {
        viewModelScope.launch {
            _isSyncing.value = true
            val res = firebaseService.connectDefaultAdminAccount()
            val accountEmail = res.getOrNull() ?: FirebaseService.DEFAULT_ADMIN_EMAIL
            showToast("Akun Firebase terhubung: $accountEmail")
            val syncRes = repository.syncToFirestore()
            if (syncRes.isSuccess) {
                val timeStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
                _lastSyncTime.value = timeStr
            }
            _isSyncing.value = false
        }
    }

    fun navigateTo(page: NavPage) {
        _currentPage.value = page
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastEvent.emit(message)
        }
    }

    // KK CRUD
    fun saveKK(
        id: String?,
        no: String,
        kepala: String,
        address: String,
        rtrw: String,
        notes: String
    ) {
        viewModelScope.launch {
            val kkId = if (id.isNullOrBlank()) "kk-${UUID.randomUUID().toString().take(6)}" else id
            val existing = kkList.value.find { it.id == kkId }
            val item = KartuKeluarga(
                id = kkId,
                no = no.trim(),
                kepala = kepala.trim(),
                address = address.trim(),
                rtrw = rtrw.trim().ifEmpty { "RT 003 / RW 007" },
                notes = notes.trim(),
                photo = existing?.photo ?: "avatar_general",
                photoName = existing?.photoName ?: "Foto_${kepala.replace(' ', '_')}.png",
                kkFile = existing?.kkFile ?: "Scan_KK_${no}.pdf",
                kkFileName = existing?.kkFileName ?: "Scan_KK_${no}.pdf",
                ktpFile = existing?.ktpFile ?: "Scan_KTP_${no}.png",
                ktpFileName = existing?.ktpFileName ?: "Scan_KTP_${no}.png",
                createdAt = existing?.createdAt ?: SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
            )
            repository.saveKK(item)
            showToast("Data Kartu Keluarga berhasil disimpan.")
        }
    }

    fun deleteKK(id: String) {
        viewModelScope.launch {
            repository.deleteKK(id)
            showToast("Kartu Keluarga berhasil dihapus.")
        }
    }

    // Warga CRUD
    fun saveWarga(
        id: String?,
        nik: String,
        name: String,
        gender: String,
        birthplace: String,
        birth: String,
        status: String,
        kk: String,
        phone: String,
        job: String,
        rtRole: String,
        notes: String
    ) {
        viewModelScope.launch {
            val wId = if (id.isNullOrBlank()) "w-${UUID.randomUUID().toString().take(6)}" else id
            val existing = wargaList.value.find { it.id == wId }
            val item = Warga(
                id = wId,
                nik = nik.trim(),
                name = name.trim(),
                gender = gender,
                birthplace = birthplace.trim(),
                birth = birth.trim(),
                status = status,
                kk = kk.trim(),
                phone = phone.trim(),
                job = job.trim(),
                rtRole = rtRole.trim(),
                notes = notes.trim(),
                photo = existing?.photo ?: "",
                photoName = existing?.photoName ?: "Foto_${name.replace(' ', '_')}.png",
                docFile = existing?.docFile ?: "",
                docFileName = existing?.docFileName ?: ""
            )
            repository.saveWarga(item)
            showToast("Data Warga berhasil disimpan.")
        }
    }

    fun deleteWarga(id: String) {
        viewModelScope.launch {
            repository.deleteWarga(id)
            showToast("Data warga berhasil dihapus.")
        }
    }

    // Pendukung CRUD
    fun savePendukung(
        id: String?,
        kk: String,
        house: String,
        phone: String,
        houseStatus: String,
        aid: String,
        water: String,
        electricity: String,
        asset: String,
        notes: String
    ) {
        viewModelScope.launch {
            val pId = if (id.isNullOrBlank()) "p-${UUID.randomUUID().toString().take(6)}" else id
            val existing = pendukungList.value.find { it.id == pId }
            val item = PendukungKK(
                id = pId,
                kk = kk.trim(),
                house = house.trim(),
                phone = phone.trim(),
                houseStatus = houseStatus,
                aid = aid,
                water = water,
                electricity = electricity,
                asset = asset.trim(),
                notes = notes.trim(),
                aidDocFile = existing?.aidDocFile ?: if (aid != "Tidak Ada / Mandiri") "Bukti_Bansos_${kk}.png" else "",
                aidDocName = existing?.aidDocName ?: if (aid != "Tidak Ada / Mandiri") "Bukti_Bansos_${kk}.png" else "",
                houseDocFile = existing?.houseDocFile ?: "Bukti_Rumah_${kk}.png",
                houseDocName = existing?.houseDocName ?: "Bukti_Rumah_${kk}.png"
            )
            repository.savePendukung(item)
            showToast("Data pendukung KK berhasil disimpan.")
        }
    }

    // Surat RT CRUD
    fun saveSurat(
        id: String?,
        type: String,
        letterNumber: String,
        title: String,
        recipient: String,
        wargaNik: String,
        wargaName: String,
        purpose: String,
        content: String,
        date: String
    ) {
        viewModelScope.launch {
            val sId = if (id.isNullOrBlank()) "surat-${UUID.randomUUID().toString().take(6)}" else id
            val item = SuratRT(
                id = sId,
                type = type,
                letterNumber = letterNumber.trim(),
                title = title.trim(),
                recipient = recipient.trim(),
                wargaNik = wargaNik.trim(),
                wargaName = wargaName.trim(),
                purpose = purpose.trim(),
                content = content.trim(),
                date = date.trim().ifEmpty { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) },
                createdAt = System.currentTimeMillis()
            )
            repository.saveSurat(item)
            showToast("Surat resmi RT berhasil dibuat & diarsipkan.")
        }
    }

    fun deleteSurat(id: String) {
        viewModelScope.launch {
            repository.deleteSurat(id)
            showToast("Surat berhasil dihapus dari arsip.")
        }
    }

    // Settings
    fun updateSettings(s: RTSettings) {
        viewModelScope.launch {
            repository.updateSettings(s)
            showToast("Pengaturan kop dan format RT berhasil disimpan.")
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetToDefaultData()
            showToast("Data RT berhasil direset ke standar percontohan.")
        }
    }

    // Firebase Auth
    fun signInWithGoogle() {
        viewModelScope.launch {
            val result = firebaseService.signInWithGoogle()
            if (result.isSuccess) {
                showToast("Selamat datang, ${result.getOrNull()}!")
            } else {
                showToast("Login Google: ${result.exceptionOrNull()?.localizedMessage ?: "Dibatalkan"}")
            }
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            if (email.isBlank() || pass.length < 6) {
                showToast("Masukkan email dan password minimal 6 karakter.")
                return@launch
            }
            val res = firebaseService.signInWithEmail(email, pass)
            if (res.isSuccess) {
                showToast("Berhasil masuk sebagai ${res.getOrNull()}")
            } else {
                showToast("Gagal masuk: ${res.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun signUpWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            if (email.isBlank() || pass.length < 6) {
                showToast("Masukkan email dan password minimal 6 karakter.")
                return@launch
            }
            val res = firebaseService.signUpWithEmail(email, pass)
            if (res.isSuccess) {
                showToast("Pendaftaran berhasil! Akun langsung aktif.")
            } else {
                showToast("Pendaftaran gagal: ${res.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun signInAnonymously() {
        viewModelScope.launch {
            val res = firebaseService.signInAnonymously()
            if (res.isSuccess) {
                showToast("Masuk sebagai Tamu / Akses Mandiri")
            } else {
                showToast("Gagal masuk tamu: ${res.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun signOut() {
        firebaseService.signOut()
        showToast("Anda telah keluar.")
    }

    // Firebase Firestore Sync
    fun syncToFirestore() {
        viewModelScope.launch {
            _isSyncing.value = true
            val res = repository.syncToFirestore()
            _isSyncing.value = false
            if (res.isSuccess) {
                val timeStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
                _lastSyncTime.value = timeStr
                showToast("Sinkronisasi Cloud Firestore Berhasil! ($timeStr)")
            } else {
                showToast("Gagal sync ke Cloud: ${res.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    fun syncFromFirestore() {
        viewModelScope.launch {
            _isSyncing.value = true
            val res = repository.syncFromFirestore()
            _isSyncing.value = false
            if (res.isSuccess) {
                val timeStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
                _lastSyncTime.value = timeStr
                showToast(res.getOrNull() ?: "Sinkronisasi selesai!")
            } else {
                showToast("Gagal unduh dari Cloud: ${res.exceptionOrNull()?.localizedMessage}")
            }
        }
    }

    // File download / share simulation
    fun downloadDocument(context: Context, doc: OfflineDocumentItem) {
        try {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, doc.fileName)
            FileOutputStream(file).use { out ->
                out.write(doc.fileData.toByteArray(Charsets.UTF_8))
            }
            // Trigger share intent
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, doc.title)
                putExtra(Intent.EXTRA_SUBJECT, "Dokumen RT: ${doc.title}")
                putExtra(Intent.EXTRA_TEXT, doc.fileData)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Unduh / Bagikan Dokumen")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
            Toast.makeText(context, "Berkas '${doc.fileName}' siap disimpan / dibagikan.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal mengunduh: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
