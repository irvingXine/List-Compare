package com.example.myapplicationexample.ui.price

import android.widget.Toast
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
import com.example.myapplicationexample.data.local.entity.PriceRecord
import com.example.myapplicationexample.data.local.entity.UnitType
import com.example.myapplicationexample.ui.viewmodel.PriceViewModel
import com.example.myapplicationexample.ui.viewmodel.factory.PriceViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceEntryScreen(
    viewModel: PriceViewModel = viewModel(
        factory = PriceViewModelFactory(
            (LocalContext.current.applicationContext as ComparadorApplication).database.priceRecordDao(),
            (LocalContext.current.applicationContext as ComparadorApplication).database.productDao(),
            (LocalContext.current.applicationContext as ComparadorApplication).database.supermarketDao()
        )
    )
) {
    val context = LocalContext.current
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val productSearchQuery by viewModel.productSearchQuery.collectAsState()
    val selectedProductId by viewModel.selectedProductId.collectAsState()
    val supermarkets by viewModel.supermarkets.collectAsState()
    val priceEntries by viewModel.priceEntries.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedSupermarketId by remember { mutableStateOf<Long?>(null) }
    var priceInput by remember { mutableStateOf("") }
    var cantidadInput by remember { mutableStateOf("1.0") }
    var notesInput by remember { mutableStateOf("") }
    var editingRecord by remember { mutableStateOf<PriceRecord?>(null) }
    
    var productDropdownExpanded by remember { mutableStateOf(false) }
    var supermarketExpanded by remember { mutableStateOf(false) }

    val selectedProduct = filteredProducts.find { it.id == selectedProductId }

    LaunchedEffect(editingRecord) {
        if (editingRecord != null) {
            priceInput = editingRecord!!.precio_ingresado.toString()
            cantidadInput = editingRecord!!.cantidad_unidad.toString()
            notesInput = editingRecord!!.notas ?: ""
            selectedSupermarketId = editingRecord!!.supermarket_id
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ASIGNAR PRECIOS", fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            
            // Product Search with Autocomplete
            ExposedDropdownMenuBox(
                expanded = productDropdownExpanded && filteredProducts.isNotEmpty(),
                onExpandedChange = { productDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = productSearchQuery,
                    onValueChange = { 
                        viewModel.onProductSearchQueryChange(it)
                        productDropdownExpanded = true
                    },
                    label = { Text("Buscar Producto...") },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    trailingIcon = {
                        if (selectedProductId != null) {
                            IconButton(onClick = { 
                                viewModel.selectProduct(null)
                                viewModel.onProductSearchQueryChange("")
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Limpiar", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                )
                
                ExposedDropdownMenu(
                    expanded = productDropdownExpanded && filteredProducts.isNotEmpty(),
                    onDismissRequest = { productDropdownExpanded = false }
                ) {
                    filteredProducts.forEach { product ->
                        DropdownMenuItem(
                            text = { Text(product.nombre) },
                            onClick = {
                                viewModel.selectProduct(product.id)
                                viewModel.onProductSearchQueryChange(product.nombre)
                                productDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Supermarket Dropdown
            ExposedDropdownMenuBox(
                expanded = supermarketExpanded,
                onExpandedChange = { supermarketExpanded = !supermarketExpanded }
            ) {
                OutlinedTextField(
                    value = supermarkets.find { it.id == selectedSupermarketId }?.nombre ?: "Seleccionar Tienda",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tienda") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supermarketExpanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                ExposedDropdownMenu(
                    expanded = supermarketExpanded,
                    onDismissRequest = { supermarketExpanded = false }
                ) {
                    supermarkets.forEach { supermarket ->
                        DropdownMenuItem(
                            text = { Text(supermarket.nombre + (if(supermarket.direccion != null) " - ${supermarket.direccion}" else "")) },
                            onClick = {
                                selectedSupermarketId = supermarket.id
                                supermarketExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = priceInput,
                    onValueChange = { priceInput = it },
                    label = { Text("Precio ($)") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                if (selectedProduct?.tipo_unidad == UnitType.KILOGRAMO || selectedProduct?.tipo_unidad == UnitType.LITRO) {
                    OutlinedTextField(
                        value = cantidadInput,
                        onValueChange = { cantidadInput = it },
                        label = { Text(if (selectedProduct.tipo_unidad == UnitType.KILOGRAMO) "Kg" else "Litros") },
                        modifier = Modifier.weight(0.6f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notesInput,
                onValueChange = { notesInput = it },
                label = { Text("Notas (específico de tienda)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val pId = selectedProductId
                    val sId = selectedSupermarketId
                    val price = priceInput.toDoubleOrNull()
                    val cantidad = cantidadInput.toDoubleOrNull() ?: 1.0
                    if (pId != null && sId != null && price != null) {
                        if (editingRecord == null) {
                            viewModel.addPrice(pId, sId, price, cantidad, notesInput.ifBlank { null })
                        } else {
                            viewModel.updatePrice(editingRecord!!.copy(
                                supermarket_id = sId, 
                                precio_ingresado = price,
                                cantidad_unidad = cantidad,
                                notas = notesInput.ifBlank { null },
                                fecha_registro = System.currentTimeMillis()
                            ))
                            editingRecord = null
                        }
                        priceInput = ""
                        cantidadInput = "1.0"
                        notesInput = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (editingRecord == null) "REGISTRAR PRECIO" else "GUARDAR CAMBIOS")
            }

            if (editingRecord != null) {
                TextButton(onClick = {
                    editingRecord = null
                    priceInput = ""
                    cantidadInput = "1.0"
                    notesInput = ""
                }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Cancelar edición", color = MaterialTheme.colorScheme.secondary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                if (selectedProductId != null) "HISTORIAL DE PRECIOS PARA ESTE PRODUCTO" else "ÚLTIMOS PRECIOS REGISTRADOS", 
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f), 
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            
            LazyColumn {
                items(priceEntries.size) { index ->
                    val entry = priceEntries[index]
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    entry.product.nombre, 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant, 
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    entry.supermarket.nombre + (if(entry.supermarket.direccion != null) " (${entry.supermarket.direccion})" else ""), 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                                    fontSize = 12.sp
                                )
                                Text(
                                    "Precio: $${entry.record.precio_ingresado} " + (if (entry.record.cantidad_unidad != 1.0) "por ${entry.record.cantidad_unidad}" else ""), 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), 
                                    fontSize = 11.sp
                                )
                                if (!entry.record.notas.isNullOrBlank()) {
                                    Text("Nota: ${entry.record.notas}", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 11.sp)
                                }
                            }
                            Row {
                                IconButton(onClick = { editingRecord = entry.record }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { viewModel.deletePrice(entry.record) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Borrar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
