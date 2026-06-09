package co.edu.cecar.smartbooks.view.publica

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.cecar.smartbooks.R
import co.edu.cecar.smartbooks.viewmodel.LoginViewModel

// IMPORTACIÓN DE TUS COLORES INSTITUCIONALES
import co.edu.cecar.smartbooks.ui.theme.RojoInstitucional
import co.edu.cecar.smartbooks.ui.theme.AzulOscuroCDI
import co.edu.cecar.smartbooks.ui.theme.BlancoFondo
import co.edu.cecar.smartbooks.ui.theme.GrisOscuroTexto
import co.edu.cecar.smartbooks.ui.theme.GrisMedioBordes

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onRecuperarPass: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212529))
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo_login),
            contentDescription = "Fondo de login",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.45f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cdi_logo),
                    contentDescription = "Logo CDI",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(60.dp)
                        .padding(bottom = 20.dp),
                    contentScale = ContentScale.Fit
                )

                FormTextField(
                    value = viewModel.correo,
                    onValueChange = { viewModel.onCorreoChange(it) },
                    label = "Correo electrónico",
                    leadingIconRes = Icons.Default.Email,
                    placeholder = "usuario@email.com"
                )

                var passwordVisible by remember { mutableStateOf(false) }
                FormPasswordField(
                    value = viewModel.contrasena,
                    onValueChange = { viewModel.onContrasenaChange(it) },
                    label = "Contraseña",
                    leadingIconRes = Icons.Default.Lock,
                    passwordVisible = passwordVisible,
                    onPasswordVisibleChange = { passwordVisible = !passwordVisible },
                    placeholder = "••••••••••••"
                )

                // COLUMNA CONTENEDORA QUE PONE UNO DEBAJO DEL OTRO ALINEADOS A LA IZQUIERDA
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // 1. Recordarme arriba
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.offset(x = (-12).dp)
                    ) {
                        Checkbox(
                            checked = viewModel.isRememberMeChecked,
                            onCheckedChange = { viewModel.onRememberMeChange(it) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = RojoInstitucional,
                                uncheckedColor = GrisMedioBordes
                            )
                        )
                        Text(
                            text = "Recordarme",
                            color = GrisOscuroTexto,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 2. ¿Olvidaste tu contraseña? abajo
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = RojoInstitucional,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier
                            .clickable { onRecuperarPass() }
                            .padding(horizontal = 2.dp, vertical = 4.dp)
                    )
                }

                AnimatedVisibility(
                    visible = viewModel.errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    viewModel.errorMessage?.let { msg ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFDF2F2))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = RojoInstitucional,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = msg,
                                color = RojoInstitucional,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                SubmitButton(
                    onClick = { viewModel.iniciarSesion(onSuccess = onLoginSuccess) },
                    isLoading = viewModel.isLoading,
                    text = "Ingresar"
                )
            }

            Text(
                text = "© 2026 SmartBook • CDI CECAR",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 16.dp)
            )
        }
    }
}

@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIconRes: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = label,
            color = GrisOscuroTexto,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color(0xFFADB5BD), style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(imageVector = leadingIconRes, contentDescription = null, tint = Color(0xFF6C757D), modifier = Modifier.size(20.dp)) },
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RojoInstitucional,
                unfocusedBorderColor = GrisMedioBordes,
                focusedContainerColor = BlancoFondo,
                unfocusedContainerColor = BlancoFondo
            )
        )
    }
}

@Composable
fun FormPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIconRes: androidx.compose.ui.graphics.vector.ImageVector,
    passwordVisible: Boolean,
    onPasswordVisibleChange: () -> Unit,
    placeholder: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = label,
            color = GrisOscuroTexto,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = Color(0xFFADB5BD), style = MaterialTheme.typography.bodyMedium) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(imageVector = leadingIconRes, contentDescription = null, tint = Color(0xFF6C757D), modifier = Modifier.size(20.dp)) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = onPasswordVisibleChange) {
                    Icon(imageVector = image, contentDescription = null, tint = Color(0xFF6C757D), modifier = Modifier.size(20.dp))
                }
            },
            shape = RoundedCornerShape(8.dp),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RojoInstitucional,
                unfocusedBorderColor = GrisMedioBordes,
                focusedContainerColor = BlancoFondo,
                unfocusedContainerColor = BlancoFondo
            )
        )
    }
}

@Composable
fun SubmitButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = RojoInstitucional, contentColor = BlancoFondo),
        shape = RoundedCornerShape(8.dp),
        enabled = !isLoading,
        contentPadding = PaddingValues(0.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(22.dp), color = BlancoFondo, strokeWidth = 2.5.dp)
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}