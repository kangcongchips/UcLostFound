package com.example.uclostfound.data

import com.example.uclostfound.model.User

object Session {
    var currentUser: User? = null

    fun isStaff() = currentUser?.role == "STAFF"
}