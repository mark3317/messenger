package ru.markn.messenger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.markn.messenger.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var viewModel: RegistrationViewModel

    companion object{
        fun newIntent(context: Context) = Intent(context, RegistrationActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[RegistrationViewModel::class.java]

        observeViewModel()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSingUp.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val age = binding.etAge.text.toString()
            viewModel.signUp(email, password, name, lastName, age)
        }
    }

    private fun observeViewModel() {
        viewModel.getUser().observe(this) {
            if (it != null) {
                startActivity(UsersActivity.newIntent(this, it.uid))
                finish()
            }
        }
        viewModel.getError().observe(this) {
            if (it != null) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}