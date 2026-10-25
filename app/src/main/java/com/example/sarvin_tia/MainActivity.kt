package com.example.sarvin_tia

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.sarvin_tia.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPref: SharedPreferences

    private val daftarTarget = listOf(
        "Menurunkan berat badan",
        "Membentuk otot",
        "Menjaga kebugaran",
        "Meningkatkan stamina"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Menerapkan Toolbar pada aplikasi
        setSupportActionBar(binding.toolbarMain)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ambil data username dari SharedPreferences
        sharedPref = getSharedPreferences("FitLevelPref", Context.MODE_PRIVATE)
        val savedUser = sharedPref.getString("username", "Sarvin")
        binding.tvGreeting.text = "Hai, $savedUser 👋"

        // Logika Interaktif Checklist Misi Harian (+50 XP tiap misi)
        val updateMissionProgress = {
            var completed = 0
            if (binding.cbMisi1.isChecked) completed++
            if (binding.cbMisi2.isChecked) completed++
            if (binding.cbMisi3.isChecked) completed++

            binding.tvMisiCounter.text = "$completed dari 3 selesai"
            val currentXp = 1190 + (completed * 50)
            binding.progressXp.progress = currentXp
            binding.tvXpText.text = "$currentXp / 1.500 XP"
        }

        binding.cbMisi1.setOnCheckedChangeListener { _, _ -> updateMissionProgress() }
        binding.cbMisi2.setOnCheckedChangeListener { _, _ -> updateMissionProgress() }
        binding.cbMisi3.setOnCheckedChangeListener { _, _ -> updateMissionProgress() }

        // ===== BOTTOM NAVIGATION (Beranda, Latihan, Profil) =====
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> showTab(1)
                R.id.nav_latihan -> showTab(2)
                R.id.nav_profil -> showTab(3)
            }
            true
        }

        // Tombol "Ayo berlatih" dari Beranda -> buka Tab Latihan
        binding.btnAyoBerlatih.setOnClickListener {
            binding.bottomNav.selectedItemId = R.id.nav_latihan
        }

        // Tombol "Mulai latihan" -> Pindah ke DetailActivity (Workout Timer)
        binding.btnMulaiTimer.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            startActivity(intent)
        }

        // Tombol Logout (Menghapus session SharedPreferences isLogin)
        binding.btnLogout.setOnClickListener {
            val editor = sharedPref.edit()
            editor.putBoolean("isLogin", false)
            editor.apply()

            Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // ===== FITUR TAMBAHAN =====
        setupListTips()
        setupListGerakan()
        setupFormProfil()
    }

    // ---------- Tab ----------
    private fun showTab(tabIndex: Int) {
        binding.layoutBeranda.visibility = if (tabIndex == 1) View.VISIBLE else View.GONE
        binding.layoutLatihan.visibility = if (tabIndex == 2) View.VISIBLE else View.GONE
        binding.layoutProfil.visibility = if (tabIndex == 3) View.VISIBLE else View.GONE

        supportActionBar?.title = when (tabIndex) {
            1 -> "FITLEVEL - Beranda"
            2 -> "FITLEVEL - Latihan"
            else -> "FITLEVEL - Perjalananmu"
        }
    }

    // ---------- Options Menu (Toolbar) ----------
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reset -> {
                binding.cbMisi1.isChecked = false
                binding.cbMisi2.isChecked = false
                binding.cbMisi3.isChecked = false
                Toast.makeText(this, "Misi harian direset", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_tentang -> {
                AlertDialog.Builder(this)
                    .setTitle("Tentang FITLEVEL")
                    .setMessage("Fitness Level Up\nGerak kecil. Raih prestasi.\n\nDibuat oleh Sarvin")
                    .setPositiveButton("Tutup", null)
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ---------- ListView 1: Simple List Item (ArrayAdapter) ----------
    private fun setupListTips() {
        val tips = listOf(
            "💧 Minum air putih sebelum dan sesudah latihan",
            "🔥 Lakukan pemanasan 5 menit",
            "😴 Tidur 7-8 jam agar otot pulih",
            "🥗 Perbanyak protein dan sayur",
            "🧘 Jangan lupa peregangan setelah latihan"
        )

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            tips
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                (v as TextView).setTextColor(ContextCompat.getColor(context, R.color.text_dark))
                v.textSize = 14f
                return v
            }
        }
        binding.lvTips.adapter = adapter
        setListViewHeight(binding.lvTips)

        binding.lvTips.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(this, tips[position], Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- ListView 2: Custom Adapter (BaseAdapter) ----------
    private fun setupListGerakan() {
        val data = GerakanData.list
        binding.lvGerakan.adapter = GerakanAdapter(this, data)
        setListViewHeight(binding.lvGerakan)

        binding.lvGerakan.setOnItemClickListener { _, _, position, _ ->
            val gerakan = data[position]
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("EXTRA_NAMA", gerakan.nama)
                putExtra("EXTRA_DURASI", gerakan.durasiDetik)
                putExtra("EXTRA_GAMBAR", gerakan.gambar)
            }
            startActivity(intent)
        }
    }

    // ListView di dalam ScrollView harus dihitung tingginya manual
    private fun setListViewHeight(listView: ListView) {
        val adapter = listView.adapter ?: return
        val density = resources.displayMetrics.density
        val widthPx = resources.displayMetrics.widthPixels - (40 * density).toInt()
        val widthSpec = View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY)

        var total = 0
        for (i in 0 until adapter.count) {
            val item = adapter.getView(i, null, listView)
            item.measure(widthSpec, View.MeasureSpec.UNSPECIFIED)
            total += item.measuredHeight
        }
        total += listView.dividerHeight * (adapter.count - 1).coerceAtLeast(0)

        val params = listView.layoutParams
        params.height = total
        listView.layoutParams = params
    }

    // ---------- Form Profil: EditText, Spinner, RadioGroup, SeekBar, Switch, Dialog, Snackbar ----------
    private fun setupFormProfil() {
        // Spinner
        binding.spTarget.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            daftarTarget
        )
        binding.spTarget.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {}
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        // SeekBar
        binding.sbMenit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvMenitValue.text = "$progress menit"
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        // Muat data tersimpan dari SharedPreferences
        binding.etNamaProfil.setText(sharedPref.getString("profil_nama", ""))
        binding.spTarget.setSelection(sharedPref.getInt("profil_target", 0))
        if (sharedPref.getString("profil_gender", "Pria") == "Wanita") {
            binding.rbWanita.isChecked = true
        } else {
            binding.rbPria.isChecked = true
        }
        binding.sbMenit.progress = sharedPref.getInt("profil_menit", 15)
        binding.tvMenitValue.text = "${binding.sbMenit.progress} menit"
        binding.swPengingat.isChecked = sharedPref.getBoolean("profil_pengingat", false)

        // Switch (dipasang setelah data dimuat agar Toast tidak muncul saat start)
        binding.swPengingat.setOnCheckedChangeListener { _, isChecked ->
            val pesan = if (isChecked) "Pengingat latihan aktif" else "Pengingat latihan nonaktif"
            Toast.makeText(this, pesan, Toast.LENGTH_SHORT).show()
        }

        // Tombol Simpan -> Validasi -> AlertDialog -> SharedPreferences -> Snackbar
        binding.btnSimpanProfil.setOnClickListener {
            val nama = binding.etNamaProfil.text.toString().trim()
            if (nama.isEmpty()) {
                binding.etNamaProfil.error = "Nama tidak boleh kosong"
                return@setOnClickListener
            }

            val gender = if (binding.rbPria.isChecked) "Pria" else "Wanita"
            val target = binding.spTarget.selectedItem.toString()
            val menit = binding.sbMenit.progress
            val pengingat = binding.swPengingat.isChecked

            val ringkasan = """
                Nama: $nama
                Gender: $gender
                Target: $target
                Durasi harian: $menit menit
                Pengingat: ${if (pengingat) "Aktif" else "Nonaktif"}
            """.trimIndent()

            AlertDialog.Builder(this)
                .setTitle("Konfirmasi data")
                .setMessage(ringkasan)
                .setPositiveButton("Simpan") { _, _ ->
                    sharedPref.edit()
                        .putString("profil_nama", nama)
                        .putString("profil_gender", gender)
                        .putInt("profil_target", binding.spTarget.selectedItemPosition)
                        .putInt("profil_menit", menit)
                        .putBoolean("profil_pengingat", pengingat)
                        .apply()
                    Snackbar.make(binding.main, "Pengaturan berhasil disimpan", Snackbar.LENGTH_SHORT).show()
                }
                .setNegativeButton("Batal", null)
                .show()
        }
    }
}