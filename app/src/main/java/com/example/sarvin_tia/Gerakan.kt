package com.example.sarvin_tia

data class Gerakan(
    val nama: String,
    val detail: String,
    val durasiDetik: Int,
    val xp: Int,
    val gambar: Int
)

object GerakanData {
    val list: List<Gerakan> = listOf(
        Gerakan("Lompat bintang", "3 set • 30 dtk • Kardio", 30, 50, R.drawable.bintang),
        Gerakan("Jongkok tanpa beban", "3 set • 12 kali • Tubuh bawah", 45, 50, R.drawable.jongkok),
        Gerakan("Tekuk dorong", "3 set • 10 kali • Tubuh atas", 45, 50, R.drawable.tekuk),
        Gerakan("Tahan posisi papan", "3 set • 45 dtk • Otot inti", 45, 50, R.drawable.plank),
        Gerakan("Latihan otot", "4 set • 10 kali • Otot besar", 60, 60, R.drawable.muscles),
        Gerakan("Sesi gym", "3 set • 15 kali • Seluruh tubuh", 60, 70, R.drawable.gym)
    )
}