package com.daragly.daraglyhair

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform