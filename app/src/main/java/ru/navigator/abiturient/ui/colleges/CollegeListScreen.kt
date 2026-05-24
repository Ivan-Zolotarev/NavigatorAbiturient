package ru.navigator.abiturient.ui.colleges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.navigator.abiturient.R
import ru.navigator.abiturient.data.repository.CollegeRepository
import ru.navigator.abiturient.data.repository.FavoriteRepository
import ru.navigator.abiturient.domain.model.CollegeType
import ru.navigator.abiturient.ui.components.AppSnackbarHost
import ru.navigator.abiturient.ui.components.CachedDataSnackbarEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeListScreen(
    collegeType: CollegeType,
    collegeRepository: CollegeRepository,
    favoriteRepository: FavoriteRepository,
    onBack: () -> Unit,
    onCollegeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CollegeListViewModel = viewModel(
        factory = CollegeListViewModel.Factory(collegeType, collegeRepository, favoriteRepository),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteKeys by favoriteRepository.observeFavoriteKeys()
        .collectAsStateWithLifecycle(initialValue = emptySet())
    val snackbarHostState = remember { SnackbarHostState() }

    CachedDataSnackbarEffect(
        showCachedHint = uiState.showCachedDataHint,
        snackbarHostState = snackbarHostState,
        onHintShown = viewModel::cachedHintShown,
    )

    Scaffold(
        modifier = modifier,
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(
                            when (collegeType) {
                                CollegeType.STATE -> R.string.college_list_title_state
                                CollegeType.PRIVATE -> R.string.college_list_title_private
                                CollegeType.FEDERAL -> R.string.college_list_title_federal
                            },
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.college_back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(text = stringResource(R.string.college_error))
                    Button(
                        onClick = { viewModel.loadColleges() },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text(stringResource(R.string.college_retry))
                    }
                }
            }
            uiState.colleges.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = stringResource(R.string.college_empty))
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(uiState.colleges, key = { it.id }) { college ->
                        val isFavorite = favoriteKeys.contains("COLLEGE_${college.id}")
                        CollegeCard(
                            college = college,
                            isFavorite = isFavorite,
                            onDetailsClick = { onCollegeClick(college.id) },
                            onFavoriteToggle = {
                                viewModel.toggleFavorite(college, isFavorite)
                            },
                        )
                    }
                }
            }
        }
    }
}
