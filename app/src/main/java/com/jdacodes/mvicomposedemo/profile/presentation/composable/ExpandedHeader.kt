package com.jdacodes.mvicomposedemo.profile.presentation.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.jdacodes.mvicomposedemo.R

import com.jdacodes.mvicomposedemo.auth.domain.model.User
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileAction
import com.jdacodes.mvicomposedemo.profile.presentation.connection
import com.jdacodes.mvicomposedemo.profile.util.CollapsingAppBarNestedScrollConnection
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandedHeader(
    user: User?,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    //To simulate Header Content
    //SubcomposeLayout is a composable allowing different parts of the layout to be measured and placed independently
    SubcomposeLayout(modifier) { constraints -> //contains the size constraints like width and height

        val headerPlaceable = subcompose("header") {
            Column(modifier = modifier) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.gradient_banner),
                        contentDescription = "Header Image",
                        contentScale = ContentScale.Crop,
                    )

                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .size(56.dp)
                    ) {

                        if (user != null) {
                            CircularImage(imageUrl = user.photoUrl)
                        }
                    }
                }
                if (user != null) {
                    HeaderContent(
                        user = user
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.height(16.dp),
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }.first().measure(constraints) //holds the size and layout result of the header content

        val navBarPlaceable = subcompose("navBar") {
            NavBar(
                user = user,
                onAction = onAction,
                onSignOutClick = { onAction(ProfileAction.SignOut) },
                modifier = Modifier.fillMaxWidth(),

                )
        }.first().measure(constraints)

        connection.maxHeight = headerPlaceable.height.toFloat()
        connection.minHeight = navBarPlaceable.height.toFloat()

        val space = IntSize(
            constraints.maxWidth,
            headerPlaceable.height + connection.headerOffset.roundToInt()
        )
        layout(space.width, space.height) {
            headerPlaceable.place(0, connection.headerOffset.roundToInt())
            navBarPlaceable.place(
                Alignment.TopCenter.align(
                    IntSize(navBarPlaceable.width, navBarPlaceable.height),
                    space,
                    layoutDirection
                )
            )
        }
    }
}