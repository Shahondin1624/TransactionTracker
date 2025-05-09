package com.github.shahondin1624

import com.github.shahondin1624.input.RelevantColumns
import org.apache.poi.ss.usermodel.CellStyle

object Constants {
    var columnMappings: Map<RelevantColumns, String> = mapOf(
        RelevantColumns.BookingDay to "Buchungstag",
        RelevantColumns.Usage to "Verwendungszweck",
        RelevantColumns.Amount to "Betrag",
        RelevantColumns.OutgoingIBAN to "Auftragskonto",
    )
    var outgoingIBAN: String = ""
    var styling: Styling = Styling()
    val dateFormatter = java.text.SimpleDateFormat("dd.MM.yyyy")
}

data class Styling(val totalIncomeStyle: CellStyle? = null,
                   val totalExpenseStyle: CellStyle? = null,
                   val euroFormat: CellStyle? = null,)