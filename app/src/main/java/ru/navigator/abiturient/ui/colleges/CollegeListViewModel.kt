package ru.navigator.abiturient.ui.colleges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.navigator.abiturient.data.repository.CollegeRepository
import ru.navigator.abiturient.data.repository.FavoriteRepository
import ru.navigator.abiturient.domain.model.College
import ru.navigator.abiturient.domain.model.CollegeType

data class CollegeListUiState(
    val isLoading: Boolean = false,
    val colleges: List<College> = emptyList(),
    val error: String? = null,
    val showCachedDataHint: Boolean = false,
)

class CollegeListViewModel(
    private val collegeType: CollegeType,
    private val repository: CollegeRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollegeListUiState(isLoading = true))
    val uiState: StateFlow<CollegeListUiState> = _uiState.asStateFlow()

    init {
        loadColleges()
    }

    fun loadColleges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getCollegesByType(collegeType)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            colleges = result.data,
                            showCachedDataHint = result.isFromCache,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "error",
                        )
                    }
                }
        }
    }

    fun cachedHintShown() {
        _uiState.update { it.copy(showCachedDataHint = false) }
    }

    fun toggleFavorite(college: College, isActive: Boolean) {
        viewModelScope.launch {
            favoriteRepository.toggleCollegeFavorite(college, isActive)
        }
    }

    class Factory(
        private val collegeType: CollegeType,
        private val repository: CollegeRepository,
        private val favoriteRepository: FavoriteRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CollegeListViewModel(collegeType, repository, favoriteRepository) as T
        }
    }
}
