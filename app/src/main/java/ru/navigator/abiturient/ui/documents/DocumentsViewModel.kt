package ru.navigator.abiturient.ui.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.navigator.abiturient.data.repository.DocumentsRepository
import ru.navigator.abiturient.domain.model.DocumentItem

data class DocumentsUiState(
    val isLoading: Boolean = false,
    val documents: List<DocumentItem> = emptyList(),
    val error: String? = null,
    val showCachedDataHint: Boolean = false,
)

class DocumentsViewModel(
    private val repository: DocumentsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentsUiState(isLoading = true))
    val uiState: StateFlow<DocumentsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getDocuments()
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            documents = result.data,
                            showCachedDataHint = result.isFromCache,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
        }
    }

    fun cachedHintShown() {
        _uiState.update { it.copy(showCachedDataHint = false) }
    }

    class Factory(
        private val repository: DocumentsRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DocumentsViewModel(repository) as T
    }
}
