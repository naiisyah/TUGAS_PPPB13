package com.example.tugas_pppb13

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.tugas_pppb13.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val channelId = "MAIN_NOTIFICATION"
    private val notifId = 100
    private val updateUIReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Perbarui UI berdasarkan SharedPreferences
            val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val likeCounter = sharedPreferences.getInt("likeCounter", 0)
            val dislikeCounter = sharedPreferences.getInt("dislikeCounter", 0)
            binding.tvLikeCounter.text = likeCounter.toString()
            binding.tvDislikeCounter.text = dislikeCounter.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Membuat Notification Channel
        val notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Main Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notifManager.createNotificationChannel(channel)
        }

        // Tombol untuk memunculkan notifikasi
        binding.btnNotification.setOnClickListener {
            showNotification(notifManager)
        }

        // Tombol Like di halaman utama
        binding.btnLike.setOnClickListener {
            updateLikeCounter(1000)
        }

        // Tombol Dislike di halaman utama
        binding.btnDislike.setOnClickListener {
            updateDislikeCounter(-100)
        }

        // Daftarkan BroadcastReceiver lokal untuk memperbarui UI
        val intentFilter = IntentFilter("UPDATE_UI_ACTION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(updateUIReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(updateUIReceiver, intentFilter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Batalkan pendaftaran receiver lokal
        unregisterReceiver(updateUIReceiver)
    }

    private fun showNotification(notifManager: NotificationManager) {
        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }

        // Intent untuk tombol Like
        val likeIntent = Intent("LIKE_ACTION")
        val likePendingIntent = PendingIntent.getBroadcast(this, 0, likeIntent, flag)

        // Intent untuk tombol Dislike
        val dislikeIntent = Intent("DISLIKE_ACTION")
        val dislikePendingIntent = PendingIntent.getBroadcast(this, 1, dislikeIntent, flag)

        val notifImage = BitmapFactory.decodeResource(resources, R.drawable.img)

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Penilaian Anda")
            .setContentText("Pilih Like atau Dislike")
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(notifImage))
            .addAction(R.drawable.ic_like, "Like", likePendingIntent)
            .addAction(R.drawable.ic_dislike, "Dislike", dislikePendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notifManager.notify(notifId, builder.build())
    }

    private fun updateLikeCounter(amount: Int) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentLike = sharedPreferences.getInt("likeCounter", 0)
        sharedPreferences.edit().putInt("likeCounter", currentLike + amount).apply()
        binding.tvLikeCounter.text = (currentLike + amount).toString()
    }

    private fun updateDislikeCounter(amount: Int) {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val currentDislike = sharedPreferences.getInt("dislikeCounter", 0)
        sharedPreferences.edit().putInt("dislikeCounter", currentDislike + amount).apply()
        binding.tvDislikeCounter.text = (currentDislike + amount).toString()
    }
}
