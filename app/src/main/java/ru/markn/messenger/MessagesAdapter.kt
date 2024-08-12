package ru.markn.messenger

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import ru.markn.messenger.databinding.MyMessageItemBinding
import ru.markn.messenger.databinding.OtherMessageItemBinding

class MessagesAdapter(
    private val context: Context,
    private val currentUserId: String
) : RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 100
        private const val VIEW_TYPE_OTHER_MESSAGE = 101
    }

    class MessagesViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    var messages = mutableListOf<Message>()
        @SuppressLint("NotifyDataSetChanged")
        set(messages) {
            field = messages
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val binding = if (viewType == VIEW_TYPE_MY_MESSAGE) {
            MyMessageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            OtherMessageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        }
        return MessagesViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        val message = messages[position]
        with(holder.binding) {
            if (this is MyMessageItemBinding) {
                this.tvMessage.text = message.text
            } else if (this is OtherMessageItemBinding) {
                this.tvMessage.text = message.text
            }
        }
    }
}