package ru.markn.messenger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ru.markn.messenger.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var viewModel: ResetPasswordViewModel

    companion object {
        private const val EXTRA_EMAIL = "email"

        fun newIntent(context: Context, email: String): Intent {
            val intent = Intent(context, ResetPasswordActivity::class.java)
            intent.putExtra(EXTRA_EMAIL, email)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[ResetPasswordViewModel::class.java]

        val email = intent.getStringExtra(EXTRA_EMAIL)
        binding.etResetEmail.setText(email)

        observeViewModel()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnResetPassword.setOnClickListener {
            val email = binding.etResetEmail.text.toString().trim()
            viewModel.resetPassword(email)
        }
    }

    private fun observeViewModel() {
        viewModel.isSuccess().observe(this) {
            if (it){
                Toast.makeText(this, getString(R.string.reset_link_sent), Toast.LENGTH_LONG).show()
            }
        }
        viewModel.getError().observe(this) {
            if (it != null) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}