package com.example.training.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.training.R

@Composable
internal fun TrueFalseButton(
    text: String,
    containerColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Card(
            onClick = onClick,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(2.dp, borderColor),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(150.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (text == "ИСТИНА") R.drawable.ic_true
                        else R.drawable.ic_false
                    ),
                    contentDescription = null,
                    tint = if (text == "ИСТИНА") MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    modifier = if (text == "ИСТИНА") Modifier.size(100.dp) else Modifier.size((70.dp))
                )
            }
        }
    }
}