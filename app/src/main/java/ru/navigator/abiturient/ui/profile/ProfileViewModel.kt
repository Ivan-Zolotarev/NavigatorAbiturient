package ru.navigator.abiturient.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.navigator.abiturient.data.repository.DocumentsRepository
import ru.navigator.abiturient.domain.model.FaqItem

data class ProfileUiState(
    val isLoadingFaq: Boolean = false,
    val faqItems: List<FaqItem> = emptyList(),
    val expandedFaqIds: Set<Int> = emptySet(),
    val error: String? = null,
    val showCachedDataHint: Boolean = false,
)

class ProfileViewModel(
    private val documentsRepository: DocumentsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoadingFaq = true))
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadFaq()
    }

    fun loadFaq() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingFaq = true, error = null) }
            documentsRepository.getFaq()
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoadingFaq = false,
                            faqItems = result.data,
                            showCachedDataHint = result.isFromCache,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoadingFaq = false, error = e.message)
                    }
                }
        }
    }

    fun toggleFaq(id: Int) {
        _uiState.update { state ->
            val expanded = state.expandedFaqIds.toMutableSet()
            if (id in expanded) expanded.remove(id) else expanded.add(id)
            state.copy(expandedFaqIds = expanded)
        }
    }

    fun cachedHintShown() {
        _uiState.update { it.copy(showCachedDataHint = false) }
    }

    class Factory(
        private val documentsRepository: DocumentsRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ProfileViewModel(documentsRepository) as T
    }
}
