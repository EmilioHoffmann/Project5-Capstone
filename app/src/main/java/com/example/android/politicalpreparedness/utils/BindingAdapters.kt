package com.example.android.politicalpreparedness.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import java.time.Instant
import java.time.ZoneId
import java.util.*

@BindingAdapter("app:electionDate")
fun TextView.showElectionDate(date: Date) {

    val localDate = Instant.ofEpochMilli(date.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    text = context.getString(
        R.string.election_date_place_holder, localDate.dayOfWeek.name.take(3),
        localDate.month.name.take(3),
        localDate.year.toString().takeLast(2)
    )
}
