package com.example.pertemuan2_splash.model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UserViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _nim = MutableLiveData<String>()
    val nim: LiveData<String> = _nim

    private val _profileImageUri = MutableLiveData<Uri?>()
    val profileImageUri: LiveData<Uri?> = _profileImageUri

    init {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _authState.value = AuthState.Authenticated
            fetchUserProfile()
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    private fun fetchUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _username.value = document.getString("username") ?: ""
                    _nim.value = document.getString("nim") ?: ""
                    val profileImageUrl = document.getString("profileImageUrl")
                    if (profileImageUrl != null) {
                        _profileImageUri.value = Uri.parse(profileImageUrl)
                    }
                }
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

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }


}