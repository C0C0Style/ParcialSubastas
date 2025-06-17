package com.example.subastaparcial.ui.subastadetalle

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.subastaparcial.data.model.Subasta

@Composable
fun DetalleSubastaScreen(subasta: Subasta, onBack: () -> Unit = {}) {
    val context = LocalContext.current

    var numeroSeleccionado by remember { mutableStateOf<Int?>(null) }
    var valorPuja by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Detalle de Subasta", style = MaterialTheme.typography.headlineSmall)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.LightGray)
                .padding(12.dp)
        ) {
            Text("Nombre: ${subasta.nombre}")
            Text("Fecha: ${subasta.fechaSubasta}")
            Text("Oferta mínima: $${subasta.ofertaMinima}")
            Text("Inscritos: ${subasta.inscritos}")
        }

        subasta.imagen?.let {
            decodeBase64ToBitmap(it)?.let { img ->
                Image(
                    bitmap = img,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                )
            }
        }

        Text("Selecciona un número disponible", style = MaterialTheme.typography.titleSmall)

        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items((1..100).toList()) { numero ->
                val seleccionado = numero == numeroSeleccionado
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .border(
                            1.dp,
                            if (seleccionado) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        .clickable { numeroSeleccionado = numero },
                    contentAlignment = Alignment.Center
                ) {
                    Text("$numero", fontSize = 12.sp)
                }
            }
        }

        OutlinedTextField(
            value = valorPuja,
            onValueChange = { valorPuja = it },
            label = { Text("Valor de Puja") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (numeroSeleccionado == null || valorPuja.isBlank()) {
                    Toast.makeText(context, "Completa los campos", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "Puja guardada para número $numeroSeleccionado",
                        Toast.LENGTH_SHORT
                    ).show()
                    // lógica para guardar
                }
            }) {
                Text("Guardar")
            }

            Button(onClick = {
                Toast.makeText(context, "Subasta finalizada", Toast.LENGTH_SHORT).show()
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))) {
                Text("Finalizar")
            }

            Button(onClick = {
                Toast.makeText(context, "Subasta eliminada", Toast.LENGTH_SHORT).show()
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))) {
                Text("Eliminar")
            }
        }

        TextButton(onClick = onBack) {
            Text("← Volver")
        }
    }
}

fun decodeBase64ToBitmap(base64: String): ImageBitmap? {
    return try {
        val cleanBase64 = base64.substringAfter(",")
        val bytes = Base64.decode(cleanBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}
