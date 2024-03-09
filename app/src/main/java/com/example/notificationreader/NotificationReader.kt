package com.example.notificationreader

import android.app.Notification
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.Locale

/*
File : 
Description : 

Author : Neev Gaur 

Todo >
*/

class NotificationReader : NotificationListenerService(), TextToSpeech.OnInitListener {


    private lateinit var textToSpeech: TextToSpeech

    private val stopSoundHandler = Handler()


    /**
     * This method is called when the service is initially created.
     * It initializes the TextToSpeech engine with the context of this service
     * and sets this service as the TextToSpeech.OnInitListener to handle initialization callbacks.
     */
    override fun onCreate() {
        super.onCreate()
        textToSpeech = TextToSpeech(this, this)
    }


    /**
     * Called when the activity is about to be destroyed.
     * This method stops the TextToSpeech engine and releases its resources,
     * ensuring proper cleanup and preventing memory leaks.
     */
    override fun onDestroy() {
        // Stop the TextToSpeech engine if it's currently speaking.
        textToSpeech.stop()

        // Shutdown the TextToSpeech engine, releasing its resources.
        textToSpeech.shutdown()

        // Call the superclass implementation of onDestroy().
        super.onDestroy()
    }


    /**
     * Method called when the TextToSpeech engine initialization is complete.
     * @param status The status of the TextToSpeech initialization.
     */
    override fun onInit(status: Int) {
        // Check if TextToSpeech initialization is successful
        if (status == TextToSpeech.SUCCESS) {
            // Set the language of the TextToSpeech engine to the default locale
            val result = textToSpeech.setLanguage(Locale.getDefault())
            // Check if setting the language was successful
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Show a toast message indicating that the language is not supported
                Toast.makeText(this,"Language is not supported", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Show a toast message indicating that Text-to-speech initialization failed
            Toast.makeText(this,"Text-to-speech initialization failed", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * This method is called when a notification is posted on the system.
     * It overrides the default behavior of the superclass.
     * @param sbn The StatusBarNotification object representing the notification.
     */
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Extract the notification from StatusBarNotification
        val notification = sbn.notification

        // Play a notification sound
        playNotificationSound()

        // Check if the notification is from WhatsApp or Instagram
        if (isWhatsAppNotification(sbn) || isInstagramNotification(sbn)) {
            // Speak out the content of the notification
            speakNotification(notification)

            Toast.makeText(this,"Notification from instagram or whatsapp", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        // Handle notification removal if needed
    }

    /**
     * Function to determine if a given notification is from WhatsApp.
     *
     * @param sbn The StatusBarNotification object representing the notification.
     * @return True if the notification is from WhatsApp, false otherwise.
     */
    private fun isWhatsAppNotification(sbn: StatusBarNotification): Boolean {
        return sbn.packageName == "com.whatsapp"
    }

    /**
     * Function to determine if a given notification is from Instagram.
     *
     * @param sbn The StatusBarNotification object representing the notification.
     * @return True if the notification is from Instagram, false otherwise.
     */
    private fun isInstagramNotification(sbn: StatusBarNotification): Boolean {
        return sbn.packageName == "com.instagram.android"
    }

    /**
     * Speaks the text content of a notification using TextToSpeech.
     *
     * @param notification The notification containing the text to be spoken.
     */
    private fun speakNotification(notification: Notification) {
        // Extract text content from the notification
        val text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)

        // If text content is available, convert and speak it using TextToSpeech
        text?.let {
            textToSpeech.speak(it.toString(), TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    /**
     * Plays the default notification sound associated with the system.
     */
    private fun playNotificationSound() {
        // Get the default notification sound URI
        val notificationSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        // Retrieve the Ringtone object for the notification sound
        val ringtone = RingtoneManager.getRingtone(applicationContext, notificationSoundUri)

        // Play the notification sound
        ringtone.play()

        // Schedule a task to stop the notification sound after 3 seconds
        stopSoundHandler.postDelayed({ ringtone?.stop() }, 3000)
    }


    companion object {
        private const val TAG = "NotificationReader"
    }
}
