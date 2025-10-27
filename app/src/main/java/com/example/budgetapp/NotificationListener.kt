package com.example.budgetapp

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationListener : NotificationListenerService() {
    private val processedNotifications = mutableSetOf<String>()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onListenerConnected() {
        super.onListenerConnected()
        handleNotifications(getActiveNotifications())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let { handleNotifications(arrayOf(it)) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleNotifications(notifications: Array<StatusBarNotification>?) {
        if (notifications.isNullOrEmpty()) {
            Log.d("NotificationService", "No notifications to process.")
            return
        }

        for (sbn in notifications) {
            if (shouldProcess(sbn)) {
                val extras = sbn.notification.extras
                val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
                val text = extras.getString(Notification.EXTRA_TEXT) ?: ""

                val transactionDetails = "$title $text"
                val price = parsePrice(transactionDetails)

                if (price > 0) {
                    saveTransaction(price, transactionDetails)
                    processedNotifications.add(sbn.key)
                } else {
                    Log.d("NotificationService", "Skipped notification with invalid data: $transactionDetails")
                }
            } else {
                Log.d("NotificationService", "Skipped notification from package: ${sbn.packageName}")
            }
        }
    }

    private fun shouldProcess(sbn: StatusBarNotification): Boolean {
        return (sbn.packageName == "com.discoverfinancial.mobile" || sbn.packageName == "com.mfoundry.mb.android.mb_731") &&
                !processedNotifications.contains(sbn.key) &&
                sbn.notification.flags.and(Notification.FLAG_GROUP_SUMMARY).isZero()
    }

    private fun parsePrice(details: String): Double {
        val dollarIndex = details.indexOf('$')
        return if (dollarIndex != -1) {
            details.substring(dollarIndex + 1)
                .takeWhile { it.isDigit() || it == '.' }
                .toDoubleOrNull() ?: 0.0
        } else 0.0
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTransaction(price: Double, description: String) {
        coroutineScope.launch {
            val context = applicationContext
            val currentTransactions = SharedPreferencesHelper.getTransactions(context)
            val newTransaction = Transaction(
                id = currentTransactions.size + 1,
                amount = price,
                description = description,
                date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            )
            SharedPreferencesHelper.saveTransactions(context, currentTransactions + newTransaction)
            Log.d("NotificationService", "Transaction added: $newTransaction")
        }
    }

    private fun Int.isZero(): Boolean = this == 0
}
