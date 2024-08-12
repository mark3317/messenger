package ru.markn.messenger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.markn.messenger.databinding.ActivityUsersBinding

class UsersActivity : AppCompatActivity() {
    private val usersAdapter: UsersAdapter = UsersAdapter(this)
    private lateinit var binding: ActivityUsersBinding
    private lateinit var viewModel: UsersViewModel

    companion object {
        private const val EXTRA_CURRENT_USER_ID = "current_id"

        fun newIntent(context: Context, currentUserId: String): Intent {
            val intent = Intent(context, UsersActivity::class.java)
            intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.root.adapter = usersAdapter
        viewModel = ViewModelProvider(this)[UsersViewModel::class.java]
        observeViewModel()
        usersAdapter.onUserClickListener = UsersAdapter.OnUserClickListener { otherUserId ->
            val currentUserId = intent.getStringExtra(EXTRA_CURRENT_USER_ID)
            currentUserId?.let {
                val intent = ChatActivity.newIntent(this, it, otherUserId)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setUserOnline(true)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setUserOnline(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_logout) {
            viewModel.logout()
        }
        return super.onOptionsItemSelected(item)
    }



    private fun observeViewModel() {
        viewModel.getCurrentUser().observe(this) {
            if (it == null) {
                startActivity(LoginActivity.newIntent(this))
                finish()
            }
        }
        viewModel.getUsers().observe(this) { usersAdapter.users = it }
        viewModel.getError().observe(this) {Toast.makeText(this, it, Toast.LENGTH_LONG).show()}
    }
}