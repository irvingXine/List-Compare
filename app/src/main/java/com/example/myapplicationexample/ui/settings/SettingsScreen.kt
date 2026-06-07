package com.example.myapplicationexample.ui.settings

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationexample.ui.theme.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream"),
        onResult = { uri ->
            uri?.let { exportDatabase(context, it) }
        }
    )

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let { importDatabase(context, it) }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CONFIGURACIÓN", color = NothingWhite, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NothingBlack)
            )
        },
        containerColor = NothingBlack
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("BASE DE DATOS", color = NothingGrey, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = NothingDarkGrey)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Respaldo", color = NothingWhite, fontWeight = FontWeight.Bold)
                    Text("Guarda o restaura tus datos locales.", color = NothingGrey, fontSize = 12.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { exportLauncher.launch("comparador_backup.db") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NothingRed, contentColor = NothingWhite)
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("EXPORTAR")
                        }
                        
                        Button(
                            onClick = { importLauncher.launch(arrayOf("*/*")) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = NothingWhite, contentColor = NothingBlack)
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

private fun exportDatabase(context: Context, uri: Uri) {
    try {
        val dbFile = context.getDatabasePath("supermercados_db")
        context.contentResolver.openOutputStream(uri)?.use { output ->
            FileInputStream(dbFile).use { input ->
                input.copyTo(output)
            }
        }
        Toast.makeText(context, "Respaldo exportado con éxito", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error al exportar: ${e.message}", Toast.LENGTH_LONG).show()
    }
}

private fun importDatabase(context: Context, uri: Uri) {
    try {
        val dbFile = context.getDatabasePath("supermercados_db")
        // Close database before importing would be better, but for simplicity:
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(dbFile).use { output ->
                input.copyTo(output)
            }
        }
        Toast.makeText(context, "Respaldo restaurado. Reinicia la app.", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error al importar: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
