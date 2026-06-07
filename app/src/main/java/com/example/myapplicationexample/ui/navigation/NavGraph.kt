package com.example.myapplicationexample.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplicationexample.ui.comparison.ComparisonScreen
import com.example.myapplicationexample.ui.price.PriceEntryScreen
import com.example.myapplicationexample.ui.product.ProductScreen
import com.example.myapplicationexample.ui.settings.SettingsScreen
import com.example.myapplicationexample.ui.supermarket.SupermarketScreen
import kotlinx.serialization.Serializable

@Serializable object ComparisonDestination
@Serializable object ProductDestination
@Serializable object SupermarketDestination
@Serializable object PriceEntryDestination
@Serializable object SettingsDestination

data class TopLevelDestination(
    val route: Any,
    val icon: ImageVector,
    val label: String
)

val TOP_LEVEL_DESTINATIONS = listOf(
    TopLevelDestination(ComparisonDestination, Icons.Default.ShoppingCart, "Precios"),
    TopLevelDestination(PriceEntryDestination, Icons.Default.Add, "Asignar"),
    TopLevelDestination(ProductDestination, Icons.Default.List, "Productos"),
    TopLevelDestination(SupermarketDestination, Icons.Default.Place, "Tiendas"),
    TopLevelDestination(SettingsDestination, Icons.Default.Settings, "Configuración")
)

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ComparisonDestination,
        modifier = modifier
    ) {
        composable<ComparisonDestination> {
            ComparisonScreen()
        }
        composable<ProductDestination> {
            ProductScreen()
        }
        composable<SupermarketDestination> {
            SupermarketScreen()
        }
        composable<PriceEntryDestination> {
            PriceEntryScreen()
        }
        composable<SettingsDestination> {
            SettingsScreen()
        }
    }
}
