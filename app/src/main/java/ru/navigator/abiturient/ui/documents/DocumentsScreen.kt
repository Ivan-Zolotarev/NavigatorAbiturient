package ru.navigator.abiturient.ui.documents

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import ru.navigator.abiturient.data.repository.DocumentsRepository
import ru.navigator.abiturient.domain.model.DocumentItem
import ru.navigator.abiturient.ui.components.AppSnackbarHost
import ru.navigator.abiturient.ui.components.CachedDataSnackbarEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreen(
    documentsRepository: DocumentsRepository,
    modifier: Modifier = Modifier,
) {
    val viewModel: DocumentsViewModel = viewModel(
        factory = DocumentsViewModel.Factory(documentsRepository),
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
            TopAppBar(title = { Text(stringResource(R.string.documents_title)) })
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
                    Text(stringResource(R.string.college_error))
                    Button(
                        onClick = { viewModel.load() },
                        modifier = Modifier.padding(top = 16.dp),
                    ) {
                        Text(stringResource(R.string.college_retry))
                    }
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
                    items(uiState.documents, key = { it.id }) { document ->
                        DocumentCard(
                            document = document,
                            onOpenLink = { url ->
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            },
                            onOpenPdf = { url ->
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentCard(
    document: DocumentItem,
    onOpenLink: (String) -> Unit,
    onOpenPdf: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = document.title, style = MaterialTheme.typography.titleMedium)
            Text(
                text = document.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
            document.linkUrl?.let { url ->
                TextButton(
                    onClick = { onOpenLink(url) },
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text(stringResource(R.string.documents_open_link))
                }
            }
            document.pdfUrl?.let { url ->
                TextButton(onClick = { onOpenPdf(url) }) {
                    Text(stringResource(R.string.documents_open_pdf))
                }
            }
        }
    }
}
