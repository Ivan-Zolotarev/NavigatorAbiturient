package ru.navigator.abiturient.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.navigator.abiturient.data.repository.CollegeRepository

data class SplashUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val navigateToHome: Boolean = false,
)

class SplashViewModel(
    private val repository: CollegeRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        start()
    }

    fun start() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, navigateToHome = false) }
            val hasCache = repository.hasCachedData()
            if (hasCache) {
                delay(400)
                _uiState.update { it.copy(isLoading = false, navigateToHome = true) }
            } else {
                repository.warmUp()
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false, navigateToHome = true) }
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
    }

    class Factory(
        private val repository: CollegeRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            SplashViewModel(repository) as T
    }
}
