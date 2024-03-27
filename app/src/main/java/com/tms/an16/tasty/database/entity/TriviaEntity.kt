package com.tms.an16.tasty.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tms.an16.tasty.model.Trivia
import com.tms.an16.tasty.util.Constants.Companion.TRIVIA_TABLE

@Entity(tableName = TRIVIA_TABLE)
data class TriviaEntity(
    @Embedded
    var trivia: Trivia
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}