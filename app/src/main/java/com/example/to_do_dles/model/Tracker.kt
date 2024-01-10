package com.example.to_do_dles.model

import androidx.room.ColumnInfo
import com.example.to_do_dles.enums.TrackerType

data class Tracker(
    @ColumnInfo
    var trackerType: TrackerType = TrackerType.NON,
    @ColumnInfo
    var trackerCounter:Int = 0,
    @ColumnInfo
    var trackerMax:Int = 0,
    @ColumnInfo
    var trackerTimeInMillis:Long = 0
)
