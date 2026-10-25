package com.example.sarvin_tia

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sarvin_tia.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 45000 // 45 detik sesuai Figma
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Terima data dari Intent (ListView Latihan)
        val namaGerakan = intent.getStringExtra("EXTRA_NAMA")
        val durasiDetik = intent.getIntExtra("EXTRA_DURASI", 45)
        val gambarGerakan = intent.getIntExtra("EXTRA_GAMBAR", 0)
        if (namaGerakan != null) {
            binding.tvJudulGerakan.text = namaGerakan
            timeLeftInMillis = durasiDetik * 1000L
            binding.tvTimer.text = String.format("%02d:%02d", durasiDetik / 60, durasiDetik % 60)
            if (gambarGerakan != 0) binding.imgGerakan.setImageResource(gambarGerakan)
        }

        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarDetail.setNavigationOnClickListener { finish() }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Mulai timer otomatis saat halaman dibuka
        startTimer()

        binding.btnPauseResume.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.btnFinishWorkout.setOnClickListener {
            countDownTimer?.cancel()
            Toast.makeText(this, "Latihan Selesai! Kamu mendapatkan +50 XP ⚡", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                val seconds = (timeLeftInMillis / 1000).toInt()
                binding.tvTimer.text = String.format("%02d:%02d", seconds / 60, seconds % 60)
            }

            override fun onFinish() {
                isTimerRunning = false
                binding.tvTimer.text = "00:00"
                Toast.makeText(this@DetailActivity, "Selamat! Target tercapai (+50 XP)", Toast.LENGTH_LONG).show()
                finish()
            }
        }.start()

        isTimerRunning = true
        binding.btnPauseResume.text = "Jeda latihan  ⏸"
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        binding.btnPauseResume.text = "Lanjutkan latihan  ▶"
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}