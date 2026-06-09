package com.example.myapplicationexample.ui.settings

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationexample.ComparadorApplication
import com.example.myapplicationexample.data.local.entity.PriceRecord
import com.example.myapplicationexample.data.local.entity.Product
import com.example.myapplicationexample.data.local.entity.Supermarket
import com.example.myapplicationexample.data.repository.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@Serializable
data class AppBackup(
    val supermarkets: List<Supermarket>,
    val products: List<Product>,
    val priceRecords: List<PriceRecord>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as ComparadorApplication
    val currentTheme by app.preferenceManager.theme.collectAsState()
    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
        onResult = { uri ->
            uri?.let { 
                scope.launch { exportDataToJson(app, it) }
            }
        }
    )

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { 
                scope.launch { importDataFromJson(app, it) }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CONFIGURACIÓN", fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            
            // --- TEMA ---
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("APARIENCIA", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tema Visual", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            RadioButton(
                                selected = currentTheme == AppTheme.AMOLED,
                                onClick = { app.preferenceManager.setTheme(AppTheme.AMOLED) }
                            )
                            Text("Amoled", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            RadioButton(
                                selected = currentTheme == AppTheme.MATERIAL,
                                onClick = { app.preferenceManager.setTheme(AppTheme.MATERIAL) }
                            )
                            Text("Material Design", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- BASE DE DATOS ---
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Icon(Icons.Default.Storage, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("DATOS", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Respaldo", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text("Guarda o restaura tus datos locales.", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 12.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { exportLauncher.launch("respaldo_precios.json") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("EXPORTAR")
                        }
                        
                        Button(
                            onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("IMPORTAR")
                        }
                    }
                }
            }
        }
    }
}

private suspend fun exportDataToJson(app: ComparadorApplication, uri: Uri) {
    withContext(Dispatchers.IO) {
        try {
            val db = app.database
            val backup = AppBackup(
                supermarkets = db.supermarketDao().getAllSupermarkets().first(),
                products = db.productDao().getAllProducts().first(),
                priceRecords = db.priceRecordDao().getAllPriceRecords().first()
            )
            
            val jsonString = Json { prettyPrint = true }.encodeToString(backup)
            
            app.contentResolver.openOutputStream(uri)?.use { output ->
                OutputStreamWriter(output).use { writer ->
                    writer.write(jsonString)
                }
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(app, "Datos exportados a JSON", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(app, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

private suspend fun importDataFromJson(app: ComparadorApplication, uri: Uri) {
    withContext(Dispatchers.IO) {
        try {
            val content = app.contentResolver.openInputStream(uri)?.use { input ->
                BufferedReader(InputStreamReader(input)).readText()
            } ?: throw Exception("No se pudo leer el archivo")

            val backup = Json.decodeFromString<AppBackup>(content)
            val db = app.database

            // Proceso de restauración: sobreescribir datos
            // Nota: Debido a las llaves foráneas, el orden importa o hay que limpiar todo
            
            // 1. Limpiar registros de precios (dependen de productos y tiendas)
            val currentPrices = db.priceRecordDao().getAllPriceRecords().first()
            currentPrices.forEach { db.priceRecordDao().deletePriceRecord(it) }

            // 2. Limpiar productos y tiendas
            val currentProducts = db.productDao().getAllProducts().first()
            currentProducts.forEach { db.productDao().deleteProduct(it) }

            val currentSupermarkets = db.supermarketDao().getAllSupermarkets().first()
            currentSupermarkets.forEach { db.supermarketDao().deleteSupermarket(it) }

            // 3. Insertar nuevos datos del JSON
            backup.supermarkets.forEach { db.supermarketDao().insertSupermarket(it) }
            backup.products.forEach { db.productDao().insertProduct(it) }
            backup.priceRecords.forEach { db.priceRecordDao().insertPriceRecord(it) }

            withContext(Dispatchers.Main) {
                Toast.makeText(app, "Respaldo JSON restaurado con éxito", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(app, "Error al importar JSON: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
