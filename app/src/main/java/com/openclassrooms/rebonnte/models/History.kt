package com.openclassrooms.rebonnte.models

data class History(
    val medicineName: String = "",
    val userId: String = "",
    val date: String = "",
    val details: String = ""
) {
    constructor() : this("", "", "", "")
}
