package ru.markn.messenger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatViewModel(
    private val currentUserId: String,
    otherUserId: String
) : ViewModel() {
    private val database = Firebase.database
    private val usersReference = database.getReference("Users")
    private val messagesReference = database.getReference("Messages")
    private val messages = MutableLiveData<MutableList<Message>>()
    private val otherUser = MutableLiveData<User>()
    private val messageSent = MutableLiveData<Boolean>()
    private val error = MutableLiveData<String>()

    init {
        usersReference
            .child(otherUserId)
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue<User>()
                otherUser.value = user
            }

            override fun onCancelled(databaseError: DatabaseError) {
                error.value = databaseError.message
            }
        })

        messagesReference
            .child(currentUserId)
            .child(otherUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val listMessage = mutableListOf<Message>()
                    for (snapshot in dataSnapshot.children) {
                        val message = snapshot.getValue<Message>()
                        if (message != null) {
                            listMessage.add(message)
                        }
                    }
                    messages.value = listMessage
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    error.value = databaseError.message
                }
            })
    }

    fun setUserOnline(isOnline: Boolean) {
        usersReference
            .child(currentUserId)
            .child("online")
            .setValue(isOnline)
    }

    fun sendMessage(message: Message) {
        messagesReference
            .child(message.senderId)
            .child(message.receiverId)
            .push().setValue(message)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    messagesReference
                        .child(message.receiverId)
                        .child(message.senderId)
                        .push().setValue(message)
                        .addOnCompleteListener {refTask ->
                            if (refTask.isSuccessful) {
                                messageSent.value = true
                            } else {
                                error.value = task.exception?.message
                            }
                        }
                } else {
                    error.value = task.exception?.message
                }
            }
    }

    fun getMessages(): LiveData<MutableList<Message>> = messages
    fun getOtherUser(): LiveData<User> = otherUser
    fun getMessageSent(): LiveData<Boolean> = messageSent
    fun getError(): LiveData<String> = error
}