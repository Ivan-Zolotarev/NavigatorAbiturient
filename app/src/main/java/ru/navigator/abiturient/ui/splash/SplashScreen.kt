package ru.navigator.abiturient.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.navigator.abiturient.R
import ru.navigator.abiturient.data.repository.CollegeRepository
import ru.navigator.abiturient.ui.theme.Burgundy
import ru.navigator.abiturient.ui.theme.OnBurgundy

@Composable
fun SplashScreen(
    collegeRepository: CollegeRepository,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SplashViewModel = viewModel(
        factory = SplashViewModel.Factory(collegeRepository),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Burgundy),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = stringResource(R.string.splash_title),
                style = MaterialTheme.typography.headlineLarge,
                color = OnBurgundy,
                textAlign = TextAlign.Center,
            )
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = OnBurgundy)
                    Text(
                        text = stringResource(R.string.splash_loading),
                        color = OnBurgundy,
                        textAlign = TextAlign.Center,
                    )
                }
                uiState.error != null -> {
                    Text(
                        text = stringResource(R.string.splash_error),
                        color = OnBurgundy,
                        textAlign = TextAlign.Center,
                    )
                    Button(onClick = { viewModel.start() }) {
                        Text(stringResource(R.string.college_retry))
                    }
                }
            }
        }
    }
}
