package com.example.data.sample

import com.example.model.KartuKeluarga
import com.example.model.PendukungKK
import com.example.model.RTSettings
import com.example.model.SuratRT
import com.example.model.Warga

object DefaultData {
    val defaultSettings = RTSettings(
        title = "PENDATAAN WARGA RUKUN TETANGGA",
        subtitle = "RT 003 / RW 007 - Kelurahan Sukamaju",
        address = "Jl. Melati Raya No. 1, Sukamaju, Kec. Cilodong, Kota Depok",
        contact = "0812-9876-5432 (Ketua RT)",
        footer = "Sistem Informasi Pendataan Warga RT • Arsip Dokumen Offline Mandiri",
        paperSize = "A4",
        dateFormat = "dd-mmmm-yyyy",
        driveFolderName = "PENDATAAN WARGA RT"
    )

    val defaultKKList = listOf(
        KartuKeluarga(
            id = "kk-1",
            no = "3201012304900001",
            kepala = "Ahmad Suryanto",
            address = "Jl. Kenanga No. 12",
            rtrw = "RT 003 / RW 007",
            notes = "Warga tetap sejak tahun 2012, aktif kerja bakti dan ronda malam.",
            photo = "avatar_ahmad",
            photoName = "Foto_Ahmad_Suryanto.png",
            kkFile = "Scan_KK_Ahmad_Suryanto_3201012304900001.pdf",
            kkFileName = "Scan_KK_Ahmad_Suryanto_3201012304900001.pdf",
            ktpFile = "Scan_KTP_Ahmad_Suryanto_3201011205800001.png",
            ktpFileName = "Scan_KTP_Ahmad_Suryanto_3201011205800001.png",
            createdAt = "2026-01-15T08:30:00.000Z"
        ),
        KartuKeluarga(
            id = "kk-2",
            no = "3201011508920002",
            kepala = "Bambang Pamungkas",
            address = "Jl. Melati Blok B No. 04",
            rtrw = "RT 003 / RW 007",
            notes = "Usaha kuliner rumahan warung bakso berkah.",
            photo = "avatar_bambang",
            photoName = "Foto_Bambang_Pamungkas.png",
            kkFile = "Scan_KK_Bambang_Pamungkas_3201011508920002.pdf",
            kkFileName = "Scan_KK_Bambang_Pamungkas_3201011508920002.pdf",
            ktpFile = "Scan_KTP_Bambang_Pamungkas_3201012010880002.png",
            ktpFileName = "Scan_KTP_Bambang_Pamungkas_3201012010880002.png",
            createdAt = "2026-02-10T10:15:00.000Z"
        ),
        KartuKeluarga(
            id = "kk-3",
            no = "3201010703850003",
            kepala = "Siti Nurhaliza",
            address = "Jl. Melati Blok A No. 08",
            rtrw = "RT 003 / RW 007",
            notes = "Lansia terdaftar di Posyandu Lansia, hidup bersama anak.",
            photo = "avatar_siti_nur",
            photoName = "Foto_Siti_Nurhaliza.png",
            kkFile = "Scan_KK_Siti_Nurhaliza_3201010703850003.pdf",
            kkFileName = "Scan_KK_Siti_Nurhaliza_3201010703850003.pdf",
            ktpFile = "Scan_KTP_Siti_Nurhaliza_3201014506550003.png",
            ktpFileName = "Scan_KTP_Siti_Nurhaliza_3201014506550003.png",
            createdAt = "2026-03-01T14:20:00.000Z"
        )
    )

