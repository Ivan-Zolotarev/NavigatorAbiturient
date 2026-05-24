package ru.navigator.abiturient.ui.colleges

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.navigator.abiturient.R
import ru.navigator.abiturient.domain.model.College
import ru.navigator.abiturient.domain.model.CollegeType

@Composable
fun CollegeTypeExtras(
    college: College,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        when (college.type) {
            CollegeType.PRIVATE -> {
                college.tuitionCost?.let { cost ->
                    Text(
                        text = stringResource(R.string.detail_tuition_cost, cost),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                college.installmentAvailable?.let { available ->
                    Text(
                        text = stringResource(
                            if (available) R.string.detail_installment_yes
                            else R.string.detail_installment_no,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            CollegeType.FEDERAL -> {
                college.specialAdmissionConditions?.let { conditions ->
                    Text(
                        text = stringResource(R.string.detail_special_conditions, conditions),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                college.hasDormitory?.let { hasDorm ->
                    Text(
                        text = stringResource(
                            if (hasDorm) R.string.detail_dormitory_yes
                            else R.string.detail_dormitory_no,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
            CollegeType.STATE -> Unit
        }
        college.district?.let { district ->
            Text(
                text = stringResource(R.string.detail_district, district),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
