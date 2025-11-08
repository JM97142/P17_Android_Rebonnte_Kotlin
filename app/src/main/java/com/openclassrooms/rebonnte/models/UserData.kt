package com.openclassrooms.rebonnte.models

data class UserData(
    val userId: String,
    val userName: String,
    val email: String,
    val profilePictureUrl: String?,
    val photoUrl: String?
)