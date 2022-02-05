package com.example.android.politicalpreparedness.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.android.politicalpreparedness.R
import java.time.Instant
import java.time.ZoneId
import java.util.*

@BindingAdapter("app:simpleElectionDate")
fun TextView.showSimpleElectionDate(date: Date?) {
    if (date == null) {
        return
    }
    val localDate = Instant.ofEpochMilli(date.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    text = context.getString(
        R.string.election_date_place_holder,
        localDate.dayOfWeek.name.take(3).lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
        localDate.month.name.take(3).lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
        localDate.year.toString().takeLast(2)
    )
}

@BindingAdapter("app:electionDate")
fun TextView.showElectionDate(date: Date?) {
    if (date == null) {
        return
    }
    val localDate = Instant.ofEpochMilli(date.time)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    text = context.getString(
        R.string.election_date_place_holder,
        localDate.dayOfMonth.toString(),
        localDate.month.name.lowercase()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
        localDate.year.toString()
    )
}

@BindingAdapter("app:loadRoundImage")
fun ImageView.loadRoundImage(imageUrl: String?) {
    val uri = imageUrl?.toUri()?.buildUpon()?.scheme("https")?.build()
    Glide.with(context).load(uri)
        .transform(CircleCrop())
        .error(R.drawable.ic_profile)
        .placeholder(R.drawable.ic_profile)
        .into(this)
}
