package com.example.uclostfound.model

data class Claim(
    val id: String = "",
    val type: String = "",
    val name: String = "",
    val category: String = "",
    val description: String = "",
    val location: String = "",
    val dateReported: Long = 0L,
    val status: String = "LOST",
    val reporterId: String = "",
    val reporterName: String = "",
    val holdingLocation: String = ""
)