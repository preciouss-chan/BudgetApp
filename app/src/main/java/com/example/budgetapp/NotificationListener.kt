package com.example.budgetapp

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener: NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        handleNotifications()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        handleNotifications() // Update when new notifications come in
    }

    private fun handleNotifications() {
        val activeNotifications = getActiveNotifications()

        if (!activeNotifications.isNullOrEmpty()) {
            for (notification in activeNotifications) {
                if (notification.packageName == "com.mfoundry.mb.android.mb_731") {
                    val description = notification.notification.tickerText?.toString();
                    var price = 0.0;
                    if (description != null) {
                        for (i in description.indices) {
                            if (description[i] == '$') {
                                for (j in i until description.length) {}
                                price = description.substring(i + 1).toDouble();
                                Log.d("NotificationService", "Price: $price")
                            }
                        }
                    }

//                    BudgetViewModel.addTransaction(price, description)
                }
            }
        }
    }
}
