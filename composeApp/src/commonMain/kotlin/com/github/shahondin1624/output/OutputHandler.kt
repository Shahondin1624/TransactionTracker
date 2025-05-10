package com.github.shahondin1624.output

import com.github.shahondin1624.Constants
import com.github.shahondin1624.Constants.createColumn
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
            this.createCell(createColumn(0)).setCellValue((index + 1).toDouble())
            val dateCell = this.createCell(createColumn(1))
            dateCell.setCellValue(Constants.dateFormatter.format(transaction.date))
            dateCell.cellStyle = Constants.styling.dateFormat
            this.createCell(createColumn(2)).setCellValue(transaction.description)
            val amountCell = when (transaction.amount) {
                is Income -> this.createCell(createColumn(3))
                is Expense -> this.createCell(createColumn(4))
                else -> throw IllegalStateException("A transaction must have either an income or an expense")
            }
            amountCell.setCellValue(abs(transaction.amount.amount))
            amountCell.cellStyle = Constants.styling.euroFormat

            val formulaCell = this.createCell(createColumn(5))
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
        this.createCell(createColumn(0)).setCellValue("Vorname:")
        this.createCell(createColumn(1)).setCellValue("Nachname:")
        this.createCell(createColumn(2)).setCellValue("IBAN")
    }
    sheet.createRow(1).apply {
        this.createCell(createColumn(0)).setCellValue(metaInformation.firstName)
        this.createCell(createColumn(1)).setCellValue(metaInformation.lastName)
        val ibanCell = this.createCell(createColumn(2))
        ibanCell.setCellValue(metaInformation.iban)
        ibanCell.cellStyle = Constants.styling.yellowBackground
    }
    sheet.createRow(5).apply {
        this.createCell(createColumn(0)).setCellValue("Zeitraum")
        this.createCell(createColumn(5)).setCellValue("Gesamte Einnahmen")
        val formulaCell = this.createCell(createColumn(6))
        formulaCell.cellFormula = "SUM(D10:D${10 + numberOfTransactions})"
        formulaCell.cellStyle = Constants.styling.totalIncomeStyle
    }
    sheet.createRow(6).apply {
        this.createCell(createColumn(0)).setCellValue("${metaInformation.startDate.toFormattedString()}")
        this.createCell(createColumn(1)).setCellValue("${metaInformation.endDate.toFormattedString()}")
        this.createCell(createColumn(5)).setCellValue("Gesamte Ausgaben")
        val formulaCell = this.createCell(createColumn(6))
        formulaCell.cellFormula = "SUM(E9:E${10 + numberOfTransactions})"
        formulaCell.cellStyle = Constants.styling.totalExpenseStyle
    }
    sheet.createRow(7).apply {
        this.createCell(createColumn(0)).setCellValue("lfd. Nr.")
        this.createCell(createColumn(1)).setCellValue("Datum")
        this.createCell(createColumn(2)).setCellValue("Bezeichnung der Einnahme/Ausgabe")
        this.createCell(createColumn(3)).setCellValue("Einnahmen")
        this.createCell(createColumn(4)).setCellValue("Ausgaben")
        this.createCell(createColumn(5)).setCellValue("Momentaner Stand")
    }
    sheet.createRow(8).apply {
        this.createCell(createColumn(2)).setCellValue("Übertrag von vorher/Startstand")
        val previousAmountCell = this.createCell(createColumn(3))
        previousAmountCell.setCellValue(amountBefore)
        previousAmountCell.cellStyle = Constants.styling.euroFormat
        val formulaCell = this.createCell(createColumn(5))
        formulaCell.cellFormula = "D9-E9"
        formulaCell.cellStyle = Constants.styling.euroFormat
    }
    for (i in 0..Constants.getNumberOfColumns()) {
        sheet.autoSizeColumn(i)
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


    val dateStyle = workbook.createCellStyle()
    dateStyle.dataFormat = dataFormat.getFormat("dd.mm.yyyy")

    val yellowStyle = workbook.createCellStyle()
    yellowStyle.fillForegroundColor = IndexedColors.YELLOW.index
    yellowStyle.fillPattern = org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND

    Constants.styling = Styling(
        totalIncomeStyle = totalIncomeStyle,
        totalExpenseStyle = totalExpenseStyle,
        euroFormat = euroStyle,
        dateFormat = dateStyle,
        yellowBackground = yellowStyle
    )
}

private fun Date.toFormattedString(): String? {
    return runCatching { Constants.dateFormatter.format(this) }.getOrNull()
}