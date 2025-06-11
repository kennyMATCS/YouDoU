package net.youdou.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.youdou.R
import net.youdou.YouDoUTopText

const val TITLE_SCALE = 1.5f

@Composable
fun AccountStartPage(navigateSignIn: () -> Unit, navigateSignUp: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
        ) {
            YouDoUTopText(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 20.dp)
                    .scale(TITLE_SCALE)
            )
            AuthenticateButton(
                text = stringResource(R.string.sign_in_text),
                onClick = {
                    navigateSignIn()
                },
                color = MaterialTheme.colorScheme.primary,
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            AuthenticateButton(
                text = stringResource(R.string.sign_up_text),
                onClick = {
                    navigateSignUp()
                },
                color = MaterialTheme.colorScheme.secondary,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
    }
}

@Composable
fun AccountSignInPage(navigate: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Min)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                YouDoUTopText(
                    modifier = Modifier
                        .padding(bottom = 10.dp, end = 10.dp)
                )

                BigText(
                    text = stringResource(R.string.sign_in_text),
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            val usernameError = remember { mutableStateOf(false) }
            val passwordError = remember { mutableStateOf(false) }


            val usernameErrorMessage = stringResource(R.string.username_invalid_message)
            val passwordErrorMessage = stringResource(R.string.password_invalid_message)

            val username = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }

            val usernameMaxLength = integerResource(R.integer.username_max_length)
            val passwordMaxLength = integerResource(R.integer.password_max_length)

            val usernameMinLength = integerResource(R.integer.username_min_length)
            val passwordMinLength = integerResource(R.integer.password_min_length)

            Surface(
                shadowElevation = 3.dp,
                tonalElevation = 3.dp,
                shape = MaterialTheme.shapes.large,
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    AuthenticateSignInForm(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        usernameError = usernameError,
                        passwordError = passwordError,
                        username = username,
                        password = password,
                        usernameErrorMessage = usernameErrorMessage,
                        passwordErrorMessage = passwordErrorMessage
                    )

                    AuthenticateButton(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.sign_in_text),
                        onClick = {
                            usernameError.value = when (username.value.length) {
                                in usernameMinLength..usernameMaxLength -> false
                                else -> true
                            }

                            passwordError.value = when (password.value.length) {
                                in passwordMinLength..passwordMaxLength -> false
                                else -> true
                            }

                            if (!(usernameError.value || passwordError.value)) {
                                navigate()
                            }
                        },
                        color = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun AccountSignUpPage(navigate: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Min)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                YouDoUTopText(
                    modifier = Modifier
                        .padding(bottom = 10.dp, end = 10.dp)
                )

                BigText(
                    text = stringResource(R.string.sign_up_text),
                    modifier = Modifier
                        .padding(bottom = 10.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            val usernameMaxLength = integerResource(R.integer.username_max_length)
            val passwordMaxLength = integerResource(R.integer.password_max_length)
            val phoneNumberMaxLength = integerResource(R.integer.phone_max_length)

            val usernameMinLength = integerResource(R.integer.username_min_length)
            val passwordMinLength = integerResource(R.integer.password_min_length)
            val phoneNumberMinLength = integerResource(R.integer.phone_min_length)

            val usernameError = remember { mutableStateOf(false) }
            val phoneNumberError = remember { mutableStateOf(false) }
            val passwordError = remember { mutableStateOf(false) }
            val confirmPasswordError = remember { mutableStateOf(false) }

            val usernameErrorMessage = stringResource(R.string.username_invalid_message)
            val phoneNumberErrorMessage = stringResource(R.string.phone_number_invalid_message)
            val passwordErrorMessage = stringResource(R.string.password_invalid_message)
            val confirmPasswordErrorMessage = stringResource(
                R.string
                    .confirm_password_invalid_message
            )

            val username = remember { mutableStateOf("") }
            val phoneNumber = remember { mutableStateOf("") }
            val password = remember { mutableStateOf("") }
            val confirmPassword = remember { mutableStateOf("") }

            Surface(
                shadowElevation = 3.dp,
                tonalElevation = 3.dp,
                shape = MaterialTheme.shapes.large,
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    AuthenticateSignUpForm(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        usernameError = usernameError,
                        phoneNumberError = phoneNumberError,
                        passwordError = passwordError,
                        confirmPasswordError = confirmPasswordError,
                        username = username,
                        phoneNumber = phoneNumber,
                        password = password,
                        confirmPassword = confirmPassword,
                        usernameErrorMessage = usernameErrorMessage,
                        passwordErrorMessage = passwordErrorMessage,
                        phoneNumberErrorMessage = phoneNumberErrorMessage,
                        confirmPasswordErrorMessage = confirmPasswordErrorMessage
                    )

                    AuthenticateButton(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.sign_up_text),
                        onClick = {
                            usernameError.value = when (username.value.length) {
                                in usernameMinLength..usernameMaxLength -> false
                                else -> true
                            }

                            passwordError.value = when (password.value.length) {
                                in passwordMinLength..passwordMaxLength -> false
                                else -> true
                            }

                            confirmPasswordError.value = (confirmPassword.value != password.value
                                    || confirmPassword.value.isEmpty())

                            phoneNumberError.value = when (phoneNumber.value.length) {
                                in phoneNumberMinLength..phoneNumberMaxLength -> false
                                else -> true
                            }

                            if (!(usernameError.value || passwordError.value ||
                                        confirmPasswordError.value || phoneNumberError.value)
                            ) {
                                navigate()
                            }
                        },
                        color = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun AuthenticateButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        content = {
            Text(
                text = text,
                color = color,
            )
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        contentPadding = PaddingValues(horizontal = 128.dp),
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 10.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 5.dp
        )
    )
}

@Composable
fun AuthenticateSignInForm(
    modifier: Modifier = Modifier,
    usernameError: MutableState<Boolean>,
    passwordError: MutableState<Boolean>,
    usernameErrorMessage: String,
    passwordErrorMessage: String,
    username: MutableState<String>,
    password: MutableState<String>
) {
    Surface(
        modifier = modifier
            .padding(6.dp),
        color = Color.Transparent,
    ) {
        val usernameLabel = stringResource(R.string.place_holder_username_sign_in_field)
        val passwordLabel = stringResource(R.string.place_holder_password_field)

        val usernameMaxLength = integerResource(R.integer.username_max_length)
        val passwordMaxLength = integerResource(R.integer.password_max_length)

        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            content = {
                AuthenticateTextField(
                    state = username, contentType = ContentType.Username,
                    label = usernameLabel, visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions.Default,
                    maxLength = usernameMaxLength,
                    isPassword = false,
                    error = usernameError,
                    errorMessage = usernameErrorMessage
                )
                AuthenticateTextField(
                    state = password, contentType = ContentType.Password,
                    label = passwordLabel,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    maxLength = passwordMaxLength,
                    isPassword = true,
                    error = passwordError,
                    errorMessage = passwordErrorMessage
                )
            },
        )
    }
}

@Composable
fun AuthenticateSignUpForm(
    modifier: Modifier,
    usernameError: MutableState<Boolean>,
    phoneNumberError: MutableState<Boolean>,
    passwordError: MutableState<Boolean>,
    confirmPasswordError: MutableState<Boolean>,
    usernameErrorMessage: String,
    phoneNumberErrorMessage: String,
    passwordErrorMessage: String,
    confirmPasswordErrorMessage: String,
    username: MutableState<String>,
    phoneNumber: MutableState<String>,
    password: MutableState<String>,
    confirmPassword: MutableState<String>
) {
    Surface(
        modifier = modifier
            .padding(6.dp),
        color = Color.Transparent
    ) {
        val usernameLabel = stringResource(R.string.place_holder_username_sign_up_field)
        val phoneNumberLabel = stringResource(R.string.place_holder_phone_number_field)
        val passwordLabel = stringResource(R.string.place_holder_password_field)
        val confirmPasswordLabel = stringResource(R.string.place_holder_password_confirm_field)

        val usernameMaxLength = integerResource(R.integer.username_max_length)
        val passwordMaxLength = integerResource(R.integer.password_max_length)
        val phoneNumberMaxLength = integerResource(R.integer.phone_max_length)

        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            content = {
                AuthenticateTextField(
                    state = username, contentType = ContentType.NewUsername,
                    label = usernameLabel, visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions.Default,
                    maxLength = usernameMaxLength,
                    isPassword = false,
                    error = usernameError,
                    errorMessage = usernameErrorMessage
                )
                AuthenticateTextField(
                    state = phoneNumber,
                    contentType = ContentType.PhoneNumber,
                    label = phoneNumberLabel,
                    visualTransformation = NanpVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ),
                    maxLength = phoneNumberMaxLength,
                    isPassword = false,
                    error = phoneNumberError,
                    errorMessage = phoneNumberErrorMessage
                )
                AuthenticateTextField(
                    state = password,
                    contentType = ContentType.NewPassword,
                    label = passwordLabel,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    maxLength = passwordMaxLength,
                    isPassword = true,
                    error = passwordError,
                    errorMessage = passwordErrorMessage
                )
                AuthenticateTextField(
                    state = confirmPassword,
                    contentType = ContentType.NewPassword,
                    label = confirmPasswordLabel,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    maxLength = passwordMaxLength,
                    isPassword = true,
                    error = confirmPasswordError,
                    errorMessage = confirmPasswordErrorMessage
                )
            },
        )
    }
}

