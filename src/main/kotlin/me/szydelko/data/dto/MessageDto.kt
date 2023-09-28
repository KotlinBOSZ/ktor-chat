package me.szydelko.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(val text: String,val username: String, val timestamp: Long)
