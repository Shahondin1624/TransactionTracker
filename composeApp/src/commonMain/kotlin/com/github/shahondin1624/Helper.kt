package com.github.shahondin1624

import com.github.shahondin1624.model.MetaInformation
import com.github.shahondin1624.model.Transaction
import java.time.ZoneId
import java.util.*

fun createDTO(firstName: String, lastName: String, startDate: Date, endDate: Date) =
    MetaInformation(firstName, lastName, Constants.outgoingIBAN, startDate, endDate)

fun String.clean() = this.replace("\"", "").trim()

fun formatIban(iban: String): String {
    return "${iban.substring(0, 4)} ${iban.substring(4, 8)} ${iban.substring(8, 12)} ${iban.substring(12)}"
}

fun createTransactionFilter(startDate: Date, endDate: Date): ((Transaction) -> Boolean)? {
    val filter: ((Transaction) -> Boolean)? = {
        val startDate =
            startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val endDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val transactionDate = it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        val include =
            !transactionDate.isBefore(startDate) &&
                    !transactionDate.isAfter(endDate) &&
                    it.amount.amount != 0.0
        println("$it will ${if (!include) "NOT" else ""} be included in the report.")
        include
    }
    return filter
}