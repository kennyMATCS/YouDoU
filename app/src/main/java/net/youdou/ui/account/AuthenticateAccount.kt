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
                .width(IntrinsicSize.Max)
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
                            .align(Alignment.CenterHorizontally)
                    )

                    AuthenticateButton(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.sign_in_text),
                        onClick = {
                            navigate()
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
                .width(IntrinsicSize.Max)
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
                            .align(Alignment.CenterHorizontally)
                    )

                    AuthenticateButton(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        text = stringResource(R.string.sign_up_text),
                        onClick = {
                            navigate()
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
fun AuthenticateSignInForm(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .padding(10.dp),
        color = Color.Transparent,
    ) {
        val username = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }

        val usernameLabel = stringResource(R.string.place_holder_username_sign_in_field)
        val passwordLabel = stringResource(R.string.place_holder_password_field)

        val usernameMaxLength = integerResource(R.integer.username_max_length)
        val passwordMaxLength = integerResource(R.integer.password_max_length)

        val passwordMinLength = integerResource(R.integer.password_min_length)

        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            content = {
                AuthenticateTextField(
                    state = username, contentType = ContentType.EmailAddress,
                    label = usernameLabel, maxLength = usernameMaxLength,
                    visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isPassword = false
                )
                AuthenticateTextField(
                    state = password, contentType = ContentType.Password,
                    label = passwordLabel,
                    minLength = passwordMinLength,
                    maxLength = passwordMaxLength,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isPassword = true
                )
            },
        )
    }
}

@Composable
fun AuthenticateSignUpForm(modifier: Modifier) {
    Surface(
        modifier = modifier
            .padding(10.dp),
        color = Color.Transparent
    ) {
        val username = remember { mutableStateOf("") }
        val phoneNumber = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val confirmPassword = remember { mutableStateOf("") }

        val usernameLabel = stringResource(R.string.place_holder_username_sign_up_field)
        val phoneNumberLabel = stringResource(R.string.place_holder_phone_number_field)
        val passwordLabel = stringResource(R.string.place_holder_password_field)
        val confirmPasswordLabel = stringResource(R.string.place_holder_password_confirm_field)

        val usernameMaxLength = integerResource(R.integer.username_max_length)
        val passwordMaxLength = integerResource(R.integer.password_max_length)
        val phoneNumberMaxLength = integerResource(R.integer.phone_max_length)

        val passwordMinLength = integerResource(R.integer.password_min_length)
        val phoneNumberMinLength = integerResource(R.integer.phone_min_length)

        Column(
            modifier = Modifier
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            content = {
                AuthenticateTextField(
                    state = username, contentType = ContentType.NewUsername,
                    label = usernameLabel, maxLength = usernameMaxLength,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    visualTransformation = VisualTransformation.None,
                    isPassword = false
                )
                AuthenticateTextField(
                    state = phoneNumber, contentType = ContentType.PhoneNumber,
                    label = phoneNumberLabel, keyboardOptions = KeyboardOptions(
                        keyboardType =
                            KeyboardType.Number
                    ), visualTransformation = NanpVisualTransformation(),
                    minLength = phoneNumberMinLength, maxLength = phoneNumberMaxLength,
                    isPassword = false
                )
                AuthenticateTextField(
                    state = password,
                    contentType = ContentType.NewPassword,
                    label = passwordLabel,
                    minLength = passwordMinLength,
                    maxLength = passwordMaxLength,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isPassword = true
                )
                AuthenticateTextField(
                    state = confirmPassword,
                    contentType = ContentType.NewPassword,
                    label = confirmPasswordLabel,
                    minLength = passwordMinLength,
                    maxLength = passwordMaxLength,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isPassword = true
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
    maxLength: Int = -1, minLength: Int = -1, isPassword: Boolean
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = state.value,
        onValueChange = { value ->
            if (value.length <= maxLength) {
                state.value = value
            }
        },
        label = {
            Text(label)
        },
        modifier = Modifier
            .semantics { this.contentType = contentType }
            .width(IntrinsicSize.Min),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,

            ),
        singleLine = true,
        shape = MaterialTheme.shapes.large,
        visualTransformation = if (passwordVisible && isPassword) VisualTransformation.None else
            visualTransformation,
        keyboardOptions = keyboardOptions,
        supportingText = {
            if (maxLength != -1) {
                Text(
                    text = "${state.value.length} / $maxLength",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        },
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff

            val description = if (passwordVisible) "Hide password" else "Show password"

            if (isPassword) {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        },
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