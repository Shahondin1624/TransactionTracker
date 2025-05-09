package com.github.shahondin1624.input

import com.github.shahondin1624.Constants
import com.github.shahondin1624.clean
import com.github.shahondin1624.model.Expense
import com.github.shahondin1624.model.Income
import com.github.shahondin1624.model.Transaction
import java.io.File
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

enum class RelevantColumns {
    BookingDay,
    Usage,
    Amount,
    OutgoingIBAN
}

fun parseCSV(filePath: String, filter: ((Transaction) -> Boolean)? = { true }): List<Transaction> {
    val transactions = mutableListOf<Transaction>()
    val file = File(filePath)
    var headerMap: Map<RelevantColumns, Int>? = null
    var index = 1

    file.bufferedReader().use { reader ->
        reader.lineSequence().forEachIndexed { lineNum, line ->
            val columns = line.split(";").map { it.trim() }

            if (lineNum == 0) {
                headerMap = extractHeaderInformation(columns)
            } else {
                val transaction = parseRow(index, columns, headerMap!!, filter)
                transaction?.let { tx ->
                    transactions.add(tx)
                    index++
                }
            }
        }
    }

    return transactions
}


private fun setOutgoingIBAN(columns: List<String>, headerMap: Map<RelevantColumns, Int>) {
    if (Constants.outgoingIBAN.isNotBlank()) return
    Constants.outgoingIBAN = columns[headerMap[RelevantColumns.OutgoingIBAN]!!].clean()
}

private fun parseRow(
    index: Int,
    columns: List<String>,
    headerMap: Map<RelevantColumns, Int>,
    filter: ((Transaction) -> Boolean)? = { true }
): Transaction? {
    setOutgoingIBAN(columns, headerMap)

    val bookingDayStr = columns[headerMap[RelevantColumns.BookingDay]!!].clean()
    val usage = columns[headerMap[RelevantColumns.Usage]!!].clean()
    val amountStr = columns[headerMap[RelevantColumns.Amount]!!].replace(",", ".").clean()

    val bookingDay = try {
        val (day, month, year) = bookingDayStr.clean().split(".")
        LocalDate.of(2000 + year.toInt(), month.toInt(), day.toInt()).let {
            Date.from(it.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }
    } catch (e: Exception) {
        throw IllegalStateException("Invalid date format in CSV: $bookingDayStr", e)
    }

    val amount = amountStr.toDouble()
    val monetaryAmount = if (amount > 0) Income(amount) else Expense(amount)
    val transaction = Transaction(number = index, date = bookingDay, description = usage, amount = monetaryAmount)

    return if (filter?.invoke(transaction) != false) transaction else null
}


private fun extractHeaderInformation(columns: List<String>): Map<RelevantColumns, Int> {
    val map = mutableMapOf<RelevantColumns, Int>()
    columns.forEachIndexed { index, cell ->
        getMatchingColumn(cell.clean())?.let { key -> map[key] = index }
    }
    RelevantColumns.entries.forEach { key ->
        if (!map.keys.contains(key)) {
            throw IllegalStateException("Could not find '${Constants.columnMappings[key]}' column in csv")
        }
    }
    return map
}

private fun getMatchingColumn(possibleMatch: String): RelevantColumns? =
    Constants.columnMappings.filter { it.value.equals(possibleMatch.trim(), ignoreCase = true) }.keys.firstOrNull()