package ru.markn.messenger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import ru.markn.messenger.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var viewModel: ChatViewModel
    private lateinit var viewModelFactory: ChatViewModelFactory
    private lateinit var binding: ActivityChatBinding
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var currentUserId: String
    private lateinit var otherUserId: String

    companion object {
        private const val EXTRA_CURRENT_USER_ID = "current_id"
        private const val EXTRA_OTHER_USER_ID = "other_id"

        fun newIntent(context: Context, currentUserId: String, otherUserId: String): Intent {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId)
            intent.putExtra(EXTRA_OTHER_USER_ID, otherUserId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentUserId = intent.getStringExtra(EXTRA_CURRENT_USER_ID).toString()
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID).toString()

        viewModelFactory = ChatViewModelFactory(currentUserId, otherUserId)
        viewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
        messagesAdapter = MessagesAdapter(this, currentUserId)
        binding.rvMessages.adapter = messagesAdapter

        observeViewModel()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setUserOnline(true)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setUserOnline(false)
    }

    private fun setupClickListeners() {
        binding.ivSendMessage.setOnClickListener {
            val textMessage = binding.etMessage.text.toString().trim()
            if (textMessage.isNotEmpty()) {
                val message = Message(
                    textMessage,
                    currentUserId,
                    otherUserId
                )
                viewModel.sendMessage(message)
            }
        }
        binding.rvMessages.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom && messagesAdapter.itemCount != 0) {
                binding.rvMessages.smoothScrollToPosition(messagesAdapter.itemCount - 1)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.getMessages().observe(this) { messages ->
            messagesAdapter.messages = messages
            if (messagesAdapter.itemCount != 0) {
                binding.rvMessages.smoothScrollToPosition(messagesAdapter.itemCount - 1)
            }
        }
        viewModel.getError().observe(this) { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        viewModel.getMessageSent().observe(this) { sent ->
            if (sent) {
                binding.etMessage.setText("")
            }
        }
        viewModel.getOtherUser().observe(this) {
            val title = "${it.name} ${it.lastName}, ${it.age}"
            binding.tvTitleChat.text = title
            val bgResId = if (it.online) {
                R.drawable.circle_green
            } else {
                R.drawable.circle_red
            }
            val background = ContextCompat.getDrawable(this, bgResId)
            binding.viewOnlineStatus.background = background
        }
    }
}