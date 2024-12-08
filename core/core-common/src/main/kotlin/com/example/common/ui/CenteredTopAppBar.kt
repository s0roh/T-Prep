package com.example.common.ui

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.common.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenteredTopAppBar(
    title: String,
    shouldShowArrowBack: Boolean = false,
    onBackClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigationIcon = {
            if (shouldShowArrowBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}