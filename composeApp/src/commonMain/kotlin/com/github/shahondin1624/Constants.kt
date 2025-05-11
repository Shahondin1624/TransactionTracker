package com.github.shahondin1624

import com.github.shahondin1624.input.RelevantColumns
import org.apache.poi.ss.usermodel.CellStyle

object Constants {
  var columnMappings: Map<RelevantColumns, String> =
      mapOf(
          RelevantColumns.BookingDay to "Buchungstag",
          RelevantColumns.Usage to "Verwendungszweck",
          RelevantColumns.Amount to "Betrag",
          RelevantColumns.OutgoingIBAN to "Auftragskonto",
      )
  var outgoingIBAN: String = ""
  var styling: Styling = Styling()
  val dateFormatter = java.text.SimpleDateFormat("dd.MM.yyyy")
  private var numberOfColumns = 0

  fun createColumn(newIndex: Int): Int {
    numberOfColumns = numberOfColumns.coerceAtLeast(newIndex)
    return newIndex
  }

    fun getNumberOfColumns() = numberOfColumns
}

data class Styling(
    val totalIncomeStyle: CellStyle? = null,
    val totalExpenseStyle: CellStyle? = null,
    val euroFormat: CellStyle? = null,
    val dateFormat: CellStyle? = null,
    val yellowBackground: CellStyle? = null,
)