    val defaultWargaList = listOf(
        Warga(
            id = "w-1",
            nik = "3201011205800001",
            name = "Ahmad Suryanto",
            gender = "L",
            birthplace = "Depok",
            birth = "1980-05-12",
            status = "Kepala Keluarga",
            kk = "3201012304900001",
            phone = "081234567891",
            job = "Karyawan Swasta",
            notes = "Golongan darah O, Ketua RT periode 2024-2027",
            rtRole = "Ketua RT",
            photo = "avatar_ahmad",
            photoName = "Foto_Ahmad.png"
        ),
        Warga(
            id = "w-2",
            nik = "3201015508820002",
            name = "Siti Aminah",
            gender = "P",
            birthplace = "Bogor",
            birth = "1982-08-15",
            status = "Istri/Suami",
            kk = "3201012304900001",
            phone = "081234567892",
            job = "Ibu Rumah Tangga",
            notes = "Ketua Kader Posyandu & Penggerak PKK RT 03",
            rtRole = "Seksi Pemberdayaan Perempuan (PKK)",
            photo = "avatar_siti_aminah",
            photoName = "Foto_Siti_Aminah.png"
        ),
        Warga(
            id = "w-3",
            nik = "3201012401080003",
            name = "Rizky Suryanto",
            gender = "L",
            birthplace = "Depok",
            birth = "2008-01-24",
            status = "Anak",
            kk = "3201012304900001",
            phone = "081234567893",
            job = "Pelajar SMA",
            notes = "Siswa Kelas 12 SMA Negeri Cilodong",
            rtRole = ""
        ),
        Warga(
            id = "w-4",
            nik = "3201016503140004",
            name = "Nabila Suryanto",
            gender = "P",
            birthplace = "Depok",
            birth = "2014-03-25",
            status = "Anak",
            kk = "3201012304900001",
            phone = "",
            job = "Pelajar SD",
            notes = "Siswa Kelas 5 SD Sukamaju",
            rtRole = ""
        ),
        Warga(
            id = "w-5",
            nik = "3201012010880002",
            name = "Bambang Pamungkas",
            gender = "L",
            birthplace = "Solo",
            birth = "1988-10-20",
            status = "Kepala Keluarga",
            kk = "3201011508920002",
            phone = "085612345678",
            job = "Wiraswasta",
            notes = "Koordinator ronda pos kamling malam Kamis",
            rtRole = "Wakil Ketua RT",
            photo = "avatar_bambang",
            photoName = "Foto_Bambang.png"
        ),
        Warga(
            id = "w-6",
            nik = "3201014811900003",
            name = "Ratna Sari",
            gender = "P",
            birthplace = "Yogyakarta",
            birth = "1990-11-08",
            status = "Istri/Suami",
            kk = "3201011508920002",
            phone = "085612345679",
            job = "Pedagang",
            notes = "Mengelola kas iuran sampah dan keamanan RT",
            rtRole = "Bendahara RT",
            photo = "avatar_ratna",
            photoName = "Foto_Ratna.png"
        ),
        Warga(
            id = "w-7",
            nik = "3201011005180004",
            name = "Dimas Pamungkas",
            gender = "L",
            birthplace = "Depok",
            birth = "2018-05-10",
            status = "Anak",
            kk = "3201011508920002",
            phone = "",
            job = "Belum Bekerja",
            notes = "Pemeriksaan imunisasi balita lengkap di Posyandu Melati",
            rtRole = ""
        ),
        Warga(
            id = "w-8",
            nik = "3201014506550003",
            name = "Siti Nurhaliza",
            gender = "P",
            birthplace = "Cirebon",
            birth = "1955-06-05",
            status = "Kepala Keluarga",
            kk = "3201010703850003",
            phone = "087811223344",
            job = "Tidak Bekerja / Pensiun",
            notes = "Lansia usia 71 tahun, sesepuh lingkungan",
            rtRole = "Dewan Penasihat / Pembina RT",
            photo = "avatar_siti_nur",
            photoName = "Foto_Siti_Nur.png"
        ),
        Warga(
            id = "w-9",
            nik = "3201011809950004",
            name = "Farhan Maulana",
            gender = "L",
            birthplace = "Depok",
            birth = "1995-09-18",
            status = "Anggota Keluarga",
            kk = "3201010703850003",
            phone = "087899887766",
            job = "Buruh Harian Lepas",
            notes = "Koordinator turnamen bola voli pemuda RT",
            rtRole = "Seksi Pemuda & Olahraga (Karang Taruna)",
            photo = "avatar_farhan",
            photoName = "Foto_Farhan.png"
        ),
        Warga(
            id = "w-10",
            nik = "3201011503840005",
            name = "Budi Santoso",
            gender = "L",
            birthplace = "Jakarta",
            birth = "1984-03-15",
            status = "Kepala Keluarga",
            kk = "3201012304900001",
            phone = "081399881122",
            job = "PNS / Administrasi",
            notes = "Penanggung jawab arsip persuratan dan notulensi rapat warga",
            rtRole = "Sekretaris RT",
            photo = "avatar_budi",
            photoName = "Foto_Budi.png"
        ),
        Warga(
            id = "w-11",
            nik = "3201011208790006",
            name = "Hendra Wijaya",
            gender = "L",
            birthplace = "Bandung",
            birth = "1979-08-12",
            status = "Kepala Keluarga",
            kk = "3201011508920002",
            phone = "081288776655",
            job = "Security / Keamanan",
            notes = "Koordinator Pos Ronda & Linmas Lingkungan RT 03",
            rtRole = "Seksi Keamanan & Ketertiban",
            photo = "avatar_hendra",
            photoName = "Foto_Hendra.png"
        ),
        Warga(
            id = "w-12",
            nik = "3201012509830007",
            name = "Agus Supriyadi",
            gender = "L",
            birthplace = "Semarang",
            birth = "1983-09-25",
            status = "Kepala Keluarga",
            kk = "3201010703850003",
            phone = "085712334455",
            job = "Teknisi Bangunan",
            notes = "Penanggung jawab kebersihan saluran air dan lampu jalan",
            rtRole = "Seksi Kebersihan & Sarana Prasarana",
            photo = "avatar_agus",
            photoName = "Foto_Agus.png"
        )
    )

