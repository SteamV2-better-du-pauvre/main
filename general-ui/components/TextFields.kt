package ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ui.theme.AppColors

/**
 * Custom text field with consistent styling
 * 
 * @param value Current text value
 * @param onValueChange Callback when value changes
 * @param label Label text
 * @param modifier Optional modifier
 * @param placeholder Optional placeholder text
 * @param enabled Whether the field is enabled
 * @param readOnly Whether the field is read-only
 * @param isError Whether to show error state
 * @param errorMessage Optional error message to display
 * @param singleLine Whether to limit to single line
 * @param maxLines Maximum number of lines (if not single line)
 * @param keyboardType Type of keyboard to show
 * @param imeAction IME action button
 * @param onImeAction Callback for IME action
 * @param leadingIcon Optional leading icon
 * @param trailingIcon Optional trailing icon
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = if (placeholder != null) {
                { Text(placeholder) }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            isError = isError,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = { onImeAction?.invoke() },
                onGo = { onImeAction?.invoke() },
                onSearch = { onImeAction?.invoke() },
                onSend = { onImeAction?.invoke() }
            ),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            interactionSource = interactionSource,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AppColors.TextPrimary,
                unfocusedTextColor = AppColors.TextPrimary,
                disabledTextColor = AppColors.TextSecondary,
                errorTextColor = AppColors.TextPrimary,
                
                focusedContainerColor = AppColors.Surface,
                unfocusedContainerColor = AppColors.Surface,
                disabledContainerColor = AppColors.Surface.copy(alpha = 0.5f),
                errorContainerColor = AppColors.Surface,
                
                focusedBorderColor = AppColors.AccentPrimary,
                unfocusedBorderColor = AppColors.Border,
                disabledBorderColor = AppColors.Border.copy(alpha = 0.5f),
                errorBorderColor = AppColors.Error,
                
                focusedLabelColor = AppColors.AccentPrimary,
                unfocusedLabelColor = AppColors.TextSecondary,
                disabledLabelColor = AppColors.TextSecondary.copy(alpha = 0.5f),
                errorLabelColor = AppColors.Error,
                
                focusedPlaceholderColor = AppColors.TextSecondary,
                unfocusedPlaceholderColor = AppColors.TextSecondary,
                disabledPlaceholderColor = AppColors.TextSecondary.copy(alpha = 0.5f),
                errorPlaceholderColor = AppColors.TextSecondary,
                
                cursorColor = AppColors.AccentPrimary,
                errorCursorColor = AppColors.Error
            ),
            shape = MaterialTheme.shapes.small
        )
        
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = AppColors.Error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

/**
 * Password field with visibility toggle
 * 
 * @param value Current password value
 * @param onValueChange Callback when value changes
 * @param label Label text
 * @param modifier Optional modifier
 * @param placeholder Optional placeholder text
 * @param enabled Whether the field is enabled
 * @param isError Whether to show error state
 * @param errorMessage Optional error message to display
 * @param imeAction IME action button
 * @param onImeAction Callback for IME action
 */
@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: (() -> Unit)? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        enabled = enabled,
        isError = isError,
        errorMessage = errorMessage,
        singleLine = true,
        keyboardType = KeyboardType.Password,
        imeAction = imeAction,
        onImeAction = onImeAction,
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                    tint = AppColors.TextSecondary
                )
            }
        }
    )
}

/**
 * Multiline text field for longer content
 * 
 * @param value Current text value
 * @param onValueChange Callback when value changes
 * @param label Label text
 * @param modifier Optional modifier
 * @param placeholder Optional placeholder text
 * @param enabled Whether the field is enabled
 * @param minLines Minimum number of lines to display
 * @param maxLines Maximum number of lines
 */
@Composable
fun MultilineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    enabled: Boolean = true,
    minLines: Int = 3,
    maxLines: Int = 6
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier.heightIn(min = (minLines * 24).dp),
        placeholder = placeholder,
        enabled = enabled,
        singleLine = false,
        maxLines = maxLines,
        keyboardType = KeyboardType.Text,
        imeAction = ImeAction.Default
    )
}
