package ru.markn.messenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class RegistrationViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val usersReference = database.getReference("Users")
    private val error: MutableLiveData<String> = MutableLiveData()
    private val user: MutableLiveData<FirebaseUser> = MutableLiveData()

    init {
        auth.addAuthStateListener { user.value = it.currentUser }
    }

    fun getError(): LiveData<String> = error
    fun getUser(): LiveData<FirebaseUser> = user

    fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        age: String
    ) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || lastName.isEmpty() || age.isEmpty()) {
            error.value = "Not all fields are filled in."
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result.user
                    firebaseUser?.let {
                        val user = User(
                            it.uid,
                            name,
                            lastName,
                            age.toInt(),
                            false)
                        usersReference.child(it.uid).setValue(user)
                    }

                } else {
                    error.value = task.exception?.message
                }
            }
    }
}