package com.example.gacha2dam.model

data class User(
    val userId: String? = null,
    val displayName: String? = null,
    val pjsObtenidos: HashMap<String, Boolean> = hashMapOf()
)