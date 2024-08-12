package ru.markn.messenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val error: MutableLiveData<String> = MutableLiveData()
    private val user: MutableLiveData<FirebaseUser> = MutableLiveData()

    init {
        auth.addAuthStateListener { user.value = it.currentUser }
    }

    fun getError(): LiveData<String> = error
    fun getUser(): LiveData<FirebaseUser> = user
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            error.value = "Not all fields are filled in."
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener { error.value = it.message }
    }
}