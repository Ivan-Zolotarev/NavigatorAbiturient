package ru.navigator.abiturient.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.navigator.abiturient.data.repository.CollegeRepository
import ru.navigator.abiturient.data.repository.CollegeSearchFilters
import ru.navigator.abiturient.domain.model.College

data class SearchUiState(
    val query: String = "",
    val budgetOnly: Boolean = false,
    val paidOnly: Boolean = false,
    val minPassingScore: String = "",
    val selectedDistrict: String? = null,
    val districts: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val results: List<College> = emptyList(),
    val hasSearched: Boolean = false,
    val error: String? = null,
    val showCachedDataHint: Boolean = false,
)

class SearchViewModel(
    private val repository: CollegeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val districts = repository.getDistricts()
            _uiState.update { it.copy(districts = districts) }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onBudgetOnlyChange(enabled: Boolean) {
        _uiState.update { it.copy(budgetOnly = enabled) }
    }

    fun onPaidOnlyChange(enabled: Boolean) {
        _uiState.update { it.copy(paidOnly = enabled) }
    }

    fun onMinScoreChange(value: String) {
        _uiState.update { it.copy(minPassingScore = value.filter { it.isDigit() }) }
    }

    fun onDistrictChange(district: String?) {
        _uiState.update { it.copy(selectedDistrict = district) }
    }

    fun search() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val minScore = state.minPassingScore.toIntOrNull()
            repository.searchColleges(
                query = state.query,
                filters = CollegeSearchFilters(
                    budgetOnly = state.budgetOnly,
                    paidOnly = state.paidOnly,
                    minPassingScore = minScore,
                    district = state.selectedDistrict,
                ),
            )
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = result.data,
                            hasSearched = true,
                            showCachedDataHint = result.isFromCache,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message,
                            hasSearched = true,
                        )
                    }
                }
        }
    }

    fun cachedHintShown() {
        _uiState.update { it.copy(showCachedDataHint = false) }
    }

    class Factory(
        private val repository: CollegeRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SearchViewModel(repository) as T
    }
}
