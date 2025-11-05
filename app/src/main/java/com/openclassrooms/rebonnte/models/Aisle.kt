package com.openclassrooms.rebonnte.models

import java.util.UUID

data class Aisle(
    var name: String = "",
    var id: String = UUID.randomUUID().toString(),
    var timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", UUID.randomUUID().toString(), System.currentTimeMillis())
}
