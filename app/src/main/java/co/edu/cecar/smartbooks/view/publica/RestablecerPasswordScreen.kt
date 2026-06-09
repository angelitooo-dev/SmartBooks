package co.edu.cecar.smartbooks.view.publica

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.cecar.smartbooks.R
import co.edu.cecar.smartbooks.viewmodel.LoginViewModel

@Composable
fun RestablecerPasswordScreen(
    viewModel: LoginViewModel,
    onPasswordCambiado: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var codigoOtp by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var confirmacionContrasena by remember { mutableStateOf("") }

    var passVisible1 by remember { mutableStateOf(false) }
    var passVisible2 by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF212529))
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo_login),
            contentDescription = "Fondo",
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
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 380.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cdi_logo),
                    contentDescription = "Logo CDI",
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(55.dp)
                        .padding(bottom = 12.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Establecer Contraseña",
                    color = Color(0xFF212529),
                    style = MaterialTheme.typography.titleLarge, // 🚀 Inter Bold
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
                    Text(
                        text = "Código de Seguridad (OTP)",
                        color = Color(0xFF343A40),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = codigoOtp,
                        onValueChange = { codigoOtp = it },
                        placeholder = { Text(text = "Ingrese el código recibido", color = Color(0xFFADB5BD), style = MaterialTheme.typography.bodyMedium) },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.Black),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFC0392B),
                            unfocusedBorderColor = Color(0xFFCED4DA),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                FormPasswordField(
                    value = nuevaContrasena,
                    onValueChange = { nuevaContrasena = it },
                    label = "Nueva Contraseña",
                    leadingIconRes = Icons.Default.Lock,
                    passwordVisible = passVisible1,
                    onPasswordVisibleChange = { passVisible1 = !passVisible1 },
                    placeholder = "••••••••••••"
                )

                FormPasswordField(
                    value = confirmacionContrasena,
                    onValueChange = { confirmacionContrasena = it },
                    label = "Confirmar Nueva Contraseña",
                    leadingIconRes = Icons.Default.Lock,
                    passwordVisible = passVisible2,
                    onPasswordVisibleChange = { passVisible2 = !passVisible2 },
                    placeholder = "••••••••••••"
                )

                Spacer(modifier = Modifier.height(12.dp))

                SubmitButton(
                    onClick = {
                        if (nuevaContrasena != confirmacionContrasena) {
                            Toast.makeText(context, "Error: Las contraseñas no coinciden.", Toast.LENGTH_LONG).show()
                        } else if (codigoOtp.isBlank() || nuevaContrasena.isBlank()) {
                            Toast.makeText(context, "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.restablecerContrasena(codigoOtp.trim(), nuevaContrasena) {
                                Toast.makeText(context, "Contraseña modificada con éxito.", Toast.LENGTH_SHORT).show()
                                onPasswordCambiado()
                            }
                        }
                    },
                    isLoading = viewModel.isLoading,
                    text = "Restablecer"
                )

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(onClick = onVolver) {
                    Text(
                        text = "Cancelar",
                        color = Color(0xFF6C757D),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
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