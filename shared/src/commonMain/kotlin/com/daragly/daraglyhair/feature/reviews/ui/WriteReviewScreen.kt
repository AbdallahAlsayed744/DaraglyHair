package com.daragly.daraglyhair.feature.reviews.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.daragly.daraglyhair.common.ui.navigation.INavigator
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGold
import com.daragly.daraglyhair.designsystem.components.buttons.HallaakButtonGhost
import com.daragly.daraglyhair.designsystem.components.textfield.HallaakTextField
import com.daragly.daraglyhair.designsystem.theme.HallaakTheme
import com.daragly.daraglyhair.feature.profile.ui.rememberImagePickerLauncher
import com.daragly.daraglyhair.feature.reviews.domain.model.ReviewPhoto
import com.daragly.daraglyhair.feature.reviews.ui.components.RatingCategoryRow
import com.daragly.daraglyhair.feature.reviews.ui.components.ReviewPhotoChip
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingSectionCard
import com.daragly.daraglyhair.feature.shopbooking.ui.components.BookingTopBar
import daraglyhair.shared.generated.resources.Res
import daraglyhair.shared.generated.resources.add_photo
import daraglyhair.shared.generated.resources.rating_cleanliness
import daraglyhair.shared.generated.resources.rating_overall
import daraglyhair.shared.generated.resources.rating_quality
import daraglyhair.shared.generated.resources.rating_value
import daraglyhair.shared.generated.resources.review_comment_placeholder
import daraglyhair.shared.generated.resources.submit_review
import daraglyhair.shared.generated.resources.write_review_title
import io.ktor.util.encodeBase64
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun WriteReviewScreen(
    appointmentId: String,
    shopName: String,
    viewModel: WriteReviewViewModel = koinViewModel(parameters = { parametersOf(appointmentId) }),
    navigator: INavigator = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    val launchImagePicker = rememberImagePickerLauncher { _, _, bytes, extension ->
        if (bytes != null && extension != null) {
            viewModel.onAction(
                WriteReviewAction.PhotoAdded(
                    ReviewPhoto(
                        fileName = "review${state.photos.size + 1}$extension",
                        extension = extension,
                        dataBase64 = bytes.encodeBase64(),
                    ),
                ),
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                WriteReviewEvent.Submitted -> navigator.navigateUp()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HallaakTheme.colors.background.canvas),
    ) {
        BookingTopBar(
            title = stringResource(Res.string.write_review_title),
            onBack = { scope.launch { navigator.navigateUp() } },
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(HallaakTheme.dimensions.space16),
            verticalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space16),
        ) {
            if (shopName.isNotBlank()) {
                Text(
                    text = shopName,
                    style = HallaakTheme.typography.title.extraLarge,
                    color = HallaakTheme.colors.onSurface,
                )
            }

            BookingSectionCard(title = stringResource(Res.string.rating_overall)) {
                RatingCategoryRow(
                    label = stringResource(Res.string.rating_overall),
                    value = state.ratings[RatingCategory.OVERALL] ?: 0,
                    onValueChange = { viewModel.onAction(WriteReviewAction.RatingChanged(RatingCategory.OVERALL, it)) },
                )
                Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                RatingCategoryRow(
                    label = stringResource(Res.string.rating_quality),
                    value = state.ratings[RatingCategory.QUALITY] ?: 0,
                    onValueChange = { viewModel.onAction(WriteReviewAction.RatingChanged(RatingCategory.QUALITY, it)) },
                )
                Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                RatingCategoryRow(
                    label = stringResource(Res.string.rating_cleanliness),
                    value = state.ratings[RatingCategory.CLEANLINESS] ?: 0,
                    onValueChange = { viewModel.onAction(WriteReviewAction.RatingChanged(RatingCategory.CLEANLINESS, it)) },
                )
                Spacer(Modifier.height(HallaakTheme.dimensions.space12))
                RatingCategoryRow(
                    label = stringResource(Res.string.rating_value),
                    value = state.ratings[RatingCategory.VALUE] ?: 0,
                    onValueChange = { viewModel.onAction(WriteReviewAction.RatingChanged(RatingCategory.VALUE, it)) },
                )
            }

            BookingSectionCard(title = stringResource(Res.string.review_comment_placeholder)) {
                HallaakTextField(
                    value = state.comment,
                    onValueChange = { viewModel.onAction(WriteReviewAction.CommentChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = stringResource(Res.string.review_comment_placeholder),
                    singleLine = false,
                    minLines = 3,
                    maxLines = 6,
                )
            }

            BookingSectionCard(title = stringResource(Res.string.add_photo)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(HallaakTheme.dimensions.space8),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    state.photos.forEachIndexed { index, photo ->
                        ReviewPhotoChip(
                            index = index,
                            onRemove = { viewModel.onAction(WriteReviewAction.PhotoRemoved(photo)) },
                        )
                    }
                    if (state.photos.size < WriteReviewViewModel.MAX_PHOTOS) {
                        HallaakButtonGhost(
                            label = stringResource(Res.string.add_photo),
                            onClick = { launchImagePicker() },
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(HallaakTheme.dimensions.space16),
        ) {
            HallaakButtonGold(
                label = stringResource(Res.string.submit_review),
                onClick = { viewModel.onAction(WriteReviewAction.Submit) },
                modifier = Modifier.fillMaxWidth(),
                isEnabled = state.canSubmit,
            )
        }
    }
}
