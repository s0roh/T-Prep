package com.example.database.models

data class AnswerStatsDBO(
    val correctCount: Int,
    val errorCount: Int,
    val allCount: Int? = null
)