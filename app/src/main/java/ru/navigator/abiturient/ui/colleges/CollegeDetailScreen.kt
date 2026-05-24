package ru.navigator.abiturient.ui.colleges

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.navigator.abiturient.R
import ru.navigator.abiturient.data.repository.CollegeRepository
import ru.navigator.abiturient.data.repository.FavoriteRepository
import ru.navigator.abiturient.domain.model.College
import ru.navigator.abiturient.domain.model.Specialty
import ru.navigator.abiturient.ui.components.FavoriteHeartButton
import ru.navigator.abiturient.ui.components.AppSnackbarHost
import ru.navigator.abiturient.ui.components.CachedDataSnackbarEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollegeDetailScreen(
    collegeId: Int,
    collegeRepository: CollegeRepository,
    favoriteRepository: FavoriteRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: CollegeDetailViewModel = viewModel(
        factory = CollegeDetailViewModel.Factory(collegeId, collegeRepository, favoriteRepository),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val favoriteKeys by favoriteRepository.observeFavoriteKeys()
        .collectAsStateWithLifecycle(initialValue = emptySet())
    val context = LocalContext.current
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
                    Text(text = uiState.college?.name ?: stringResource(R.string.college_loading))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.college_back),
                        )
                    }
                },
                actions = {
                    uiState.college?.let { college ->
                        val isFavorite = favoriteKeys.contains("COLLEGE_${college.id}")
                        FavoriteHeartButton(
                            isFavorite = isFavorite,
                            onToggle = { viewModel.toggleCollegeFavorite(isFavorite) },
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
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
                        onClick = { viewModel.loadCollege() },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text(stringResource(R.string.college_retry))
                    }
                }
            }
            uiState.college != null -> {
                CollegeDetailContent(
                    college = uiState.college!!,
                    favoriteKeys = favoriteKeys,
                    modifier = Modifier.padding(innerPadding),
                    onOpenWebsite = { url ->
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    onToggleSpecialtyFavorite = { specialty, isFavorite ->
                        viewModel.toggleSpecialtyFavorite(specialty, isFavorite)
                    },
                )
            }
        }
    }
}

@Composable
private fun CollegeDetailContent(
    college: College,
    favoriteKeys: Set<String>,
    onOpenWebsite: (String) -> Unit,
    onToggleSpecialtyFavorite: (Specialty, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = college.shortDescription,
                style = MaterialTheme.typography.bodyLarge,
            )
            CollegeTypeExtras(college = college, modifier = Modifier.padding(top = 8.dp))
            college.address?.let { address ->
                Text(
                    text = stringResource(R.string.detail_address, address),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            college.websiteUrl?.let { url ->
                TextButton(
                    onClick = { onOpenWebsite(url) },
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text(stringResource(R.string.detail_website))
                }
            }
        }
        item {
            Text(
                text = stringResource(R.string.detail_specialties),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        items(college.specialties, key = { it.id }) { specialty ->
            val isFavorite = favoriteKeys.contains("SPECIALTY_${specialty.id}")
            SpecialtyCard(
                specialty = specialty,
                isFavorite = isFavorite,
                onFavoriteToggle = { onToggleSpecialtyFavorite(specialty, isFavorite) },
            )
        }
    }
}

@Composable
private fun SpecialtyCard(
    specialty: Specialty,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = specialty.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                FavoriteHeartButton(isFavorite = isFavorite, onToggle = onFavoriteToggle)
            }
            specialty.budgetPlaces?.let { places ->
                Text(
                    text = stringResource(R.string.detail_budget_places, places),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            specialty.paidPlaces?.let { places ->
                Text(
                    text = stringResource(R.string.detail_paid_places, places),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            specialty.passingScore?.let { score ->
                Text(
                    text = stringResource(R.string.detail_passing_score, score),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            specialty.admissionInfo?.let { info ->
                Text(
                    text = info,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
