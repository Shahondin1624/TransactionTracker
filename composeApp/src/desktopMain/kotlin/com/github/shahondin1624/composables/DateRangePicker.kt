package com.github.shahondin1624.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.shahondin1624.Constants
import java.util.*

@Composable
fun DateRangePicker(
    startDateFocusRequester: FocusRequester,
    endDateFocusRequester: FocusRequester,
    nextFocusRequester: FocusRequester? = null,
    initialStartDate: Date? = null,
    initialEndDate: Date? = null,
    onAllRangeSelected: (Pair<Date, Date>) -> Unit = {}
) {
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var isInvalidRange by remember { mutableStateOf(false) }

    DatePicker(
        initialValue = initialStartDate,
        labelText = "Enter Start Date",
        focusRequester = startDateFocusRequester,
        nextFocusRequester = endDateFocusRequester
    ) {
        startDate = it.toDate()
        if (endDate != null) {
            isInvalidRange = startDate!!.after(endDate)
            if (!isInvalidRange) {
                onAllRangeSelected(Pair(startDate!!, endDate!!))
            }
        } else {
            isInvalidRange = false
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    DatePicker(
        initialValue = initialEndDate,
        labelText = "Enter End Date",
        focusRequester = endDateFocusRequester,
        nextFocusRequester = nextFocusRequester
    ) {
        endDate = it.toDate()
        try {
            if (startDate != null) {
                isInvalidRange = startDate!!.after(endDate!!)
                if (!isInvalidRange) {
                    onAllRangeSelected(Pair(startDate!!, endDate!!))
                }
            }
        } catch (_: Exception) {
            isInvalidRange = true
        }
    }

    if (isInvalidRange) {
        Text("End date must be after start date", color = Color.Red)
    }
}

private fun String.toDate(): Date? {
    return runCatching { Constants.dateFormatter.parse(this) }.getOrNull()
}