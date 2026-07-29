package com.vael.aipet

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val OVERLAY_PERMISSION_REQUEST = 1001
        private const val USAGE_STATS_REQUEST = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_start).setOnClickListener {
            checkAndStart()
        }

        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            stopService(Intent(this, OverlayService::class.java))
        }
    }

    private fun checkAndStart() {
        // Check overlay permission
        if (!Settings.canDrawOverlays(this)) {
            AlertDialog.Builder(this)
                .setTitle("需要悬浮窗权限")
                .setMessage("小宝需要悬浮在其他应用上层显示")
                .setPositiveButton("去设置") { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    )
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST)
                }
                .show()
            return
        }

        // Check notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= 33) {
            if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                AlertDialog.Builder(this)
                    .setTitle("需要通知权限")
                    .setMessage("小宝需要通过通知栏保持后台运行")
                    .setPositiveButton("去设置") { _, _ ->
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        }
                        startActivity(intent)
                    }
                    .show()
                return
            }
        }

        startService(Intent(this, OverlayService::class.java))
        Toast.makeText(this, "小宝启动啦 🐱", Toast.LENGTH_SHORT).show()
    }
}
