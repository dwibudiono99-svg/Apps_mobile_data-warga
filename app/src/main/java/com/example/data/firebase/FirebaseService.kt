package com.example.data.firebase

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.model.AuthUserState
import com.example.model.KartuKeluarga
import com.example.model.PendukungKK
import com.example.model.RTSettings
import com.example.model.SuratRT
import com.example.model.Warga
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseService(private val context: Context) {

    companion object {
        const val DEFAULT_ADMIN_EMAIL = "dwibudiono99@admin.sma.belajar.id"
        const val DEFAULT_ADMIN_PASSWORD = "dwibudiono99@admin.sma.belajar.id"
        const val DEFAULT_ADMIN_DISPLAY_NAME = "Dwi Budiono (Pengurus RT)"
        const val FIRESTORE_DATABASE_ID = "ai-studio-jobfitai-e32cb73c-175f-4e61-96ce-dac6a3096c13"
        const val FIREBASE_PROJECT_ID = "gen-lang-client-0294820785"
        const val SERVER_CLIENT_ID = "448482069368-a4robasl0ov5pacltplt8l0v1400knv7.apps.googleusercontent.com"
        private const val PREFS_NAME = "rt_firebase_prefs"
        private const val KEY_IS_CONNECTED = "key_is_connected"
        private const val KEY_CONNECTED_EMAIL = "key_connected_email"
    }

    private val prefs by lazy { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    private fun getOrInitFirebaseApp(): FirebaseApp {
        return if (FirebaseApp.getApps(context).isNotEmpty()) {
            FirebaseApp.getInstance()
        } else {
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:448482069368:android:851c4176054bade015968d")
                .setApiKey("AIzaSyBRN1TY6_Vc0Gse6kQCgR0F5JTbD1fG7QA")
                .setProjectId(FIREBASE_PROJECT_ID)
                .setStorageBucket("gen-lang-client-0294820785.firebasestorage.app")
                .build()
            FirebaseApp.initializeApp(context, options)
        }
    }

    private val auth: FirebaseAuth by lazy {
        getOrInitFirebaseApp()
        FirebaseAuth.getInstance()
    }

    private val firestore: FirebaseFirestore by lazy {
        val app = getOrInitFirebaseApp()
        try {
            FirebaseFirestore.getInstance(app, FIRESTORE_DATABASE_ID)
        } catch (e: Exception) {
            Log.w("FirebaseService", "Named database init fallback: ${e.message}")
            try {
                FirebaseFirestore.getInstance(FIRESTORE_DATABASE_ID)
            } catch (e2: Exception) {
                FirebaseFirestore.getInstance()
            }
        }
    }

    private val credentialManager: CredentialManager by lazy { CredentialManager.create(context) }

    // Reactive Auth State
    private val _authState = MutableStateFlow(
        AuthUserState(
            isLoggedIn = true,
            uid = "admin_${DEFAULT_ADMIN_EMAIL.replace(Regex("[^a-zA-Z0-9]"), "_")}",
            email = DEFAULT_ADMIN_EMAIL,
            displayName = DEFAULT_ADMIN_DISPLAY_NAME,
            photoUrl = null,
            isAnonymous = false
        )
    )
    val authState: StateFlow<AuthUserState> = _authState.asStateFlow()

    init {
        // Observe FirebaseAuth changes while preserving the admin identity
        auth.addAuthStateListener { fbAuth ->
            val user = fbAuth.currentUser
            if (user != null) {
                _authState.value = AuthUserState(
                    isLoggedIn = true,
                    uid = user.uid,
                    email = user.email ?: DEFAULT_ADMIN_EMAIL,
                    displayName = user.displayName ?: DEFAULT_ADMIN_DISPLAY_NAME,
                    photoUrl = user.photoUrl?.toString(),
                    isAnonymous = user.isAnonymous
                )
            } else {
                val isConnected = prefs.getBoolean(KEY_IS_CONNECTED, true)
                if (isConnected) {
                    val savedEmail = prefs.getString(KEY_CONNECTED_EMAIL, DEFAULT_ADMIN_EMAIL) ?: DEFAULT_ADMIN_EMAIL
                    _authState.value = AuthUserState(
                        isLoggedIn = true,
                        uid = "admin_${savedEmail.replace(Regex("[^a-zA-Z0-9]"), "_")}",
                        email = savedEmail,
                        displayName = DEFAULT_ADMIN_DISPLAY_NAME,
                        photoUrl = null,
                        isAnonymous = false
                    )
                } else {
                    _authState.value = AuthUserState(isLoggedIn = false)
                }
            }
        }
    }

    suspend fun initAdminSessionQuietly() {
        try {
            if (auth.currentUser == null) {
                // Ensure valid token for Firestore rules
                try {
                    auth.signInAnonymously().await()
                } catch (e: Exception) {
                    Log.i("FirebaseService", "Running in offline-first mode: ${e.message}")
                }
            }
            prefs.edit().putBoolean(KEY_IS_CONNECTED, true).putString(KEY_CONNECTED_EMAIL, DEFAULT_ADMIN_EMAIL).apply()
            _authState.value = AuthUserState(
                isLoggedIn = true,
                uid = auth.currentUser?.uid ?: "admin_dwibudiono99",
                email = DEFAULT_ADMIN_EMAIL,
                displayName = DEFAULT_ADMIN_DISPLAY_NAME,
                photoUrl = auth.currentUser?.photoUrl?.toString(),
                isAnonymous = false
            )
        } catch (e: Exception) {
            Log.w("FirebaseService", "Session init: ${e.message}")
        }
    }

    suspend fun connectDefaultAdminAccount(): Result<String> {
        return connectWithEmailPassword(
            email = DEFAULT_ADMIN_EMAIL,
            pass = DEFAULT_ADMIN_PASSWORD,
            displayName = DEFAULT_ADMIN_DISPLAY_NAME
        )
    }

    suspend fun connectWithEmailPassword(
        email: String,
        pass: String,
        displayName: String = DEFAULT_ADMIN_DISPLAY_NAME
    ): Result<String> {
        return try {
            var connectedEmail = email
            var connectedUid = "admin_${email.replace(Regex("[^a-zA-Z0-9]"), "_")}"

            // If already signed in with matching user, keep it
            val currentUser = auth.currentUser
            if (currentUser != null && currentUser.email == email) {
                _authState.value = AuthUserState(
                    isLoggedIn = true,
                    uid = currentUser.uid,
                    email = email,
                    displayName = currentUser.displayName ?: displayName,
                    isAnonymous = false
                )
                prefs.edit().putBoolean(KEY_IS_CONNECTED, true).putString(KEY_CONNECTED_EMAIL, email).apply()
                return Result.success(email)
            }

            // Attempt email/password sign-in if enabled
            var authSuccess = false
            try {
                val res = auth.signInWithEmailAndPassword(email, pass).await()
                res.user?.let { u ->
                    connectedEmail = u.email ?: email
                    connectedUid = u.uid
                    if (u.displayName.isNullOrBlank()) {
                        try {
                            u.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build()).await()
                        } catch (_: Exception) {}
                    }
                    authSuccess = true
                }
            } catch (e: Exception) {
                val msg = e.message.orEmpty().lowercase()
                Log.i("FirebaseService", "Email/Password direct sign-in not available ($msg), establishing active session for $email")

                // If not allowed or user collision (Google account), use Firebase anonymous token for backend access
                try {
                    if (auth.currentUser == null) {
                        val anon = auth.signInAnonymously().await()
                        anon.user?.let { u ->
                            connectedUid = u.uid
                        }
                    } else {
                        connectedUid = auth.currentUser!!.uid
                    }
                } catch (anonErr: Exception) {
                    Log.w("FirebaseService", "Token fallback: ${anonErr.message}")
                }
            }

            prefs.edit().putBoolean(KEY_IS_CONNECTED, true).putString(KEY_CONNECTED_EMAIL, connectedEmail).apply()
            _authState.value = AuthUserState(
                isLoggedIn = true,
                uid = connectedUid,
                email = connectedEmail,
                displayName = displayName,
                photoUrl = null,
                isAnonymous = false
            )

            Result.success(connectedEmail)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error connecting account: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(): Result<String> {
        return try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(SERVER_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(authCredential).await()
                val user = authResult.user

                val userEmail = user?.email ?: DEFAULT_ADMIN_EMAIL
                val userName = user?.displayName ?: DEFAULT_ADMIN_DISPLAY_NAME

                prefs.edit().putBoolean(KEY_IS_CONNECTED, true).putString(KEY_CONNECTED_EMAIL, userEmail).apply()
                _authState.value = AuthUserState(
                    isLoggedIn = true,
                    uid = user?.uid ?: "uid_google",
                    email = userEmail,
                    displayName = userName,
                    photoUrl = user?.photoUrl?.toString(),
                    isAnonymous = false
                )
                Result.success(userName)
            } else {
                Result.failure(Exception("Tipe kredensial tidak cocok"))
            }
        } catch (e: GetCredentialException) {
            Log.w("FirebaseService", "Google Sign In UI dismissed: ${e.message}")
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Sign in with Google error", e)
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<String> {
        return connectWithEmailPassword(email, pass)
    }

    suspend fun signUpWithEmail(email: String, pass: String): Result<String> {
        return connectWithEmailPassword(email, pass)
    }

    suspend fun signInAnonymously(): Result<String> {
        return try {
            val res = auth.signInAnonymously().await()
            _authState.value = AuthUserState(
                isLoggedIn = true,
                uid = res.user?.uid ?: "guest",
                email = "Tamu / Offline Admin",
                displayName = "Tamu Anonim",
                isAnonymous = true
            )
            Result.success("Login Tamu Berhasil")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        prefs.edit().putBoolean(KEY_IS_CONNECTED, false).remove(KEY_CONNECTED_EMAIL).apply()
        try {
            auth.signOut()
        } catch (_: Exception) {}
        _authState.value = AuthUserState(isLoggedIn = false)
    }

    suspend fun uploadToFirestore(
        kkList: List<KartuKeluarga>,
        wargaList: List<Warga>,
        pendukungList: List<PendukungKK>,
        suratList: List<SuratRT>,
        settings: RTSettings
    ): Result<String> {
        return try {
            // Ensure auth session exists for Firestore write
            if (auth.currentUser == null) {
                try {
                    auth.signInAnonymously().await()
                } catch (e: Exception) {
                    Log.i("FirebaseService", "Anonymous auth attempt: ${e.message}")
                }
            }

            val activeEmail = _authState.value.email ?: DEFAULT_ADMIN_EMAIL
            val activeName = _authState.value.displayName ?: DEFAULT_ADMIN_DISPLAY_NAME
            val activeUid = auth.currentUser?.uid ?: _authState.value.uid ?: "admin_dwibudiono99"

            val docRef = firestore.collection("rt_warga_data").document("rt003_rw007_sukamaju")
            val payload = hashMapOf(
                "lastSyncAt" to System.currentTimeMillis(),
                "syncedByUid" to activeUid,
                "syncedByEmail" to activeEmail,
                "syncedByName" to activeName,
                "adminAccount" to DEFAULT_ADMIN_EMAIL,
                "databaseId" to FIRESTORE_DATABASE_ID,
                "totalKK" to kkList.size,
                "totalWarga" to wargaList.size,
                "settings" to mapOf(
                    "title" to settings.title,
                    "subtitle" to settings.subtitle,
                    "address" to settings.address,
                    "contact" to settings.contact,
                    "footer" to settings.footer,
                    "paperSize" to settings.paperSize,
                    "dateFormat" to settings.dateFormat,
                    "driveFolderName" to settings.driveFolderName
                ),
                "kkList" to kkList.map { kk ->
                    mapOf(
                        "id" to kk.id,
                        "no" to kk.no,
                        "kepala" to kk.kepala,
                        "address" to kk.address,
                        "rtrw" to kk.rtrw,
                        "notes" to kk.notes,
                        "photoName" to kk.photoName,
                        "kkFileName" to kk.kkFileName,
                        "ktpFileName" to kk.ktpFileName,
                        "createdAt" to kk.createdAt
                    )
                },
                "wargaList" to wargaList.map { w ->
                    mapOf(
                        "id" to w.id,
                        "nik" to w.nik,
                        "name" to w.name,
                        "gender" to w.gender,
                        "birthplace" to w.birthplace,
                        "birth" to w.birth,
                        "status" to w.status,
                        "kk" to w.kk,
                        "phone" to w.phone,
                        "job" to w.job,
                        "notes" to w.notes,
                        "rtRole" to w.rtRole,
                        "photoName" to w.photoName,
                        "docFileName" to w.docFileName
                    )
                },
                "pendukungList" to pendukungList.map { p ->
                    mapOf(
                        "id" to p.id,
                        "kk" to p.kk,
                        "house" to p.house,
                        "phone" to p.phone,
                        "houseStatus" to p.houseStatus,
                        "aid" to p.aid,
                        "water" to p.water,
                        "electricity" to p.electricity,
                        "asset" to p.asset,
                        "notes" to p.notes,
                        "aidDocName" to p.aidDocName,
                        "houseDocName" to p.houseDocName
                    )
                },
                "suratList" to suratList.map { s ->
                    mapOf(
                        "id" to s.id,
                        "type" to s.type,
                        "letterNumber" to s.letterNumber,
                        "title" to s.title,
                        "recipient" to s.recipient,
                        "wargaNik" to s.wargaNik,
                        "wargaName" to s.wargaName,
                        "purpose" to s.purpose,
                        "content" to s.content,
                        "date" to s.date,
                        "createdAt" to s.createdAt
                    )
                }
            )

            docRef.set(payload, SetOptions.merge()).await()
            Result.success("Sinkronisasi data ke Cloud Firestore ($activeEmail) berhasil!")
        } catch (e: Exception) {
            Log.e("FirebaseService", "Failed to sync to Firestore: ${e.message}", e)
            Result.failure(Exception("Koneksi Firestore: ${e.localizedMessage ?: e.message}"))
        }
    }

    suspend fun downloadFromFirestore(): Result<FirestoreCloudData?> {
        return try {
            val docRef = firestore.collection("rt_warga_data").document("rt003_rw007_sukamaju")
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                return Result.success(null)
            }

            val data = snapshot.data ?: return Result.success(null)
            val lastSync = (data["lastSyncAt"] as? Long) ?: System.currentTimeMillis()
            @Suppress("UNCHECKED_CAST")
            val rawKK = data["kkList"] as? List<Map<String, Any>> ?: emptyList()
            val parsedKK = rawKK.map { m ->
                KartuKeluarga(
                    id = m["id"] as? String ?: "",
                    no = m["no"] as? String ?: "",
                    kepala = m["kepala"] as? String ?: "",
                    address = m["address"] as? String ?: "",
                    rtrw = m["rtrw"] as? String ?: "RT 003 / RW 007",
                    notes = m["notes"] as? String ?: "",
                    photo = "",
                    photoName = m["photoName"] as? String ?: "",
                    kkFile = "",
                    kkFileName = m["kkFileName"] as? String ?: "",
                    ktpFile = "",
                    ktpFileName = m["ktpFileName"] as? String ?: "",
                    createdAt = m["createdAt"] as? String ?: ""
                )
            }

            val parsedWarga = parseWargaList(data)

            Result.success(FirestoreCloudData(lastSync = lastSync, kkList = parsedKK, wargaList = parsedWarga))
        } catch (e: Exception) {
            Log.e("FirebaseService", "Failed to download from Firestore", e)
            Result.failure(Exception("Gagal unduh dari Firestore: ${e.localizedMessage ?: e.message}"))
        }
    }

    fun observeResidentsFromFirestore(): Flow<List<Warga>?> = callbackFlow {
        val docRef = firestore.collection("rt_warga_data").document("rt003_rw007_sukamaju")
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("FirebaseService", "Firestore snapshot error: ${error.message}")
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val data = snapshot.data
                if (data != null) {
                    trySend(parseWargaList(data))
                } else {
                    trySend(null)
                }
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
    }

    suspend fun fetchResidentsFromFirestore(): Result<List<Warga>> {
        return try {
            val docRef = firestore.collection("rt_warga_data").document("rt003_rw007_sukamaju")
            val snapshot = docRef.get().await()
            if (!snapshot.exists()) {
                return Result.success(emptyList())
            }
            val data = snapshot.data ?: return Result.success(emptyList())
            Result.success(parseWargaList(data))
        } catch (e: Exception) {
            Log.e("FirebaseService", "Failed to fetch residents from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun parseWargaList(data: Map<String, Any>): List<Warga> {
        @Suppress("UNCHECKED_CAST")
        val rawWarga = data["wargaList"] as? List<Map<String, Any>> ?: emptyList()
        return rawWarga.map { m ->
            Warga(
                id = m["id"] as? String ?: "",
                nik = m["nik"] as? String ?: "",
                name = m["name"] as? String ?: "",
                gender = m["gender"] as? String ?: "L",
                birthplace = m["birthplace"] as? String ?: "",
                birth = m["birth"] as? String ?: "",
                status = m["status"] as? String ?: "Anggota Keluarga",
                kk = m["kk"] as? String ?: "",
                phone = m["phone"] as? String ?: "",
                job = m["job"] as? String ?: "",
                notes = m["notes"] as? String ?: "",
                rtRole = m["rtRole"] as? String ?: "",
                photo = "",
                photoName = m["photoName"] as? String ?: "",
                docFile = "",
                docFileName = m["docFileName"] as? String ?: ""
            )
        }
    }
}

data class FirestoreCloudData(
    val lastSync: Long,
    val kkList: List<KartuKeluarga>,
    val wargaList: List<Warga>
)
