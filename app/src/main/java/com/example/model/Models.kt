package com.example.model

data class KartuKeluarga(
    val id: String = "",
    val no: String = "",
    val kepala: String = "",
    val address: String = "",
    val rtrw: String = "RT 003 / RW 007",
    val notes: String = "",
    val photo: String = "",
    val photoName: String = "",
    val kkFile: String = "",
    val kkFileName: String = "",
    val ktpFile: String = "",
    val ktpFileName: String = "",
    val createdAt: String = ""
)

data class Warga(
    val id: String = "",
    val nik: String = "",
    val name: String = "",
    val gender: String = "L", // "L" or "P"
    val birthplace: String = "",
    val birth: String = "", // "YYYY-MM-DD"
    val status: String = "Anggota Keluarga", // "Kepala Keluarga", "Istri/Suami", "Anak", "Orang Tua", "Famili Lain"
    val kk: String = "", // Reference to KK number
    val phone: String = "",
    val job: String = "",
    val notes: String = "",
    val rtRole: String = "", // e.g. "Ketua RT", "Wakil Ketua RT", "Sekretaris RT", "Bendahara RT", "Seksi Keamanan & Ketertiban", etc.
    val photo: String = "",
    val photoName: String = "",
    val docFile: String = "",
    val docFileName: String = ""
)

data class PendukungKK(
    val id: String = "",
    val kk: String = "",
    val house: String = "",
    val phone: String = "",
    val houseStatus: String = "Milik Sendiri", // "Milik Sendiri", "Kontrak", "Sewa", "Menumpang"
    val aid: String = "Tidak Ada / Mandiri", // "PKH", "BPNT", "KIS", "BLT", "SKTM", "Tidak Ada / Mandiri"
    val water: String = "PDAM", // "PDAM", "Sumur Bor", "PDAM & Sumur Bor", "Sumur Gali"
    val electricity: String = "1300 VA", // "450 VA", "900 VA", "1300 VA", "2200 VA", "3500 VA+"
    val asset: String = "",
    val notes: String = "",
    val aidDocFile: String = "",
    val aidDocName: String = "",
    val houseDocFile: String = "",
    val houseDocName: String = "",
    val extraDocFile: String = "",
    val extraDocName: String = ""
)

data class SuratRT(
    val id: String = "",
    val type: String = "PENGANTAR", // "PENGANTAR", "DOMISILI", "UNDANGAN", "SERTIJAB"
    val letterNumber: String = "",
    val title: String = "",
    val recipient: String = "",
    val wargaNik: String = "",
    val wargaName: String = "",
    val purpose: String = "",
    val content: String = "",
    val date: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class RTSettings(
    val title: String = "PENDATAAN WARGA RUKUN TETANGGA",
    val subtitle: String = "RT 003 / RW 007 - Kelurahan Sukamaju",
    val address: String = "Jl. Melati Raya No. 1, Sukamaju, Kec. Cilodong, Kota Depok",
    val contact: String = "0812-9876-5432 (Ketua RT)",
    val footer: String = "Sistem Informasi Pendataan Warga RT • Arsip Dokumen Offline Mandiri",
    val paperSize: String = "A4",
    val dateFormat: String = "dd-mmmm-yyyy",
    val driveFolderName: String = "PENDATAAN WARGA RT"
)

data class OfflineDocumentItem(
    val id: String,
    val category: String, // "KK", "KTP", "BANTUAN", "RUMAH", "FOTO", "SURAT"
    val categoryLabel: String,
    val title: String,
    val fileName: String,
    val fileType: String, // "image", "pdf", "text"
    val ownerName: String,
    val kkNo: String,
    val fileData: String, // Base64 or descriptive text content
    val estimatedSizeKb: Int
)

data class AuthUserState(
    val isLoggedIn: Boolean = false,
    val uid: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isAnonymous: Boolean = false
)

enum class NavPage(val id: String, val title: String) {
    DASHBOARD("dashboard", "Dashboard"),
    KK("kk", "Data KK"),
    WARGA("warga", "Data Warga"),
    PENDUKUNG("pendukung", "Pendukung KK"),
    SURAT("surat", "Surat & Sertijab"),
    STRUKTUR("struktur", "Struktur Organisasi"),
    OFFLINE_DOCS("offline-docs", "Dokumen Offline"),
    FIREBASE_SYNC("backup", "Firebase & Sync"),
    GRAFIK("grafik", "Grafik & Statistik"),
    PENGATURAN("pengaturan", "Pengaturan RT"),
    UNDUH_APP("unduh-app", "Unduh & Play Store"),
    JOB_FIT_AI("jobfit-ai", "JobFit AI Analyzer")
}

data class JobFitAnalysisResult(
    val id: String = java.util.UUID.randomUUID().toString(),
    val jobTitle: String = "",
    val matchScore: Int = 0,
    val matchLevel: String = "",
    val summary: String = "",
    val matchingSkills: List<String> = emptyList(),
    val missingSkills: List<String> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val interviewPitch: String = "",
    val isFromGemini: Boolean = false,
    val analyzedAt: Long = System.currentTimeMillis()
)
