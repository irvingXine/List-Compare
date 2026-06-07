package com.example.myapplicationexample.ui.product

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationexample.ComparadorApplication
import com.example.myapplicationexample.data.local.entity.Product
import com.example.myapplicationexample.data.local.entity.UnitType
import com.example.myapplicationexample.ui.components.BarcodeScannerView
import com.example.myapplicationexample.ui.theme.*
import com.example.myapplicationexample.ui.viewmodel.ProductViewModel
import com.example.myapplicationexample.ui.viewmodel.factory.ProductViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ProductViewModel = viewModel(
        factory = ProductViewModelFactory(
            (LocalContext.current.applicationContext as ComparadorApplication).database.productDao()
        )
    )
) {
    val context = LocalContext.current
    var productName by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("") }
    var unitType by remember { mutableStateOf(UnitType.PIEZA) }
    var piecesPerPackage by remember { mutableStateOf("") }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    
    val products by viewModel.products.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var unitExpanded by remember { mutableStateOf(false) }
    
    var showScanner by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showScanner = true
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(editingProduct) {
        if (editingProduct != null) {
            productName = editingProduct!!.nombre
            barcode = editingProduct!!.codigo_barras ?: ""
            unitType = editingProduct!!.tipo_unidad
            piecesPerPackage = editingProduct!!.piezas_por_empaque?.toString() ?: ""
        }
    }

    if (showScanner) {
        BarcodeScannerView(
            onBarcodeDetected = { code ->
                barcode = code
                showScanner = false
                // Intentar buscar si el producto ya existe
                val existingProduct = products.find { it.codigo_barras == code }
                if (existingProduct != null) {
                    editingProduct = existingProduct
                    Toast.makeText(context, "Producto encontrado: ${existingProduct.nombre}", Toast.LENGTH_SHORT).show()
                }
            },
            onClose = { showScanner = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("PRODUCTOS", color = NothingWhite, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
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
                // Form Section
                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Nombre del producto", color = NothingGrey) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NothingWhite,
                        unfocusedTextColor = NothingWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = barcode,
                        onValueChange = { barcode = it },
                        label = { Text("Código de barras", color = NothingGrey) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NothingWhite,
                            unfocusedTextColor = NothingWhite
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    Button(
                        onClick = { 
                            when (PackageManager.PERMISSION_GRANTED) {
                                ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                    showScanner = true
                                }
                                else -> {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NothingBlue, contentColor = NothingWhite),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("SCAN")
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = !unitExpanded }
                ) {
                    OutlinedTextField(
                        value = unitType.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo de Unidad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NothingWhite,
                            unfocusedTextColor = NothingWhite
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        UnitType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    unitType = type
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }

                if (unitType == UnitType.EMPAQUE) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = piecesPerPackage,
                        onValueChange = { piecesPerPackage = it },
                        label = { Text("Piezas por empaque") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NothingWhite,
                            unfocusedTextColor = NothingWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { 
                        if (editingProduct == null) {
                            viewModel.addProduct(productName, barcode.ifBlank { null }, unitType, piecesPerPackage.toIntOrNull())
                        } else {
                            viewModel.updateProduct(editingProduct!!.copy(
                                nombre = productName,
                                codigo_barras = barcode.ifBlank { null },
                                tipo_unidad = unitType,
                                piezas_por_empaque = piecesPerPackage.toIntOrNull()
                            ))
                            editingProduct = null
                        }
                        productName = ""
                        barcode = ""
                        piecesPerPackage = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NothingWhite, contentColor = NothingBlack),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (editingProduct == null) "GUARDAR PRODUCTO" else "GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
                }

                if (editingProduct != null) {
                    TextButton(onClick = {
                        editingProduct = null
                        productName = ""
                        barcode = ""
                        piecesPerPackage = ""
                    }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Cancelar edición", color = NothingGrey)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                
                // Search Section
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = { Text("Buscar en registrados...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NothingGrey) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NothingWhite,
                        unfocusedTextColor = NothingWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn {
                    items(products.size) { index ->
                        val product = products[index]
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = NothingDarkGrey)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = product.nombre, color = NothingWhite, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = if (product.tipo_unidad == UnitType.EMPAQUE) 
                                            "Unidad: ${product.tipo_unidad} (${product.piezas_por_empaque} pz)" 
                                            else "Unidad: ${product.tipo_unidad}", 
                                        color = NothingGrey, 
                                        fontSize = 12.sp
                                    )
                                }
                                Row {
                                    IconButton(onClick = { editingProduct = product }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = NothingGrey)
                                    }
                                    IconButton(onClick = { viewModel.deleteProduct(product) }) {
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
}
