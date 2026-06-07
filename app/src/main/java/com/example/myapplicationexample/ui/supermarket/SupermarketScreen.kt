package com.example.myapplicationexample.ui.supermarket

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationexample.ComparadorApplication
import com.example.myapplicationexample.data.local.entity.Supermarket
import com.example.myapplicationexample.ui.theme.*
import com.example.myapplicationexample.ui.viewmodel.SupermarketViewModel
import com.example.myapplicationexample.ui.viewmodel.factory.SupermarketViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupermarketScreen(
    viewModel: SupermarketViewModel = viewModel(
        factory = SupermarketViewModelFactory(
            (LocalContext.current.applicationContext as ComparadorApplication).database.supermarketDao()
        )
    )
) {
    var supermarketName by remember { mutableStateOf("") }
    var supermarketAddress by remember { mutableStateOf("") }
    var editingSupermarket by remember { mutableStateOf<Supermarket?>(null) }
    
    val supermarkets by viewModel.supermarkets.collectAsState()

    LaunchedEffect(editingSupermarket) {
        if (editingSupermarket != null) {
            supermarketName = editingSupermarket!!.nombre
            supermarketAddress = editingSupermarket!!.direccion ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TIENDAS", color = NothingWhite, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingBlack)
            )
        },
        containerColor = NothingBlack
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = supermarketName,
                onValueChange = { supermarketName = it },
                label = { Text("Nombre de la tienda", color = NothingGrey) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = NothingWhite,
                    unfocusedTextColor = NothingWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = supermarketAddress,
                onValueChange = { supermarketAddress = it },
                label = { Text("Dirección (opcional)", color = NothingGrey) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = NothingWhite,
                    unfocusedTextColor = NothingWhite
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { 
                    if (editingSupermarket == null) {
                        viewModel.addSupermarket(supermarketName, supermarketAddress.ifBlank { null })
                    } else {
                        viewModel.updateSupermarket(editingSupermarket!!.copy(nombre = supermarketName, direccion = supermarketAddress.ifBlank { null }))
                        editingSupermarket = null
                    }
                    supermarketName = ""
                    supermarketAddress = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = NothingRed, contentColor = NothingWhite),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text(if (editingSupermarket == null) "AGREGAR" else "GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
            }
            
            if (editingSupermarket != null) {
                TextButton(onClick = {
                    editingSupermarket = null
                    supermarketName = ""
                    supermarketAddress = ""
                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Cancelar edición", color = NothingGrey)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text("LISTA DE TIENDAS", color = NothingGrey, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn {
                items(supermarkets.size) { index ->
                    val supermarket = supermarkets[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = NothingDarkGrey)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = supermarket.nombre, color = NothingWhite, fontWeight = FontWeight.Bold)
                                if (!supermarket.direccion.isNullOrBlank()) {
                                    Text(text = supermarket.direccion!!, color = NothingGrey, fontSize = 12.sp)
                                }
                            }
                            Row {
                                IconButton(onClick = { editingSupermarket = supermarket }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = NothingGrey)
                                }
                                IconButton(onClick = { viewModel.deleteSupermarket(supermarket) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = NothingGrey)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
