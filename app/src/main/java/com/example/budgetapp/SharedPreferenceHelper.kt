package com.example.budgetapp

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesHelper {

    private const val PREFS_NAME = "budget_prefs"
    private const val TRANSACTIONS_KEY = "transactions"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveTransactions(context: Context, transactions: List<Transaction>) {
        val json = Gson().toJson(transactions)
        getPreferences(context).edit().putString(TRANSACTIONS_KEY, json).apply()
    }

    fun getTransactions(context: Context): List<Transaction> {
        val json = getPreferences(context).getString(TRANSACTIONS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
