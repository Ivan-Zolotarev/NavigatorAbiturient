package ru.navigator.abiturient.ui.colleges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.navigator.abiturient.R
import ru.navigator.abiturient.domain.model.College
import ru.navigator.abiturient.ui.components.FavoriteHeartButton

@Composable
fun CollegeCard(
    college: College,
    isFavorite: Boolean,
    onDetailsClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = college.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = college.shortDescription,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
            CollegeTypeExtras(college = college, modifier = Modifier.padding(top = 4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(onClick = onDetailsClick) {
                    Text(stringResource(R.string.college_details))
                }
                FavoriteHeartButton(
                    isFavorite = isFavorite,
                    onToggle = onFavoriteToggle,
                )
            }
        }
    }
}

@Composable
fun CollegeSearchResultCard(
    college: College,
    isFavorite: Boolean,
    onDetailsClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CollegeCard(
        college = college,
        isFavorite = isFavorite,
        onDetailsClick = onDetailsClick,
        onFavoriteToggle = onFavoriteToggle,
        modifier = modifier,
    )
}
