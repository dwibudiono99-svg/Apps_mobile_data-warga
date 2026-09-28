package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.KartuKeluarga
import com.example.model.PendukungKK
import com.example.model.RTSettings
import com.example.model.SuratRT
import com.example.model.Warga
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "kartu_keluarga")
data class KKEntity(
    @PrimaryKey val id: String,
    val no: String,
    val kepala: String,
    val address: String,
    val rtrw: String,
    val notes: String,
    val photo: String,
    val photoName: String,
    val kkFile: String,
    val kkFileName: String,
    val ktpFile: String,
    val ktpFileName: String,
    val createdAt: String
) {
    fun toDomain() = KartuKeluarga(
        id = id,
        no = no,
        kepala = kepala,
        address = address,
        rtrw = rtrw,
        notes = notes,
        photo = photo,
        photoName = photoName,
        kkFile = kkFile,
        kkFileName = kkFileName,
        ktpFile = ktpFile,
        ktpFileName = ktpFileName,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(m: KartuKeluarga) = KKEntity(
            id = m.id,
            no = m.no,
            kepala = m.kepala,
            address = m.address,
            rtrw = m.rtrw,
            notes = m.notes,
            photo = m.photo,
            photoName = m.photoName,
            kkFile = m.kkFile,
            kkFileName = m.kkFileName,
            ktpFile = m.ktpFile,
            ktpFileName = m.ktpFileName,
            createdAt = m.createdAt
        )
    }
}

@Entity(tableName = "warga")
data class WargaEntity(
    @PrimaryKey val id: String,
    val nik: String,
    val name: String,
    val gender: String,
    val birthplace: String,
    val birth: String,
    val status: String,
    val kk: String,
    val phone: String,
    val job: String,
    val notes: String,
    val rtRole: String,
    val photo: String,
    val photoName: String,
    val docFile: String,
    val docFileName: String
) {
    fun toDomain() = Warga(
        id = id,
        nik = nik,
        name = name,
        gender = gender,
        birthplace = birthplace,
        birth = birth,
        status = status,
        kk = kk,
        phone = phone,
        job = job,
        notes = notes,
        rtRole = rtRole,
        photo = photo,
        photoName = photoName,
        docFile = docFile,
        docFileName = docFileName
    )

    companion object {
        fun fromDomain(m: Warga) = WargaEntity(
            id = m.id,
            nik = m.nik,
            name = m.name,
            gender = m.gender,
            birthplace = m.birthplace,
            birth = m.birth,
            status = m.status,
            kk = m.kk,
            phone = m.phone,
            job = m.job,
            notes = m.notes,
            rtRole = m.rtRole,
            photo = m.photo,
            photoName = m.photoName,
            docFile = m.docFile,
            docFileName = m.docFileName
        )
    }
}

@Entity(tableName = "pendukung_kk")
data class PendukungEntity(
    @PrimaryKey val id: String,
    val kk: String,
    val house: String,
    val phone: String,
    val houseStatus: String,
    val aid: String,
    val water: String,
    val electricity: String,
    val asset: String,
    val notes: String,
    val aidDocFile: String,
    val aidDocName: String,
    val houseDocFile: String,
    val houseDocName: String,
    val extraDocFile: String,
    val extraDocName: String
) {
    fun toDomain() = PendukungKK(
        id = id,
        kk = kk,
        house = house,
        phone = phone,
        houseStatus = houseStatus,
        aid = aid,
        water = water,
        electricity = electricity,
        asset = asset,
        notes = notes,
        aidDocFile = aidDocFile,
        aidDocName = aidDocName,
        houseDocFile = houseDocFile,
        houseDocName = houseDocName,
        extraDocFile = extraDocFile,
        extraDocName = extraDocName
    )

    companion object {
        fun fromDomain(m: PendukungKK) = PendukungEntity(
            id = m.id,
            kk = m.kk,
            house = m.house,
            phone = m.phone,
            houseStatus = m.houseStatus,
            aid = m.aid,
            water = m.water,
            electricity = m.electricity,
            asset = m.asset,
            notes = m.notes,
            aidDocFile = m.aidDocFile,
            aidDocName = m.aidDocName,
            houseDocFile = m.houseDocFile,
            houseDocName = m.houseDocName,
            extraDocFile = m.extraDocFile,
            extraDocName = m.extraDocName
        )
    }
}

