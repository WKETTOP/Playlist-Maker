package com.example.playlistmaker.search.domain.impl

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object Transform {

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun millisToMin(millis: String): String {
        return try {
            val seconds = (millis.toInt() / 1000)
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(seconds * 1000L)
        } catch (e: NumberFormatException) {
            "00:00"
        }
    }

    fun dateToYear(date: String): String {
        return try {
            val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val dateTime = LocalDate.parse(date, inputFormat)

            dateTime.year.toString()
        } catch (e: Exception) {
            "Unknown"
        }
    }
}
