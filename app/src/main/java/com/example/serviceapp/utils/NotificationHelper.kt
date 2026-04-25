package com.example.serviceapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.serviceapp.MainActivity
import com.example.serviceapp.R

object NotificationHelper {

    private const val CHANNEL_ID   = "mistri_chai_channel"
    private const val CHANNEL_NAME = "মিস্ত্রি চাই"
    private var appContext: Context? = null
    private var notificationId      = 1

    fun init(context: Context) {
        appContext = context.applicationContext
        createChannel()
    }

    private fun createChannel() {
        val ctx = appContext ?: return
        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "সেবা অনুরোধের আপডেট"
            enableVibration(true)
        }
        manager.createNotificationChannel(channel)
    }

    fun showProviderFoundNotification(providerName: String, serviceType: String, baseFee: Double, rating: Double) {
        val ctx = appContext ?: return
        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            ctx, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("মিস্ত্রি পাওয়া গেছে! 🔧")
            .setContentText("$providerName আপনার $serviceType অনুরোধ নিতে আগ্রহী")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$providerName আপনার $serviceType অনুরোধ নিতে আগ্রহী।\n⭐ রেটিং: ${"%.1f".format(rating)} | 💰 বেস ফি: ৳${baseFee.toInt()}\nঅ্যাপ খুলুন এবং সিদ্ধান্ত নিন।")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(notificationId++, notification)
    }

    fun showRequestCancelledNotification() {
        val ctx = appContext ?: return
        val manager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("কোনো মিস্ত্রি পাওয়া যায়নি 😔")
            .setContentText("আপনার ফিল্টার পরিবর্তন করে আবার চেষ্টা করুন।")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        manager.notify(notificationId++, notification)
    }
}
