package com.example.myapplicationexample.ui.comparison

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationexample.ComparadorApplication
import com.example.myapplicationexample.data.local.entity.UnitType
import com.example.myapplicationexample.domain.PriceCalculator
import com.example.myapplicationexample.ui.theme.*
import com.example.myapplicationexample.ui.viewmodel.PriceEntry
import com.example.myapplicationexample.ui.viewmodel.PriceViewModel
import com.example.myapplicationexample.ui.viewmodel.factory.PriceViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonScreen(
    viewModel: PriceViewModel = viewModel(
        factory = PriceViewModelFactory(
            (LocalContext.current.applicationContext as ComparadorApplication).database.priceRecordDao(),
            (LocalContext.current.applicationContext as ComparadorApplication).database.productDao(),
            (LocalContext.current.applicationContext as ComparadorApplication).database.supermarketDao()
        )
    )
) {
    var searchQuery by remember { mutableStateOf("") }
    val priceEntries by viewModel.priceEntries.collectAsState()
    
    // Filtered entries based on search
    val filteredEntries = priceEntries.filter { 
        it.product.nombre.contains(searchQuery, ignoreCase = true) 
    }.groupBy { it.product.id }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("COMPARADOR", color = NothingWhite, fontWeight = FontWeight.Bold, letterSpacing = 2.sp) },
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
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar producto...", color = NothingGrey) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = NothingWhite,
                    unfocusedTextColor = NothingWhite
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchQuery.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ingresa un producto para comparar", color = NothingGrey)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    filteredEntries.forEach { (productId, entries) ->
                        item {
                            ProductComparisonGroup(entries)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductComparisonGroup(entries: List<PriceEntry>) {
    val product = entries.first().product
    
    // Calculate unit prices for all entries
    val entriesWithUnitPrices = entries.map { entry ->
        val unitPrice = PriceCalculator.calculateUnitPrice(
            entry.record.precio_ingresado,
            entry.product.tipo_unidad,
            entry.product.piezas_por_empaque
        )
        entry to unitPrice
    }.sortedBy { it.second }

    val bestPrice = entriesWithUnitPrices.first().second

    Column {
        Text(
            text = product.nombre.uppercase(),
            color = NothingWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            letterSpacing = 1.sp
        )
        Text(
            text = "Unidad: ${product.tipo_unidad} " + (if(product.tipo_unidad == UnitType.EMPAQUE) "(${product.piezas_por_empaque} pz)" else ""),
            color = NothingGrey,
            fontSize = 12.sp
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        entriesWithUnitPrices.forEach { (entry, unitPrice) ->
            val isCheapest = unitPrice == bestPrice
            PriceComparisonCard(entry, unitPrice, isCheapest)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PriceComparisonCard(entry: PriceEntry, unitPrice: Double, isCheapest: Boolean) {
    val borderColor = if (isCheapest) NothingRed else NothingDarkGrey
    val backgroundColor = if (isCheapest) NothingDarkGrey else NothingBlack
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(if (isCheapest) 2.dp else 1.dp, borderColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(entry.supermarket.nombre, color = NothingWhite, fontWeight = FontWeight.Bold)
                Text("Precio: $${entry.record.precio_ingresado}", color = NothingGrey, fontSize = 14.sp)
                if (!entry.record.notas.isNullOrBlank()) {
                    Text(entry.record.notas, color = NothingGrey, fontSize = 12.sp)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    String.format("$%.2f", unitPrice),
                    color = if (isCheapest) NothingRed else NothingWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = when(entry.product.tipo_unidad) {
                        UnitType.KILOGRAMO -> "por 100g"
                        UnitType.LITRO -> "por 100ml"
                        UnitType.EMPAQUE -> "por pieza"
                        UnitType.PIEZA -> "por unidad"
                    },
                    color = NothingGrey,
                    fontSize = 11.sp
                )
            }
        }
    }
}
