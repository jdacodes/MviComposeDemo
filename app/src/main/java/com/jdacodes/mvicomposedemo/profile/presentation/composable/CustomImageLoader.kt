package com.jdacodes.mvicomposedemo.profile.presentation.composable

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.jdacodes.mvicomposedemo.R
import kotlinx.coroutines.Dispatchers

@Composable
fun CircularImage(
    imageUrl: String? = "",
    context: Context = LocalContext.current,
    crossFade: Boolean = true,
    size: Dp = 60.dp,
    @DrawableRes placeholder: Int = R.drawable.ic_avatar_default,
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    // Build an ImageRequest with Coil
    val listener = object : ImageRequest.Listener {
        override fun onError(request: ImageRequest, result: ErrorResult) {
            super.onError(request, result)
        }

        override fun onSuccess(request: ImageRequest, result: SuccessResult) {
            super.onSuccess(request, result)
        }
    }
    val imageRequest = ImageRequest.Builder(context)
        .data(imageUrl)
        .listener(listener)
        .dispatcher(Dispatchers.IO)
        .memoryCacheKey(imageUrl)
        .diskCacheKey(imageUrl)
        .placeholder(placeholder)
        .crossfade(crossFade)
        .error(placeholder)
        .fallback(placeholder)
        .diskCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .build()
    // Load and display the image with AsyncImage
    AsyncImage(
        model = imageRequest,
        contentDescription = contentDescription,
        modifier = modifier
            .clip(CircleShape)
            .width(size)
            .height(size),
        contentScale = ContentScale.Crop,
    )

}