@Entity(tableName = "surat_rt")
data class SuratEntity(
    @PrimaryKey val id: String,
    val type: String,
    val letterNumber: String,
    val title: String,
    val recipient: String,
    val wargaNik: String,
    val wargaName: String,
    val purpose: String,
    val content: String,
    val date: String,
    val createdAt: Long
) {
    fun toDomain() = SuratRT(
        id = id,
        type = type,
        letterNumber = letterNumber,
        title = title,
        recipient = recipient,
        wargaNik = wargaNik,
        wargaName = wargaName,
        purpose = purpose,
        content = content,
        date = date,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(m: SuratRT) = SuratEntity(
            id = m.id,
            type = m.type,
            letterNumber = m.letterNumber,
            title = m.title,
            recipient = m.recipient,
            wargaNik = m.wargaNik,
            wargaName = m.wargaName,
            purpose = m.purpose,
            content = m.content,
            date = m.date,
            createdAt = m.createdAt
        )
    }
}

@Entity(tableName = "rt_settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val title: String,
    val subtitle: String,
    val address: String,
    val contact: String,
    val footer: String,
    val paperSize: String,
    val dateFormat: String,
    val driveFolderName: String
) {
    fun toDomain() = RTSettings(
        title = title,
        subtitle = subtitle,
        address = address,
        contact = contact,
        footer = footer,
        paperSize = paperSize,
        dateFormat = dateFormat,
        driveFolderName = driveFolderName
    )

    companion object {
        fun fromDomain(m: RTSettings) = SettingsEntity(
            id = 1,
            title = m.title,
            subtitle = m.subtitle,
            address = m.address,
            contact = m.contact,
            footer = m.footer,
            paperSize = m.paperSize,
            dateFormat = m.dateFormat,
            driveFolderName = m.driveFolderName
        )
    }
}

@Dao
interface RTDao {
    @Query("SELECT * FROM kartu_keluarga ORDER BY kepala ASC")
    fun getAllKK(): Flow<List<KKEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKK(vararg items: KKEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKKList(items: List<KKEntity>)

    @Query("DELETE FROM kartu_keluarga WHERE id = :id")
    suspend fun deleteKKById(id: String)

    @Query("SELECT * FROM warga ORDER BY name ASC")
    fun getAllWarga(): Flow<List<WargaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarga(vararg items: WargaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWargaList(items: List<WargaEntity>)

    @Query("DELETE FROM warga WHERE id = :id")
    suspend fun deleteWargaById(id: String)

    @Query("SELECT * FROM pendukung_kk")
    fun getAllPendukung(): Flow<List<PendukungEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendukung(vararg items: PendukungEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendukungList(items: List<PendukungEntity>)

    @Query("DELETE FROM pendukung_kk WHERE id = :id")
    suspend fun deletePendukungById(id: String)

    @Query("SELECT * FROM surat_rt ORDER BY createdAt DESC")
    fun getAllSurat(): Flow<List<SuratEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurat(vararg items: SuratEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuratList(items: List<SuratEntity>)

    @Query("DELETE FROM surat_rt WHERE id = :id")
    suspend fun deleteSuratById(id: String)

    @Query("SELECT * FROM rt_settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<SettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(entity: SettingsEntity)

    @Query("SELECT COUNT(*) FROM kartu_keluarga")
    suspend fun getKKCount(): Int
}

@Database(
    entities = [
        KKEntity::class,
        WargaEntity::class,
        PendukungEntity::class,
        SuratEntity::class,
        SettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rtDao(): RTDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "rt_warga_database.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
