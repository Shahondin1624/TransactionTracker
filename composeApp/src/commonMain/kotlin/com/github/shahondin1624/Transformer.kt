package com.github.shahondin1624

import com.github.shahondin1624.model.Transaction
import com.github.shahondin1624.model.YearlyTransactions

fun applyModifications(input: YearlyTransactions): YearlyTransactions {
  return input.copy(
      transactions = input.transactions.sortedBy { it.date },
      metaInformation = input.metaInformation.copy(iban = formatIban(input.metaInformation.iban)))
}

fun getAllUniqueDescriptions(transactions: List<Transaction>): Set<String> {
    return transactions.filter { !it.modified }.map { it.description }.toSet()
}

private fun mapDescriptions(transaction: List<Transaction>, mappings: Map<(String) -> Boolean, String>): List<Transaction> =
    transaction.map { tx ->
        val nullableCondition = mappings.keys.firstOrNull { it(tx.description) }
        if (nullableCondition != null && nullableCondition.invoke(tx.description)) {
            tx.copy(description = mappings[nullableCondition]!!)
        } else {
            tx
        }
    }
