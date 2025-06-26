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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.subastaparcial.data.model.Puja
import com.example.subastaparcial.data.model.Subasta
import com.example.subastaparcial.data.repository.SubastaRepository
import com.example.subastaparcial.viewmodel.PujaViewModel

@Composable
fun DetalleSubastaScreen(
    subasta: Subasta,
    onBack: () -> Unit = {},
    viewModel: PujaViewModel = viewModel()
) {
    val context = LocalContext.current

    var subastaActual by remember { mutableStateOf(subasta) }


    var numeroSeleccionado by remember { mutableStateOf<Int?>(null) }
    var valorPuja by remember { mutableStateOf("") }
    var finalizada by remember { mutableStateOf(false) }
    var mostrarDialogoGanador by remember { mutableStateOf(false) }
    var mensajeGanador by remember { mutableStateOf("") }



    val estadoPuja by viewModel.estadoPuja.collectAsState()
    val pujas by viewModel.pujasExistentes.collectAsState()
    val estadoEliminacion by viewModel.estadoEliminacion.collectAsState()


    // Cargar las pujas existentes al iniciar
    LaunchedEffect(subasta.id) {
        viewModel.cargarPujas(subasta.id)
    }

    // Mostrar notificaciones según resultado de envío
    LaunchedEffect(estadoPuja) {
        estadoPuja?.let {
            it.onSuccess {
                Toast.makeText(context, "✅ Puja enviada correctamente", Toast.LENGTH_SHORT).show()
                viewModel.cargarPujas(subasta.id) // Recargar lista

                // 👉 Recargar la subasta para obtener el nuevo valor de 'inscritos'
                val subastaRepository = SubastaRepository() // O injecta si usas Hilt
                val actualizada = subastaRepository.obtenerSubastaPorId(subasta.id)
                actualizada?.let { subastaNueva ->
                    subastaActual = subastaNueva
                }
            }.onFailure { error ->
                Toast.makeText(context, "❌ Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(estadoEliminacion) {
        estadoEliminacion?.let {
            it.onSuccess {
                Toast.makeText(context, "✅ Puja eliminada correctamente", Toast.LENGTH_SHORT).show()
                numeroSeleccionado = null
                valorPuja = ""
                // También puedes actualizar la subasta si quieres
                val subastaRepository = SubastaRepository()
                val actualizada = subastaRepository.obtenerSubastaPorId(subasta.id)
                actualizada?.let { subastaNueva ->
                    subastaActual = subastaNueva
                }
            }.onFailure { error ->
                Toast.makeText(context, "❌ Error al eliminar: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
            Text("Nombre: ${subastaActual.nombre}")
            Text("Fecha: ${subastaActual.fechaSubasta}")
            Text("Oferta mínima: $${subastaActual.ofertaMinima}")
            Text("Inscritos: ${subastaActual.inscritos}")
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
                .heightIn(max = 260.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items((1..100).toList()) { numero ->
                val pujaExistente = pujas.find { it.numero == numero }
                val seleccionado = numero == numeroSeleccionado
                val color = when {
                    seleccionado -> MaterialTheme.colorScheme.primary
                    pujaExistente != null -> Color.Red
                    else -> Color.Gray
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .border(1.dp, color)
                        .clickable(enabled = !finalizada) {
                            numeroSeleccionado = numero
                            valorPuja = pujaExistente?.valor ?: ""},
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$numero",
                        fontSize = 12.sp,
                        color = color
                    )
                }
            }
        }

        OutlinedTextField(
            value = valorPuja,
            onValueChange = { valorPuja = it },
            label = { Text("Valor de Puja") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !finalizada
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                if (numeroSeleccionado == null || valorPuja.isBlank()) {
                    Toast.makeText(context, "⚠️ Completa los campos", Toast.LENGTH_SHORT).show()
                } else {
                    val nuevaPuja = Puja(
                        subastaId = subasta.id,
                        numero = numeroSeleccionado!!,
                        valor = valorPuja
                    )
                    viewModel.guardarPujaDetectando(nuevaPuja)
                }
            }) {
                Text("Guardar")
            }

            Button(onClick = {
                if (pujas.isEmpty()) {
                    Toast.makeText(context, "❌ No hay pujas aún", Toast.LENGTH_SHORT).show()
                } else {
                    finalizada = true
                    val pujaGanadora = pujas.maxByOrNull { it.valor.toDoubleOrNull() ?: 0.0 }
                    pujaGanadora?.let {
                        mensajeGanador = "🎉 Felicidades, el ganador es el número ${it.numero} con el valor de $${it.valor}"
                        mostrarDialogoGanador = true
                    }
                }
            }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))) {
                Text("Finalizar")
            }

            if (numeroSeleccionado != null && pujas.any { it.numero == numeroSeleccionado }) {
                Button(
                    onClick = {
                        viewModel.eliminarPuja(subasta.id, numeroSeleccionado!!)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Eliminar")
                }
            }
        }

        if (mostrarDialogoGanador) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoGanador = false },
                confirmButton = {
                    Button(onClick = { mostrarDialogoGanador = false }) {
                        Text("Cerrar")
                    }
                },
                title = { Text("Ganador de la subasta") },
                text = { Text(mensajeGanador) }
            )
        }


        /*TextButton(onClick = onBack) {
            Text("← Volver")
        }*/
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
