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
import ru.navigator.abiturient.domain.model.Specialty

data class CollegeDetailUiState(
    val isLoading: Boolean = false,
    val college: College? = null,
    val error: String? = null,
    val showCachedDataHint: Boolean = false,
)

class CollegeDetailViewModel(
    private val collegeId: Int,
    private val repository: CollegeRepository,
    private val favoriteRepository: FavoriteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CollegeDetailUiState(isLoading = true))
    val uiState: StateFlow<CollegeDetailUiState> = _uiState.asStateFlow()

    init {
        loadCollege()
    }

    fun loadCollege() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getCollegeById(collegeId)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            college = result.data,
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

    fun toggleCollegeFavorite(isFavorite: Boolean) {
        val college = _uiState.value.college ?: return
        viewModelScope.launch {
            favoriteRepository.toggleCollegeFavorite(college, isFavorite)
        }
    }

    fun toggleSpecialtyFavorite(specialty: Specialty, isFavorite: Boolean) {
        val college = _uiState.value.college ?: return
        viewModelScope.launch {
            favoriteRepository.toggleSpecialtyFavorite(college, specialty, isFavorite)
        }
    }

    class Factory(
        private val collegeId: Int,
        private val repository: CollegeRepository,
        private val favoriteRepository: FavoriteRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CollegeDetailViewModel(collegeId, repository, favoriteRepository) as T
        }
    }
}
