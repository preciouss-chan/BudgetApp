package com.example.budgetapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.budgetapp.ui.theme.BudgetAppTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetAppTheme {
                if (!isNotificationServiceEnabled()) {
                    // Direct user to enable the Notification Listener Service
                    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
                BudgetApp() // Render the app
            }
        }
    }


    private fun isNotificationServiceEnabled(): Boolean {
        val packageNames = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return !TextUtils.isEmpty(packageNames) && packageNames.contains(packageName)
    }


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) } // Add Bottom Navigation
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "transactions", // Default screen
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("transactions") { BudgetList() } // Transactions screen
            composable("visualizations") { MonthlySpendingScreen() } // Visualizations screen
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BudgetAppTheme {
        BudgetList()
    }
}