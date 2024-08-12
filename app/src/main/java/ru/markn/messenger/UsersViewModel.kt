package ru.markn.messenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UsersViewModel : ViewModel() {
    private val auth = Firebase.auth
    private val database = Firebase.database
    private val usersReference = database.getReference("Users")
    private val currentUser: MutableLiveData<FirebaseUser> = MutableLiveData()
    private val users: MutableLiveData<MutableList<User>> = MutableLiveData()
    private val error: MutableLiveData<String> = MutableLiveData()

    init {
        auth.addAuthStateListener { currentUser.value = it.currentUser }
        usersReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (currentUser.value == null) {
                    return
                }
                val listUser = mutableListOf<User>()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue<User>()
                    user?.let {
                        if (currentUser.value?.uid != it.id) {
                            listUser.add(it)
                        }
                    }
                }
                users.value = listUser
            }
            override fun onCancelled(databaseError: DatabaseError) {
                error.value = databaseError.message
            }
        })
    }

    fun setUserOnline(isOnline: Boolean) {
        auth.uid?.let {
            usersReference
                .child(it)
                .child("online")
                .setValue(isOnline)
        }
    }

    fun getCurrentUser(): LiveData<FirebaseUser> = currentUser
    fun getUsers(): LiveData<MutableList<User>> = users
    fun getError(): LiveData<String> = error
    fun logout(){
        setUserOnline(false)
        auth.signOut()
    }
}