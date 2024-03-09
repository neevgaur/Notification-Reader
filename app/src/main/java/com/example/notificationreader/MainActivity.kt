package com.example.notificationreader

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.notificationreader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if notification access is granted
        checkForPermission()
    }

    private fun checkForPermission() {
        if (!isNotificationAccessGranted()) {
            // If not granted, prompt the user to grant permission
            promptNotificationAccess()
        } else {
            // Notification access is granted, start the service
            startNotificationService()
        }
    }

    /**
     * Checks whether the notification access is granted to the application.
     * @return Boolean value indicating whether notification access is granted.
     */
    private fun isNotificationAccessGranted(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(this)
            .contains(packageName)
    }

    /**
     * Prompts the user to grant notification access to the application.
     * This function opens the system settings page for enabling notification access.
     */
    private fun promptNotificationAccess() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }

    /**
     * Starts the notification service.
     * This function initializes and starts the service responsible for reading notifications.
     */
    private fun startNotificationService() {
        val componentName = ComponentName(this, NotificationReader::class.java)
        val intent = Intent()
        intent.component = componentName
        startService(intent)
    }

}