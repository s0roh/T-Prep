package com.example.auth.presentation.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.auth.R
import com.example.auth.presentation.components.AuthTextField
import com.example.auth.util.isEmailValid
import com.example.localdecks.util.startSyncWork

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthScreen(
    onSuccessAuthListener: () -> Unit,
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val screenState by viewModel.screenState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(screenState) {
        if (screenState is AuthScreenState.Success) {
            startSyncWork(context)
            onSuccessAuthListener()
        }
    }

    if (screenState !is AuthScreenState.Success) {
        AuthScreenContent(
            screenState = screenState,
            onLoginClick = { login, password ->
                viewModel.onLoginClick(login, password)
            },
            onSignupClick = { username, email, password ->
                viewModel.onSignupClick(email = email, password = password, name = username)
            }
        )
    }


}

@Composable
private fun AuthScreenContent(
    screenState: AuthScreenState,
    onLoginClick: (String, String) -> Unit,
    onSignupClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var tabState by remember { mutableIntStateOf(0) }
        val titles = listOf("Вход", "Регистрация")

        Text(text = "T-Prep", style = MaterialTheme.typography.displayLarge)

        Spacer(modifier = Modifier.height(34.dp))

        AuthTextTabs(
            modifier = Modifier.padding(horizontal = 62.dp),
            state = tabState,
            onStateChange = { newState -> tabState = newState },
            titles = titles
        )

        Spacer(modifier = Modifier.height(22.dp))

        AnimatedContent(
            targetState = tabState,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }.using(SizeTransform(clip = false))
            },
            label = "Tab switch animation"
        ) { currentTab ->
            when (currentTab) {
                0 -> {
                    LoginTabContent(
                        modifier = Modifier.padding(horizontal = 38.dp),
                        screenState = screenState,
                        onLoginClick = { email, password ->
                            onLoginClick(email, password)
                        }
                    )
                }

                1 -> {
                    SignupTabContent(
                        modifier = Modifier.padding(horizontal = 38.dp),
                        screenState = screenState,
                        onSignupClick = { username, email, password ->
                            onSignupClick(username, email, password)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AuthScreenStateContent(screenState = screenState)
    }
}


@Composable
private fun ButtonWithIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        onClick = { onClick() }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Continue",
            modifier = Modifier.size(ButtonDefaults.IconSize)
        )
        Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
        Text(text = "Продолжить")
    }
}

@Composable
private fun SignupTabContent(
    screenState: AuthScreenState,
    onSignupClick: (String, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf("") }
    val isButtonEnabled = screenState !is AuthScreenState.Loading
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        AuthTextField(
            value = username,
            onValueChange = { username = it },
            label = stringResource(R.string.auth_user_name),
            imeAction = ImeAction.Next,
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        Spacer(modifier = Modifier.height(15.dp))

        AuthTextField(
            value = email,
            onValueChange =  {
                email = it
                emailError = if (!isEmailValid(it)) "Введите корректную электронную почту" else ""
            },
            label = stringResource(R.string.auth_email),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email,
            isError = emailError.isNotEmpty(),
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )

        if (emailError.isNotEmpty()) {
            Text(
                text = emailError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

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
                onSignupClick(username, email, password)
            }
        )

        Spacer(modifier = Modifier.height(43.dp))

        ButtonWithIcon(
            enabled = isButtonEnabled,
            onClick = {
                focusManager.clearFocus()
                onSignupClick(username, email, password)
            }
        )
    }

}

@Composable
private fun LoginTabContent(
    screenState: AuthScreenState,
    onLoginClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf("") }
    val isButtonEnabled = screenState !is AuthScreenState.Loading
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        AuthTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = if (!isEmailValid(it)) "Введите корректную электронную почту" else ""
            },
            label = stringResource(R.string.auth_email),
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Email,
            isError = emailError.isNotEmpty(),
            onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
        )
        if (emailError.isNotEmpty()) {
            Text(
                text = emailError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))

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
                onLoginClick(email, password)
            }
        )

        Spacer(modifier = Modifier.height(43.dp))

        ButtonWithIcon(
            enabled = isButtonEnabled,
            onClick = {
                focusManager.clearFocus()
                onLoginClick(email, password)
            }
        )

        Spacer(modifier = Modifier.height(79.dp))
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthTextTabs(
    state: Int,
    onStateChange: (Int) -> Unit,
    titles: List<String>,
    modifier: Modifier = Modifier,
) {
    PrimaryTabRow(
        selectedTabIndex = state,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedTabIndex = state),
                width = Dp.Unspecified,
            )
        },
        modifier = modifier
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                selected = state == index,
                onClick = { onStateChange(index) },
                text = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}

@Composable
private fun AuthScreenStateContent(screenState: AuthScreenState) {
    when (screenState) {
        is AuthScreenState.Loading -> {
            CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
        }

        is AuthScreenState.Error -> {
            Text(
                text = screenState.message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
            )
        }

        else -> {}
    }
}