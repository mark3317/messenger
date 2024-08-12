package ru.markn.messenger

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import ru.markn.messenger.databinding.UserItemBinding

class UsersAdapter(
    private val context: Context
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root)

    var users = mutableListOf<User>()
        @SuppressLint("NotifyDataSetChanged")
        set(users) {
            field = users
            notifyDataSetChanged()
        }
    var onUserClickListener: OnUserClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        val userInfo = "${user.name} ${user.lastName}, ${user.age}"
        holder.binding.tvUserInfo.text = userInfo
        val bgResId = if (user.online) {
            R.drawable.circle_green
        } else {
            R.drawable.circle_red
        }
        val background = ContextCompat.getDrawable(holder.itemView.context, bgResId)
        holder.binding.viewOnlineStatus.background = background
        holder.itemView.setOnClickListener{ onUserClickListener?.onUserClick(user.id) }
    }

    override fun getItemCount(): Int = users.size

    fun interface OnUserClickListener {
        fun onUserClick(userId: String)
    }
}