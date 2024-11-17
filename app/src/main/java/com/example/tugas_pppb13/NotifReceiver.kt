package com.example.tugas_pppb13

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotifReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val sharedPreferences = it.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            when (intent?.action) {
                "LIKE_ACTION" -> {
                    val currentLike = sharedPreferences.getInt("likeCounter", 0)
                    editor.putInt("likeCounter", currentLike + 1000).apply()
                    Toast.makeText(context, "Liked! +1000", Toast.LENGTH_SHORT).show()
                }
                "DISLIKE_ACTION" -> {
                    val currentDislike = sharedPreferences.getInt("dislikeCounter", 0)
                    editor.putInt("dislikeCounter", currentDislike - 100).apply()
                    Toast.makeText(context, "Disliked! -100", Toast.LENGTH_SHORT).show()
                }
            }

            // Kirim broadcast untuk memperbarui UI di MainActivity
            val updateIntent = Intent("UPDATE_UI_ACTION")
            context.sendBroadcast(updateIntent)
        }
    }
}
