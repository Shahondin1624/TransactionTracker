package com.github.shahondin1624

import com.github.shahondin1624.input.parseCSV
import com.github.shahondin1624.model.MetaInformation
import com.github.shahondin1624.model.Transaction
import com.github.shahondin1624.model.YearlyTransactions
import com.github.shahondin1624.output.exportToExcelFile
import java.time.ZoneId
import java.util.*

fun createDTO(firstName: String, lastName: String, startDate: Date, endDate: Date) =
    MetaInformation(firstName, lastName, Constants.outgoingIBAN, startDate, endDate)

fun generate(
    inputFilePath: String,
    outputFilePath: String,
    metaInformation: MetaInformation,
    previousAmount: Double,
    onError: (Exception) -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    try {
        val filter: ((Transaction) -> Boolean)? = {
            val startDate = metaInformation.startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val endDate = metaInformation.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val transactionDate = it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            val include =
                !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate) && it.amount.amount != 0.0
            println("$it will ${if (!include) "NOT" else ""} be included in the report.")
            include
        }
        println("Created filter for date-range: ${metaInformation.startDate} - ${metaInformation.endDate}")
        val transactions = parseCSV(inputFilePath, filter = filter)
        println("Found ${transactions.size} transactions")
        val metaInformationWithIBAN = metaInformation.copy(iban = Constants.outgoingIBAN)
        println("Updated meta information: $metaInformationWithIBAN")
        val yearlyTransactions = YearlyTransactions(metaInformationWithIBAN, previousAmount, transactions)
        exportToExcelFile(yearlyTransactions, outputFilePath)
        onSuccess()
    } catch (e: Exception) {
        e.printStackTrace()
        onError(e)
    }
}

fun String.clean() = this.replace("\"", "").trim()