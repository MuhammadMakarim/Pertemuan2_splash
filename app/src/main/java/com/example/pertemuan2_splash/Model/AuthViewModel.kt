package com.example.pertemuan2_splash.model


import android.app.Application
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pertemuan2_splash.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://papb-e6157-default-rtdb.asia-southeast1.firebasedatabase.app/")

    internal val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _nim = MutableLiveData<String>()
    val nim: LiveData<String> = _nim

    private val _profileImageUri = MutableLiveData<Uri?>()
    val profileImageUri: LiveData<Uri?> = _profileImageUri

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    init {
        checkAuthState()
        configureGoogleSignIn()
    }

    private fun configureGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(getApplication<Application>())
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getApplication<Application>().getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }

    fun beginGoogleSignIn(onSuccess: (IntentSender) -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("AuthViewModel", "Starting Google Sign-In")
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                Log.d("AuthViewModel", "Google Sign-In success, launching intent")
                onSuccess(result.pendingIntent.intentSender)
            }
            .addOnFailureListener { e ->
                Log.e("AuthViewModel", "Google Sign-In failed: ${e.message}")
                onFailure(e)
            }
    }

    fun handleGoogleSignInResult(data: Intent) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                Log.d("AuthViewModel", "Google ID Token obtained")
                firebaseAuthWithGoogle(idToken)
            } else {
                Log.e("AuthViewModel", "Google sign in failed: No ID token!")
                _authState.value = AuthState.Error("Google sign in failed: No ID token!")
            }
        } catch (e: ApiException) {
            Log.e("AuthViewModel", "Google sign in failed: ${e.message}")
            _authState.value = AuthState.Error("Google sign in failed: ${e.message}")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AuthViewModel", "Firebase authentication with Google successful")
                    _authState.value = AuthState.Authenticated
                    fetchUserProfile()
                } else {
                    Log.e("AuthViewModel", "Firebase authentication with Google failed: ${task.exception?.message}")
                    _authState.value = AuthState.Error(task.exception?.message ?: "Google sign in failed")
                }
            }
    }
    // Existing methods...

    fun checkAuthState() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
            fetchUserProfile()
        }
    }



    fun login(identifier: String, password: String) {
        if (identifier.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Username or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
            // Jika input adalah email
            loginWithEmail(identifier, password)
        } else {
            // Jika input adalah username, cari email yang sesuai
            firestore.collection("users").whereEqualTo("username", identifier).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val email = documents.documents[0].getString("email") ?: ""
                        loginWithEmail(email, password)
                    } else {
                        _authState.value = AuthState.Error("Username not found")
                    }
                }
                .addOnFailureListener {
                    _authState.value = AuthState.Error("Failed to search username: ${it.message}")
                }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                    fetchUserProfile()
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    _username.value = document.getString("username") ?: "User"
                    _nim.value = document.getString("nim") ?: ""
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (profileImageUrl != null) {
                        _profileImageUri.value = Uri.parse(profileImageUrl)
                    }
                }
                .addOnFailureListener {
                    _username.value = "User"
                }
        }
    }

    fun updateProfile(newUsername: String, newNim: String) {
        val userId = auth.currentUser?.uid ?: return
        val userUpdates = mapOf(
            "username" to newUsername,
            "nim" to newNim
        )
        firestore.collection("users").document(userId).update(userUpdates)
            .addOnSuccessListener {
                _username.value = newUsername
                _nim.value = newNim
                // Optionally show a success message to the user
            }
            .addOnFailureListener {
                // Handle any errors here
            }
    }

    fun uploadProfileImage(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val storageRef = storage.reference.child("profile_images/$userId.jpg")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    _profileImageUri.value = downloadUri
                    firestore.collection("users").document(userId)
                        .update("profileImageUrl", downloadUri.toString())
                        .addOnSuccessListener {
                            // Optionally show a success message to the user
                        }
                        .addOnFailureListener {
                            // Handle any errors here
                        }
                }
            }
            .addOnFailureListener {
                // Handle any errors here
            }
    }

    private fun isUsernameTaken(username: String, callback: (Boolean) -> Unit) {
        firestore.collection("users").whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    private fun isEmailTaken(email: String, callback: (Boolean) -> Unit) {
        firestore.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun signupRD(username: String, email: String, password: String) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }
        _authState.value = AuthState.Loading

        isEmailTaken(email) { taken ->
            if (taken) {
                _authState.value = AuthState.Error("Email is already registered")
                return@isEmailTaken
            }
            isUsernameTaken(username) { usernameTaken ->
                if (usernameTaken) {
                    _authState.value = AuthState.Error("Username is already taken")
                    return@isUsernameTaken
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            userId?.let {
                                // Simpan username ke Realtime Database
                                val userData = hashMapOf("username" to username, "email" to email)
                                database.reference.child("users").child(it).setValue(userData)
                                    .addOnSuccessListener {
                                        _authState.value = AuthState.Authenticated
                                    }
                                    .addOnFailureListener { e ->
                                        _authState.value = AuthState.Error("Failed to save user data: ${e.message}")
                                    }
                            }
                            fetchUserProfile()
                        } else {
                            _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                        }
                    }
            }
        }
    }

    fun signup(username: String, email: String, password: String) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            _authState.value = AuthState.Error("All fields are required")
            return
        }
        _authState.value = AuthState.Loading

        isEmailTaken(email) { taken ->
            if (taken) {
                _authState.value = AuthState.Error("Email is already registered")
                return@isEmailTaken
            }
            isUsernameTaken(username) { usernameTaken ->
                if (usernameTaken) {
                    _authState.value = AuthState.Error("Username is already taken")
                    return@isUsernameTaken
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            userId?.let {
                                val userData = hashMapOf("username" to username, "email" to email)
                                firestore.collection("users").document(it).set(userData)
                                    .addOnSuccessListener {
                                        _authState.value = AuthState.Authenticated
                                    }
                                    .addOnFailureListener { e ->
                                        _authState.value = AuthState.Error("Failed to save user data in firestore: ${e.message}")
                                    }
                            }
                            userId?.let {
                                // Simpan username ke Realtime Database
                                val userData = mapOf(
                                    "username" to username,
                                    "email" to email
                                )
                                database.reference.child("users").child(it).setValue(userData)
                                    .addOnSuccessListener {
                                        _authState.value = AuthState.Authenticated
                                    }
                                    .addOnFailureListener { e ->
                                        _authState.value = AuthState.Error("Failed to save user data: ${e.message}")
                                    }
                            }
                            fetchUserProfile()
                        } else {
                            _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                        }
                    }
            }
        }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}