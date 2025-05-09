package com.github.shahondin1624.model

import java.util.*

data class MetaInformation(val firstName: String, val lastName: String, val iban: String, val startDate: Date, val endDate: Date)

data class Transaction(val number: Int, val date: Date, val description: String, val amount: MonetaryAmount)

interface MonetaryAmount {
    val amount: Double
}

data class Income(override val amount: Double) : MonetaryAmount
data class Expense(override val amount: Double) : MonetaryAmount

data class YearlyTransactions(val metaInformation: MetaInformation, val previous: Double, val transactions: List<Transaction>)