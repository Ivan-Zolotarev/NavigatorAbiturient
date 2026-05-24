package ru.navigator.abiturient.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.navigator.abiturient.R
import ru.navigator.abiturient.data.repository.CollegeRepository
import ru.navigator.abiturient.data.repository.FavoriteRepository
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import ru.navigator.abiturient.ui.colleges.CollegeSearchResultCard
import ru.navigator.abiturient.ui.components.AppSnackbarHost
import ru.navigator.abiturient.ui.components.CachedDataSnackbarEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    collegeRepository: CollegeRepository,
    favoriteRepository: FavoriteRepository,
    onCollegeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SearchViewModel = viewModel(
        factory = SearchViewModel.Factory(collegeRepository),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteKeys by favoriteRepository.observeFavoriteKeys()
        .collectAsStateWithLifecycle(initialValue = emptySet())
    val scope = rememberCoroutineScope()
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
            TopAppBar(title = { Text(stringResource(R.string.nav_search)) })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                label = { Text(stringResource(R.string.search_hint)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true,
            )
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = uiState.budgetOnly,
                    onClick = { viewModel.onBudgetOnlyChange(!uiState.budgetOnly) },
                    label = { Text(stringResource(R.string.search_filter_budget)) },
                )
                FilterChip(
                    selected = uiState.paidOnly,
                    onClick = { viewModel.onPaidOnlyChange(!uiState.paidOnly) },
                    label = { Text(stringResource(R.string.search_filter_paid)) },
                )
            }
            OutlinedTextField(
                value = uiState.minPassingScore,
                onValueChange = viewModel::onMinScoreChange,
                label = { Text(stringResource(R.string.search_filter_min_score)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true,
            )
            DistrictDropdown(
                districts = uiState.districts,
                selected = uiState.selectedDistrict,
                onSelected = viewModel::onDistrictChange,
                modifier = Modifier.padding(top = 8.dp),
            )
            Button(
                onClick = { viewModel.search() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
            ) {
                Text(stringResource(R.string.search_apply))
            }
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.hasSearched && uiState.results.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(stringResource(R.string.search_empty))
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(uiState.results, key = { it.id }) { college ->
                            val isFavorite = favoriteKeys.contains("COLLEGE_${college.id}")
                            CollegeSearchResultCard(
                                college = college,
                                isFavorite = isFavorite,
                                onDetailsClick = { onCollegeClick(college.id) },
                                onFavoriteToggle = {
                                    scope.launch {
                                        favoriteRepository.toggleCollegeFavorite(college, isFavorite)
                                    }
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DistrictDropdown(
    districts: List<String>,
    selected: String?,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val label = selected ?: stringResource(R.string.search_filter_district_all)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = label,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.search_filter_district)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.search_filter_district_all)) },
                onClick = {
                    onSelected(null)
                    expanded = false
                },
            )
            districts.forEach { district ->
                DropdownMenuItem(
                    text = { Text(district) },
                    onClick = {
                        onSelected(district)
                        expanded = false
                    },
                )
            }
        }
    }
}
