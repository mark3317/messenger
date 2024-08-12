package ru.markn.messenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val error: MutableLiveData<String> = MutableLiveData()
    private val success: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getError(): LiveData<String> = error
    fun isSuccess(): LiveData<Boolean> = success

    fun resetPassword(email: String) {
        if (email.isEmpty()) {
            error.value = "Fill in the email field."
            return
        }
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    success.value = true
                } else {
                    error.value = task.exception?.message
                }
            }
    }
}