package ru.navigator.abiturient.ui.components

import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import ru.navigator.abiturient.R

@Composable
fun CachedDataSnackbarEffect(
    showCachedHint: Boolean,
    snackbarHostState: SnackbarHostState,
    onHintShown: () -> Unit,
    messageResId: Int = R.string.cache_shown_snackbar,
) {
    val message = stringResource(messageResId)
    LaunchedEffect(showCachedHint) {
        if (showCachedHint) {
            snackbarHostState.showSnackbar(message)
            onHintShown()
        }
    }
}

@Composable
fun AppSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(hostState = snackbarHostState)
}
