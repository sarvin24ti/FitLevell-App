package com.example.sarvin_tia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sarvin_tia.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener {
            val emailInput = binding.etUsername.text.toString().trim()
            val passwordInput = binding.etPassword.text.toString().trim()

            // Validasi jika kosong
            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Email dan Kata Sandi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Pengecekan Username/Email dan Password
            if ((emailInput.equals("Sarvin24ti@mahasiswa.pcr.ac.id", ignoreCase = true) ||
                        emailInput.equals("sarvin", ignoreCase = true)) &&
                passwordInput == "123456"
            ) {
                // 1. Simpan status isLogin = true ke SharedPreferences
                val sharedPref = getSharedPreferences("FitLevelPref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putBoolean("isLogin", true)
                editor.putString("username", "Sarvin")
                editor.putString("email", emailInput)
                editor.apply()

                Toast.makeText(this, "Autentikasi Berhasil! Memuat data...", Toast.LENGTH_SHORT).show()

                // 2. Arahkan ke SplashActivity dulu agar muncul animasi Loading sebelum masuk ke MainActivity!
                val intent = Intent(this, SplashActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Email atau Kata Sandi salah! (Gunakan: sarvin / 123456)", Toast.LENGTH_LONG).show()
            }
        }
    }
}