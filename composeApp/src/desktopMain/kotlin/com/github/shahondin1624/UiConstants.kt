package com.github.shahondin1624

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

object UiConstants {
    val PADDING_VERY_SMALL = 2.dp
    val PADDING_SMALL = 4.dp
    val SPACER_HEIGHT_DEFAULT = 8.dp
    val SPACER_HEIGHT_LARGE = 16.dp

    object Table {
        const val NUMBERS_COLUMN_WEIGHT = 0.3f
        const val DESCRIPTION_COLUMN_WEIGHT = NUMBERS_COLUMN_WEIGHT
        const val ICON_COLUMN_WEIGHT = 0.04f
        const val INPUT_FIELD_COLUMN_WEIGHT = NUMBERS_COLUMN_WEIGHT + 0.2f
        const val APPLY_COLUMN_WEIGHT = ICON_COLUMN_WEIGHT
    }

    object Text {
        val BIG_TEXT_SIZE = TextUnit(16f, TextUnitType.Sp)
    }
}