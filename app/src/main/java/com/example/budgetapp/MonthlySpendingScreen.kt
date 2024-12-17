package com.example.budgetapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MonthlySpendingScreen(viewModel: BudgetViewModel = viewModel()) {

    val monthlyData = viewModel.getMonthlySpending()

    val context = LocalContext.current
    viewModel.loadTransactions(context)

    if (monthlyData.isEmpty()) {
        Text(text = monthlyData.toString())
        Text(text = "No data available, your program sucks!")
    } else {


        Scaffold { innerPadding ->
            // Apply content padding here
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Monthly Spending", modifier = Modifier.padding(top = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(monthlyData) { (month, total) ->
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .height(150.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Month
                            Text(text = month)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Spending value
                            Text(text = "$${"%.2f".format(total)}")
                        }
                    }
                }
            }
        }
    }
}
