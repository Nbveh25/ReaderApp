package ru.kazan.itis.bikmukhametov.feature.auth.presentation.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppOutlinedButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppPasswordTextField
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppPrimaryButton
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTextField
import ru.kazan.itis.bikmukhametov.core.ui.appuicomponent.AppTopBar
import ru.kazan.itis.bikmukhametov.impl.R
import java.util.regex.Pattern

@Composable
fun AuthScreen(
    onNavigateToBooks: () -> Unit = {},
    onNavigateToRegistration: () -> Unit = {}
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                AuthEffect.NavigateToBooks -> onNavigateToBooks()
                AuthEffect.NavigateToRegistration -> {
                    onNavigateToRegistration()
                }

                AuthEffect.StartGoogleSignInFlow -> {

                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AppTopBar(
                title = stringResource(R.string.auth_title),
                endContent = {
                    TextButton(
                        onClick = {
                            viewModel.onIntent(AuthIntent.RegistrationButtonClicked)
                        }
                    ) {
                        Text(stringResource(R.string.auth_registration_action))
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // header
            Text(
                text = stringResource(R.string.auth_brand_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )


            // login form
            AuthForm(
                state = uiState,
                onIntent = viewModel::onIntent,
                focusManager = focusManager
            )

        }
    }
}

@Composable
private fun AuthForm(
    state: AuthState,
    onIntent: (AuthIntent) -> Unit,
    focusManager: FocusManager
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$",
        Pattern.CASE_INSENSITIVE
    )
    val isEmailInvalid = state.emailInput.isNotBlank() &&
            !emailPattern.matcher(state.emailInput).matches()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // email
        AppTextField(
            value = state.emailInput,
            onValueChange = { onIntent(AuthIntent.EmailChanged(it)) },
            label = stringResource(R.string.email_label),
            isError = isEmailInvalid,
            supportingText = if (isEmailInvalid) {
                {
                    Text(
                        text = stringResource(R.string.error_invalid_email),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else null,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Email
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        // password
        AppPasswordTextField(
            value = state.passwordInput,
            onValueChange = { onIntent(AuthIntent.PasswordChanged(it)) },
            label = stringResource(R.string.password_label),
            isPasswordVisible = state.isPasswordVisible,
            onToggleVisibility = { onIntent(AuthIntent.TogglePasswordVisibility) },
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { errorText ->
                {
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onIntent(AuthIntent.LoginButtonClicked)
                }
            )
        )

        // Remember Me checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.rememberMe,
                onCheckedChange = { onIntent(AuthIntent.RememberMeChanged(it)) }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.auth_remember_me),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // login button
        AppPrimaryButton(
            text = stringResource(R.string.auth_primary_button),
            onClick = { onIntent(AuthIntent.LoginButtonClicked) },
            enabled = state.isButtonEnabled && !state.isLoading,
            isLoading = state.isLoading,
            modifier = Modifier.fillMaxWidth()
        )

        // Retry button (shown on network error)
        AnimatedVisibility(
            visible = state.isNetworkError && !state.isLoading,
            enter = fadeIn() + slideInVertically()
        ) {
            AppOutlinedButton(
                text = stringResource(R.string.auth_retry_button),
                onClick = { onIntent(AuthIntent.RetryButtonClicked) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // oauth button
        AnimatedVisibility(
            visible = !state.isLoading,
            enter = fadeIn() + slideInVertically()
        ) {
            AppOutlinedButton(
                text = stringResource(R.string.auth_google_button),
                onClick = { onIntent(AuthIntent.GoogleSignInButtonClicked) },
                modifier = Modifier.fillMaxWidth(),
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google_32),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
        }
    }
}

