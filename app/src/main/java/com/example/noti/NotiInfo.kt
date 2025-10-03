package com.example.noti

data class NotiInfo(
    val hour: Int = -1,
    val minute: Int = -1,
    val isActive: Boolean,
    val message: String,
    val period: Int = 0,
    val id: Int = 1
)
