package com.tms.an16.tasty.model


import com.google.gson.annotations.SerializedName

data class Trivia(
    @SerializedName("text")
    val text: String
)