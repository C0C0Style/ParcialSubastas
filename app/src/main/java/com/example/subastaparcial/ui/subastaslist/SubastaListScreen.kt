package com.example.subastaparcial.ui.subastaslist

import android.util.Base64
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.subastaparcial.viewmodel.SubastaListViewModel

@Composable
fun SubastaListScreen(
    navController: NavController,
    viewModel: SubastaListViewModel
) {
    val subastas by viewModel.subastas.collectAsState()
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.cargarSubastas()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Subastas",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search by name or date") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { /* filtro ya aplicado */ }) {
                Text("Search")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Encabezado tipo tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray)
                .padding(8.dp)
        ) {
            Text("Nombre", modifier = Modifier.weight(1f), fontSize = 14.sp)
            Text("Oferta Mínima", modifier = Modifier.weight(1f), fontSize = 14.sp)
            Text("Inscritos", modifier = Modifier.weight(1f), fontSize = 14.sp)
            Text("Fecha Subasta", modifier = Modifier.weight(1f), fontSize = 14.sp)
        }

        Divider()

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(subastas.filter {
                it.nombre.contains(searchText.text, ignoreCase = true) ||
                        it.fechaSubasta.contains(searchText.text, ignoreCase = true)
            }) { subasta ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.subastaSeleccionada = subasta
                            navController.navigate("detalleSubasta")
                        }
                        .border(1.dp, Color.LightGray)
                        .padding(8.dp)
                ) {
                    Text(subasta.nombre, modifier = Modifier.weight(1f))
                    Text("$${subasta.ofertaMinima}", modifier = Modifier.weight(1f))
                    Text("${subasta.inscritos}", modifier = Modifier.weight(1f))
                    Text(subasta.fechaSubasta, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("crearSubasta") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Nueva")
        }
    }
}
