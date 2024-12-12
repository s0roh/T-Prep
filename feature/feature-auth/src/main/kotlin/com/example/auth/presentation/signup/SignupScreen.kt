package com.example.auth.presentation.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auth.presentation.components.AuthTextField

@Composable
fun SignupScreen(
) {
    val viewModel: SignupViewModel = hiltViewModel()
    val screenState = viewModel.screenState.collectAsState()
    // Состояние для полей ввода
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Поле ввода для имени пользователя
        AuthTextField(
            value = username,
            label = "Username",
            imeAction = ImeAction.Next,
            onValueChange = { username = it },
            onImeAction = {}
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Поле ввода для email
        AuthTextField(
            value = email,
            label = "Email",
            imeAction = ImeAction.Next,
            onValueChange = { email = it },
            onImeAction = {  }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Поле ввода для пароля
        AuthTextField(
            value = password,
            label = "Password",
            imeAction = ImeAction.Done,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { password = it },
            onImeAction = {}
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка для отправки данных
        Button(
            onClick = {  viewModel.onSignupClick(email, password, username) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign In")
        }
    }
}
