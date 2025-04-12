package com.example.feature.localdecks.presentation.add_edit_card

import android.net.Uri

data class AddEditCardScreenState(
    val question: String = "",
    val answer: String = "",
    val questionError: String? = null,
    val answerError: String? = null,
    val cardPictureUri: Uri? = null,
    val isSaveButtonEnabled: Boolean = true,
    val wrongAnswerList: List<String> = emptyList(),
    val attachment: String? = null,
    val picturePath: String? = null,
)