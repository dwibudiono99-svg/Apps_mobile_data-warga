package com.example.data.repository

import com.example.data.firebase.FirebaseService
import com.example.data.local.AppDatabase
import com.example.data.local.KKEntity
import com.example.data.local.PendukungEntity
import com.example.data.local.SettingsEntity
import com.example.data.local.SuratEntity
import com.example.data.local.WargaEntity
import com.example.data.sample.DefaultData
import com.example.model.KartuKeluarga
import com.example.model.PendukungKK
import com.example.model.RTSettings
import com.example.model.SuratRT
import com.example.model.Warga
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RTRepository(
    private val database: AppDatabase,
    val firebaseService: FirebaseService
) {
    private val dao = database.rtDao()

    val allKK: Flow<List<KartuKeluarga>> = dao.getAllKK().map { list ->
        list.map { it.toDomain() }
    }

    val allWarga: Flow<List<Warga>> = dao.getAllWarga().map { list ->
        list.map { it.toDomain() }
    }

    val firestoreResidents: Flow<List<Warga>?> = firebaseService.observeResidentsFromFirestore()

    suspend fun fetchResidentsFromFirestore(): Result<List<Warga>> {
        return firebaseService.fetchResidentsFromFirestore()
    }

    val allPendukung: Flow<List<PendukungKK>> = dao.getAllPendukung().map { list ->
        list.map { it.toDomain() }
    }

    val allSurat: Flow<List<SuratRT>> = dao.getAllSurat().map { list ->
        list.map { it.toDomain() }
    }

    val settings: Flow<RTSettings> = dao.getSettings().map { entity ->
        entity?.toDomain() ?: DefaultData.defaultSettings
    }

    suspend fun checkAndSeedInitialData() {
        val count = dao.getKKCount()
        if (count == 0) {
            dao.insertKKList(DefaultData.defaultKKList.map { KKEntity.fromDomain(it) })
            dao.insertWargaList(DefaultData.defaultWargaList.map { WargaEntity.fromDomain(it) })
            dao.insertPendukungList(DefaultData.defaultPendukungList.map { PendukungEntity.fromDomain(it) })
            dao.insertSuratList(DefaultData.defaultSuratList.map { SuratEntity.fromDomain(it) })
            dao.saveSettings(SettingsEntity.fromDomain(DefaultData.defaultSettings))
        }
    }

    suspend fun saveKK(kk: KartuKeluarga) {
        dao.insertKK(KKEntity.fromDomain(kk))
    }

    suspend fun deleteKK(id: String) {
        dao.deleteKKById(id)
    }

    suspend fun saveWarga(warga: Warga) {
        dao.insertWarga(WargaEntity.fromDomain(warga))
    }

    suspend fun deleteWarga(id: String) {
        dao.deleteWargaById(id)
    }

    suspend fun savePendukung(pendukung: PendukungKK) {
        dao.insertPendukung(PendukungEntity.fromDomain(pendukung))
    }

    suspend fun deletePendukung(id: String) {
        dao.deletePendukungById(id)
    }

    suspend fun saveSurat(surat: SuratRT) {
        dao.insertSurat(SuratEntity.fromDomain(surat))
    }

    suspend fun deleteSurat(id: String) {
        dao.deleteSuratById(id)
    }

    suspend fun updateSettings(settings: RTSettings) {
        dao.saveSettings(SettingsEntity.fromDomain(settings))
    }

    suspend fun syncToFirestore(): Result<String> {
        val kk = allKK.first()
        val warga = allWarga.first()
        val pendukung = allPendukung.first()
        val surat = allSurat.first()
        val s = settings.first()
        return firebaseService.uploadToFirestore(kk, warga, pendukung, surat, s)
    }

    suspend fun syncFromFirestore(): Result<String> {
        val downloadRes = firebaseService.downloadFromFirestore()
        if (downloadRes.isFailure) {
            return Result.failure(downloadRes.exceptionOrNull() ?: Exception("Gagal mengunduh data"))
        }
        val cloudData = downloadRes.getOrNull()
        if (cloudData == null) {
            return Result.failure(Exception("Tidak ada data di Cloud Firestore"))
        }

        if (cloudData.kkList.isNotEmpty()) {
            dao.insertKKList(cloudData.kkList.map { KKEntity.fromDomain(it) })
        }
        if (cloudData.wargaList.isNotEmpty()) {
            dao.insertWargaList(cloudData.wargaList.map { WargaEntity.fromDomain(it) })
        }

        return Result.success("Berhasil sinkronisasi dari Cloud Firestore (${cloudData.kkList.size} KK, ${cloudData.wargaList.size} Warga)")
    }

    suspend fun resetToDefaultData() {
        dao.insertKKList(DefaultData.defaultKKList.map { KKEntity.fromDomain(it) })
        dao.insertWargaList(DefaultData.defaultWargaList.map { WargaEntity.fromDomain(it) })
        dao.insertPendukungList(DefaultData.defaultPendukungList.map { PendukungEntity.fromDomain(it) })
        dao.insertSuratList(DefaultData.defaultSuratList.map { SuratEntity.fromDomain(it) })
        dao.saveSettings(SettingsEntity.fromDomain(DefaultData.defaultSettings))
    }
}
