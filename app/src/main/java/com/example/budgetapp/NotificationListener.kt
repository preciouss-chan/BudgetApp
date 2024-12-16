package com.example.budgetapp

import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class NotificationListener : NotificationListenerService() {
    private val processedNotifications = mutableSetOf<String>() // To track processed notifications

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onListenerConnected() {
        super.onListenerConnected()
        handleNotifications()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        handleNotifications() // Update when new notifications come in
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleNotifications() {
        val activeNotifications = getActiveNotifications()

        if (!activeNotifications.isNullOrEmpty()) {
            for (notification in activeNotifications) {
                if ((notification.packageName == "com.discoverfinancial.mobile" || notification.packageName == "com.mfoundry.mb.android.mb_731") && !processedNotifications.contains(notification.key)) {
                    val description = notification.notification.tickerText?.toString()
                    var price = 0.0

                    if (!description.isNullOrEmpty()) {
                        val dollarIndex = description.indexOf('$')
                        if (dollarIndex != -1) {
                            // Extract text after the dollar sign
                            val priceText = description.substring(dollarIndex + 1)
                                .takeWhile { it.isDigit() || it == '.' }
                            price = priceText.toDoubleOrNull() ?: 0.0
                            Log.d("NotificationService", "Parsed price: $price")
                        }
                    }

                    if (!description.isNullOrEmpty() && price > 0) {
                        // Save the transaction directly using SharedPreferences
                        val context = applicationContext
                        val currentTransactions = SharedPreferencesHelper.getTransactions(context)
                        val newTransaction = Transaction(
                            id = currentTransactions.size + 1,
                            amount = price,
                            description = description,
                            date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"))
                        )
                        SharedPreferencesHelper.saveTransactions(context, currentTransactions + newTransaction)
                        Log.d("NotificationService", "Transaction added: $newTransaction")

                        // Mark this notification as processed
                        processedNotifications.add(notification.key)
                    } else {
                        Log.d("NotificationService", "Skipped notification with invalid data: $description")
                    }
                } else {
                    Log.d("NotificationService", "Duplicate notification skipped: ${notification.key}")
                }
            }
        } else {
            Log.d("NotificationService", "No active notifications to process.")
        }
    }
}


