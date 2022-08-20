package com.dk.notificationcenter

import android.R
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.app.NotificationCompat


class NotificationsService : Service() {

    private val mWindowManager: WindowManager =
        App.INSTANCE.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val layoutInflater: LayoutInflater =
        App.INSTANCE.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private lateinit var mHandler: Handler


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand()")
        mHandler = Handler(Looper.getMainLooper())
        val input = intent!!.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service")
            .setContentText(input)
            .setSmallIcon(R.drawable.sym_def_app_icon)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)

        popupMsgInLoop(5000)
        return START_NOT_STICKY
    }


    var count = 0

    private fun popupMsgInLoop(frequency: Long) {
        val run = object : Runnable {
            override fun run() {
                Log.i(TAG, "Counter:${count++}")
                popUp()
                mHandler.postDelayed(this, frequency)
            }
        }

        mHandler.post(run)
    }


    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )

        manager.createNotificationChannel(serviceChannel)
    }

    private fun popUp() {
        Log.i("dima", "Poped up")


        layoutInflater.inflate(com.dk.notificationcenter.R.layout.activity_popup, null)
            .let { popup ->
                popup.findViewById<ImageView>(com.dk.notificationcenter.R.id.closeBTN).setOnClickListener { mWindowManager.removeView(popup) }
                val p =
                    WindowManager.LayoutParams( // Shrink the window to wrap the content rather than filling the screen
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows, but only for the current user
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,  // Make the underlying application window visible through any transparent parts
                        PixelFormat.TRANSLUCENT
                    )



                mWindowManager.addView(popup, p)
            }


    }

    companion object {
        private const val TAG = "NotificationsService"
        const val CHANNEL_ID = "ForegroundServiceChannel"
    }


}