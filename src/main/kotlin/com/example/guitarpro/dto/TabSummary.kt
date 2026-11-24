package com.example.guitarpro.dto

import java.time.LocalDateTime

data class TabSummary(
    val id: Long,
    val filename: String,
    val title: String,
    val artist: String?,
    val uploadedAt: LocalDateTime?
)
