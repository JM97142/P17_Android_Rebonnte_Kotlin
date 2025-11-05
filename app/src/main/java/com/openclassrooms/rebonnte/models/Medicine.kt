package com.openclassrooms.rebonnte.models

data class Medicine(
    val name: String = "",
    val stock: Int = 0,
    val nameAisle: String = "",
    val histories: List<History> = emptyList(),
    val id: String = "",
    val addedByEmail: String = ""
) {
    constructor() : this("", 0, "", emptyList(), "", "")
}