    val defaultPendukungList = listOf(
        PendukungKK(
            id = "p-1",
            kk = "3201012304900001",
            house = "No. 12",
            phone = "081234567891",
            houseStatus = "Milik Sendiri",
            aid = "PKH (Program Keluarga Harapan)",
            water = "PDAM & Sumur Bor",
            electricity = "1300 VA",
            asset = "1 Unit Sepeda Motor, Kulkas, TV",
            notes = "Komponen bantuan anak sekolah (SD & SMA). PBB lunas.",
            aidDocFile = "Kartu_PKH_Kemensos_3201012304900001.png",
            aidDocName = "Kartu_PKH_Kemensos_3201012304900001.png",
            houseDocFile = "Bukti_PBB_Rumah_No_12_3201012304900001.png",
            houseDocName = "Bukti_PBB_Rumah_No_12_3201012304900001.png"
        ),
        PendukungKK(
            id = "p-2",
            kk = "3201011508920002",
            house = "Blok B No. 04",
            phone = "085612345678",
            houseStatus = "Kontrak",
            aid = "Tidak Ada / Mandiri",
            water = "Air Tanah / Jetpump",
            electricity = "900 VA",
            asset = "Gerobak Usaha Bakso, 1 Sepeda Motor",
            notes = "Masa sewa rumah diperpanjang tahunan sampai Desember 2026.",
            aidDocFile = "",
            aidDocName = "",
            houseDocFile = "Surat_Perjanjian_Sewa_Rumah_Blok_B4.png",
            houseDocName = "Surat_Perjanjian_Sewa_Rumah_Blok_B4.png"
        ),
        PendukungKK(
            id = "p-3",
            kk = "3201010703850003",
            house = "Blok A No. 08",
            phone = "087811223344",
            houseStatus = "Menumpang",
            aid = "BPNT, KIS, Bantuan Lansia",
            water = "Sumur Gali",
            electricity = "450 VA",
            asset = "Peralatan Rumah Tangga Sederhana",
            notes = "Pemeriksaan tensi rutin posyandu lansia tiap bulan. Prioritas bansos sembako.",
            aidDocFile = "Surat_Keterangan_SKTM_BPNT_3201010703850003.png",
            aidDocName = "Surat_Keterangan_SKTM_BPNT_3201010703850003.png",
            houseDocFile = "",
            houseDocName = ""
        )
    )

    val defaultSuratList = listOf(
        SuratRT(
            id = "surat-1",
            type = "PENGANTAR",
            letterNumber = "045/RT.03-RW.07/SK/III/2026",
            title = "Surat Pengantar Pembuatan e-KTP",
            recipient = "Kantor Kelurahan Sukamaju",
            wargaNik = "3201012401080003",
            wargaName = "Rizky Suryanto",
            purpose = "Permohonan Perekaman dan Pencetakan e-KTP Pemula (Usia 17 Tahun)",
            content = "Menerangkan bahwa nama tersebut di atas adalah benar-benar warga warga kami yang berdomisili di RT 003 / RW 007 dan bermaksud mengurus perekaman KTP Elektronik.",
            date = "2026-03-10"
        ),
        SuratRT(
            id = "surat-2",
            type = "DOMISILI",
            letterNumber = "046/RT.03-RW.07/SKD/III/2026",
            title = "Surat Keterangan Domisili Tinggal",
            recipient = "Instansi / Bank Tempat Kerja",
            wargaNik = "3201012010880002",
            wargaName = "Bambang Pamungkas",
            purpose = "Keterangan Domisili Usaha dan Tempat Tinggal",
            content = "Menerangkan dengan sebenarnya bahwa yang bersangkutan berdomisili menetap di Jl. Melati Blok B No. 04 RT 003 / RW 007 Kelurahan Sukamaju.",
            date = "2026-03-15"
        ),
        SuratRT(
            id = "surat-3",
            type = "UNDANGAN",
            letterNumber = "047/RT.03-RW.07/UND/III/2026",
            title = "Undangan Musyawarah Warga & Kerja Bakti",
            recipient = "Seluruh Warga RT 003 / RW 007",
            wargaNik = "-",
            wargaName = "Warga RT 003 / RW 007",
            purpose = "Koordinasi Keamanan, Ronda Ramadhan, dan Kerja Bakti Saluran Lingkungan",
            content = "Mengharap kehadiran Bapak/Ibu/Saudara dalam rapat koordinasi rutin warga RT pada hari Sabtu malam Minggu bertempat di Balai Warga RT 03.",
            date = "2026-03-20"
        ),
        SuratRT(
            id = "surat-4",
            type = "SERTIJAB",
            letterNumber = "001/RT.03-RW.07/BA-SERTIJAB/2024",
            title = "Berita Acara Serah Terima Jabatan & Inventaris",
            recipient = "Pengurus RT Baru & RW 007",
            wargaNik = "3201011205800001",
            wargaName = "Ahmad Suryanto (Ketua RT Baru)",
            purpose = "Serah Terima Administrasi, Kas RT, dan Inventaris Fasilitas Lingkungan",
            content = "Telah dilaksanakan serah terima jabatan Ketua RT 003 RW 007 berikut saldo kas RT sebesar Rp 4.850.000,- serta inventaris berupa tenda warga, kursi 50 unit, dan sound system portable.",
            date = "2024-06-01"
        )
    )
}
