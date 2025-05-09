package com.github.shahondin1624.output

import com.github.shahondin1624.Constants
import com.github.shahondin1624.Styling
import com.github.shahondin1624.model.*
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.util.*
import kotlin.math.abs

fun exportToExcelFile(yearlyTransactions: YearlyTransactions, filePath: String) {
    XSSFWorkbook().use { workbook ->
        val sheet = workbook.createSheet("Tabelle1")

        createStyling(workbook)

        val startRowIndex =
            createHeader(
                sheet = sheet,
                metaInformation = yearlyTransactions.metaInformation,
                numberOfTransactions = yearlyTransactions.transactions.size,
                amountBefore = yearlyTransactions.previous
            )

        writeTransactions(transactions = yearlyTransactions.transactions, sheet = sheet, startIndex = startRowIndex)

        FileOutputStream(filePath).use { fileOut ->
            workbook.write(fileOut)
        }
    }
}

private fun writeTransactions(transactions: List<Transaction>, sheet: Sheet, startIndex: Int) {
    transactions.forEachIndexed { index, transaction ->
        val rowIndex = startIndex + index
        sheet.createRow(rowIndex).apply {
            this.createCell(0).setCellValue(transaction.number.toDouble())
            this.createCell(1).setCellValue(Constants.dateFormatter.format(transaction.date))
            this.createCell(2).setCellValue(transaction.description)
            val amountCell = when (transaction.amount) {
                is Income -> this.createCell(3)
                is Expense -> this.createCell(4)
                else -> throw IllegalStateException("A transaction must have either an income or an expense")
            }
            amountCell.setCellValue(abs(transaction.amount.amount))
            amountCell.cellStyle = Constants.styling.euroFormat

            val formulaCell = this.createCell(5)
            formulaCell.cellFormula = "F${rowIndex}+D${rowIndex + 1}-E${rowIndex + 1}"
            formulaCell.cellStyle = Constants.styling.euroFormat
        }
    }
}

private fun createHeader(
    sheet: Sheet,
    metaInformation: MetaInformation,
    numberOfTransactions: Int,
    amountBefore: Double
): Int {
    sheet.createRow(0).apply {
        this.createCell(0).setCellValue("Vorname:")
        this.createCell(1).setCellValue("Nachname:")
        this.createCell(2).setCellValue("IBAN")
    }
    sheet.createRow(1).apply {
        this.createCell(0).setCellValue(metaInformation.firstName)
        this.createCell(1).setCellValue(metaInformation.lastName)
        this.createCell(2).setCellValue(metaInformation.iban)
    }
    sheet.createRow(5).apply {
        this.createCell(0).setCellValue("Zeitraum")
        this.createCell(5).setCellValue("Gesamte Einnahmen")
        val formulaCell = this.createCell(6)
        formulaCell.cellFormula = "SUM(D10:D${10 + numberOfTransactions})"
        formulaCell.cellStyle = Constants.styling.totalIncomeStyle
    }
    sheet.createRow(6).apply {
        this.createCell(0).setCellValue("${metaInformation.startDate.toFormattedString()}")
        this.createCell(1).setCellValue("${metaInformation.endDate.toFormattedString()}")
        this.createCell(5).setCellValue("Gesamte Ausgaben")
        val formulaCell = this.createCell(6)
        formulaCell.cellFormula = "SUM(E9:E${10 + numberOfTransactions})"
        formulaCell.cellStyle = Constants.styling.totalExpenseStyle
    }
    sheet.createRow(7).apply {
        this.createCell(0).setCellValue("lfd. Nr.")
        this.createCell(1).setCellValue("Datum")
        this.createCell(2).setCellValue("Bezeichnung der Einnahme/Ausgabe")
        this.createCell(3).setCellValue("Einnahmen")
        this.createCell(4).setCellValue("Ausgaben")
        this.createCell(5).setCellValue("Momentaner Stand")
    }
    sheet.createRow(8).apply {
        this.createCell(2).setCellValue("Übertrag von vorher/Startstand")
        val previousAmountCell = this.createCell(3)
        previousAmountCell.setCellValue(amountBefore)
        previousAmountCell.cellStyle = Constants.styling.euroFormat
        val formulaCell = this.createCell(5)
        formulaCell.cellFormula = "D9-E9"
        formulaCell.cellStyle = Constants.styling.euroFormat
    }
    return 9
}

private fun createStyling(workbook: Workbook) {
    val dataFormat = workbook.createDataFormat()
    val euroFormat = dataFormat.getFormat("#,##0.00\u00a0€")

    val totalIncomeStyle = workbook.createCellStyle()
    totalIncomeStyle.fillForegroundColor = IndexedColors.GREEN.index
    totalIncomeStyle.fillPattern = org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND
    totalIncomeStyle.dataFormat = euroFormat

    val totalExpenseStyle = workbook.createCellStyle()
    totalExpenseStyle.fillForegroundColor = IndexedColors.RED.index
    totalExpenseStyle.fillPattern = org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND
    totalExpenseStyle.dataFormat = euroFormat

    val euroStyle = workbook.createCellStyle()
    euroStyle.dataFormat = euroFormat

    Constants.styling = Styling(
        totalIncomeStyle = totalIncomeStyle,
        totalExpenseStyle = totalExpenseStyle,
        euroFormat = euroStyle
    )
}

private fun Date.toFormattedString(): String? {
    return runCatching { Constants.dateFormatter.format(this) }.getOrNull()
}