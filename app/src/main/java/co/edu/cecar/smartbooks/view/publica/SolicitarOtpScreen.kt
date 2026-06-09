package co.edu.cecar.smartbooks.view.publica

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun SolicitarOtpScreen(
    viewModel: LoginViewModel,
    onCodigoEnviado: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var correoRecuperacion by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

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
                        .padding(bottom = 12.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Recuperar Contraseña",
                    color = Color(0xFF212529),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                FormTextField(
                    value = correoRecuperacion,
                    onValueChange = { correoRecuperacion = it },
                    label = "Correo Institucional",
                    leadingIconRes = Icons.Default.Email,
                    placeholder = "usuario@email.com"
                )

                Spacer(modifier = Modifier.height(8.dp))

                SubmitButton(
                    onClick = {
                        if (correoRecuperacion.isNotBlank()) {
                            viewModel.solicitarCodigo(correoRecuperacion.trim()) {
                                onCodigoEnviado()
                            }
                        } else {
                            Toast.makeText(context, "Ingrese su correo electrónico", Toast.LENGTH_SHORT).show()
                        }
                    },
                    isLoading = viewModel.isLoading,
                    text = "Enviar Código OTP"
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = onVolver) {
                    Text(text = "Volver al Login", color = Color(0xFF6C757D), fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = "© 2026 SmartBook • CDI CECAR",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}