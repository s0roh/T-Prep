package com.example.feature.training.presentation.training_errors

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.models.Source
import com.example.feature.training.domain.GetCardPictureUseCase
import com.example.feature.training.domain.GetDeckNameTrainingSessionTimeSourceUseCase
import com.example.feature.training.domain.GetErrorsListUseCase
import com.example.training.domain.entity.TrainingError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
internal class TrainingErrorsViewModel @Inject constructor(
    private val getErrorsListUseCase: GetErrorsListUseCase,
    private val getDeckNameTrainingSessionTimeSourceUseCase: GetDeckNameTrainingSessionTimeSourceUseCase,
    private val getCardPictureUseCase: GetCardPictureUseCase,
) : ViewModel() {

    var errorsList = MutableStateFlow<List<TrainingError>>(emptyList())
        private set

    var errorPictures = MutableStateFlow<Map<Int, Uri?>>(emptyMap())
        private set

    var pictureErrors = MutableStateFlow<Map<Int, String>>(emptyMap())
        private set

    var trainingSessionTime = MutableStateFlow<Long>(0L)
        private set

    var currentSource: Source? = null

    fun loadErrorsData(trainingSessionId: String) {
        viewModelScope.launch {
            val errors = getErrorsListUseCase(trainingSessionId)
            errorsList.value = errors

            val (_, sessionTime, source) = getDeckNameTrainingSessionTimeSourceUseCase(
                trainingSessionId
            )
            currentSource = source
            trainingSessionTime.value = sessionTime

            val pictureMap = mutableMapOf<Int, Uri?>()
            val errorMap = mutableMapOf<Int, String>()

            errors.forEach { error ->
                val attachment = error.attachment
                if (!attachment.isNullOrBlank()) {
                    try {
                        val uri = getCardPictureUseCase(
                            deckId = error.deckId,
                            cardId = error.cardId,
                            source = source,
                            attachment = attachment
                        )
                        if (uri != null) {
                            pictureMap[error.cardId] = uri
                        }
                    } catch (_: ConnectException) {
                        errorMap[error.cardId] =
                            "Не удалось подключиться к серверу. Проверьте соединение."
                    } catch (e: Exception) {
                        if (e.message?.contains("Card picture not found") == true) {
                            errorMap[error.cardId] =
                                "Изображение не найдено (возможно, было изменено или удалено)"
                        }
                        pictureMap[error.cardId] = null
                    }
                }
            }

            errorPictures.value = pictureMap
            pictureErrors.value = errorMap
        }
    }
}