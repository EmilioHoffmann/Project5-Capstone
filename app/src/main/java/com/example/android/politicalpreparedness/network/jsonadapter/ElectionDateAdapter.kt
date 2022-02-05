package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class ElectionDateAdapter {

    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd"
        val formatter: DateFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
    }

    @FromJson
    fun dateFromJson(electionDay: String): Date = formatter.parse(electionDay)

    @ToJson
    fun dateToJson(electionDay: Date): String = formatter.format(electionDay)
}