@Composable
private fun AuthenticateTextField(
    state: MutableState<String>, contentType: ContentType, label: String,
    visualTransformation: VisualTransformation,
    keyboardOptions: KeyboardOptions,
    maxLength: Int = -1, isPassword: Boolean,
    error: MutableState<Boolean>,
    errorMessage: String,
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = state.value,
        onValueChange = { value ->
            if (value.length <= maxLength && !value.isEmpty() && value.last() != ' ') {
                state.value = value
            }
        },
        label = {
            Text(
                text = label,
            )
        },
        modifier = Modifier
            .semantics { this.contentType = contentType }
            .width(300.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        visualTransformation = if (passwordVisible && isPassword) VisualTransformation.None else
            visualTransformation,
        keyboardOptions = keyboardOptions,
        supportingText = {
            if (error.value) {
                Text(
                    text = errorMessage,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        },
        trailingIcon = if (isPassword) {
            {
                val image =
                    if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        } else {
            null
        },
        isError = error.value
    )
}

@Composable
fun BigText(modifier: Modifier = Modifier, text: String, color: Color) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.headlineLarge,
        modifier = modifier
    )
}

private class NanpVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text

        var out = if (trimmed.isNotEmpty()) "(" else ""

        for (i in trimmed.indices) {
            if (i == 3) out += ") "
            if (i == 6) out += "-"
            out += trimmed[i]
        }
        return TransformedText(AnnotatedString(out), phoneNumberOffsetTranslator)
    }

    private val phoneNumberOffsetTranslator = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int =
            when (offset) {
                0 -> offset
                // Add 1 for opening parenthesis.
                in 1..3 -> offset + 1
                // Add 3 for both parentheses and a space.
                in 4..6 -> offset + 3
                // Add 4 for both parentheses, space, and hyphen.
                else -> offset + 4
            }

        override fun transformedToOriginal(offset: Int): Int =
            when (offset) {
                0 -> offset
                // Subtract 1 for opening parenthesis.
                in 1..5 -> offset - 1
                // Subtract 3 for both parentheses and a space.
                in 6..10 -> offset - 3
                // Subtract 4 for both parentheses, space, and hyphen.
                else -> offset - 4
            }
    }
}

@Preview
@Composable
fun PreviewAccountStartPage() {
    AccountStartPage({ }, { })
}

@Preview
@Composable
fun PreviewAccountSignIn() {
    AccountSignInPage({ })
}

@Preview
@Composable
fun PreviewAccountSignUp() {
    AccountSignUpPage({ })
}