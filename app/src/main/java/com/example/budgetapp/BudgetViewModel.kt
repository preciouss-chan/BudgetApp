package com.example.budgetapp

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BudgetViewModel : ViewModel() {
    var transactions by mutableStateOf<List<Transaction>>(emptyList())
        private set

    // Load transactions asynchronously but update state on the main thread
    fun loadTransactions(context: Context) {
        viewModelScope.launch {
            val loadedTransactions = withContext(Dispatchers.IO) {
                SharedPreferencesHelper.getTransactions(context)
            }
            transactions = loadedTransactions
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTransaction(context: Context, amount: Double, description: String) {
        viewModelScope.launch {
            val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val newTransaction = Transaction(
                id = transactions.size + 1,
                amount = amount,
                description = description,
                date = currentDate
            )

            // Update state on main thread
            transactions = transactions + newTransaction

            // Save on IO thread
            withContext(Dispatchers.IO) {
                SharedPreferencesHelper.saveTransactions(context, transactions)
            }
        }
    }


    fun getMonthlySpending(): List<Pair<String, Double>> {
        if (transactions.isEmpty()) {Log.d("MonthlySpending", "No transactions available")}

        Log.d("MonthlySpending", "Transactions: $transactions")

        val monthlySpending = transactions.groupBy {
            it.date.substring(0, 7)
        }.map { (month, transactions) ->
            month to transactions.sumOf { it.amount }
        }

        return monthlySpending.sortedBy { it.first }
    }

}