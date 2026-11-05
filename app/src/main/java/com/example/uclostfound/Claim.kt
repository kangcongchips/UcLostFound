package com.example.uclostfound.model

data class Claim(
    val id: String = "",
    val itemId: String = "",
    val claimantId: String = "",
    val claimantName: String = "",
    val proofDescription: String = "",
    val status: String = "PENDING",
    val rejectReason: String = "",
    val dateSubmitted: Long = 0L
)