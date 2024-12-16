package com.example.budgetapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.budgetapp.ui.theme.BudgetAppTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                if (!isNotificationServiceEnabled()) {
                    // Direct user to enable the Notification Listener Service
                    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
                BudgetList()
            }
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val packageNames = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return !TextUtils.isEmpty(packageNames) && packageNames.contains(packageName)
    }

    override fun onResume() {
        super.onResume()

        // Obtain the ViewModel using ViewModelProvider
        val viewModel: BudgetViewModel = ViewModelProvider(this)[BudgetViewModel::class.java]

        // Reload transactions
        viewModel.reloadTransactions(this)
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