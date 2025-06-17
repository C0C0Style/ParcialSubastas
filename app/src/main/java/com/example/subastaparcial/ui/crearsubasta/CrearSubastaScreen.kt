package com.example.subastaparcial.ui.crearsubasta

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.subastaparcial.data.model.Subasta
import com.example.subastaparcial.data.repository.SubastaRepository
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CrearSubastaScreen(navController: NavController) {
    val context = LocalContext.current
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    val scope = rememberCoroutineScope()
    val repository = remember { SubastaRepository() }

    var nombre by remember { mutableStateOf("") }
    var ofertaMinima by remember { mutableStateOf("") }
    var fechaSubasta by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de la Subasta") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = ofertaMinima,
            onValueChange = { ofertaMinima = it },
            label = { Text("Oferta Mínima") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        calendar.set(year, month, day)
                        fechaSubasta = sdf.format(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        ) {
            Text(text = if (fechaSubasta.isBlank()) "Seleccionar fecha" else fechaSubasta)
        }

        Button(onClick = { launcher.launch("image/*") }) {
            Text("Seleccionar Imagen")
        }

        imageUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(150.dp)
            )
        }

        Button(onClick = {
            if (nombre.isBlank() || ofertaMinima.isBlank() || fechaSubasta.isBlank() || imageUri == null) {
                Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@Button
            }

            val imagenBase64 = encodeImageToBase64(context, imageUri!!)
            val subasta = Subasta(
                id = 0,
                nombre = nombre,
                ofertaMinima = ofertaMinima.toDouble(),
                fechaSubasta = fechaSubasta,
                imagen = imagenBase64
            )

            scope.launch {
                val exito = repository.crearSubasta(subasta)
                if (exito) {
                    Toast.makeText(context, "Subasta creada", Toast.LENGTH_SHORT).show()
                    navController.navigate("subastasList") {
                        popUpTo("subastasList") { inclusive = true }
                    }
                } else {
                    Toast.makeText(context, "Error al crear subasta", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Crear Subasta")
        }
    }
}

fun encodeImageToBase64(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Redimensionar la imagen a un tamaño razonable (por ejemplo, 600px de ancho máx)
        val scaledBitmap = Bitmap.createScaledBitmap(
            originalBitmap,
            600,
            (originalBitmap.height * (600.0 / originalBitmap.width)).toInt(),
            true
        )

        val outputStream = ByteArrayOutputStream()
        // Comprimir la imagen a JPEG con calidad del 75%
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)

        val compressedBytes = outputStream.toByteArray()
        val base64 = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)

        "data:image/jpeg;base64,$base64"
    } catch (e: Exception) {
        ""
    }
}

