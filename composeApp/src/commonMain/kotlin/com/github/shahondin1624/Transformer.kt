package com.github.shahondin1624

import com.github.shahondin1624.model.YearlyTransactions

fun applyModifications(input: YearlyTransactions): YearlyTransactions {
  return input.copy(
      transactions = input.transactions.sortedBy { it.date },
      metaInformation = input.metaInformation.copy(iban = formatIban(input.metaInformation.iban)))
}
