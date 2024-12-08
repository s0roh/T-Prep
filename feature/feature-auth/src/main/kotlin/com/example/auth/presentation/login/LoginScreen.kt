package com.example.auth.presentation.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auth.R
import com.example.auth.presentation.components.AuthTextField

@Composable
fun LoginScreen(
    onSuccessLoginListener: () -> Unit
) {
    val viewModel: LoginViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsState()

    LaunchedEffect(screenState) {
        if (screenState is LoginScreenState.Success) {
            onSuccessLoginListener()
        }
    }

    if (screenState !is LoginScreenState.Success){
        LoginScreenContent(
            screenState = screenState,
            onLoginClick = { login, password ->
                viewModel.onLoginClick(login, password)
            }
        )
    }
}

@Composable
private fun LoginScreenContent(
    screenState: LoginScreenState,
    onLoginClick: (String, String) -> Unit
) {
    var login by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    val isButtonEnabled = screenState !is LoginScreenState.Loading
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.auth_entrance),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
                value = login,
                onValueChange = { login = it },
                label = stringResource(R.string.auth_login),
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
            )

        Spacer(modifier = Modifier.height(8.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.auth_password),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            imeAction = ImeAction.Done,
            onImeAction = {
                focusManager.clearFocus()
                onLoginClick(login, password)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                onLoginClick(login, password)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isButtonEnabled
        ) {
            Text(text = stringResource(R.string.auth_entrance))
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoginScreenStateContent(screenState = screenState)
    }
}

@Composable
private fun LoginScreenStateContent(screenState: LoginScreenState) {
    when (screenState) {
        is LoginScreenState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
        }

        is LoginScreenState.Error -> {
            Text(
                text = screenState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        else -> {}
    }
}