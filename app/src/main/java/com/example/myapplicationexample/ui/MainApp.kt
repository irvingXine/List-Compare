package com.example.myapplicationexample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplicationexample.ui.navigation.NavGraph
import com.example.myapplicationexample.ui.navigation.TOP_LEVEL_DESTINATIONS
import com.example.myapplicationexample.ui.theme.NothingBlack
import com.example.myapplicationexample.ui.theme.NothingDarkGrey
import com.example.myapplicationexample.ui.theme.NothingGrey
import com.example.myapplicationexample.ui.theme.NothingRed
import com.example.myapplicationexample.ui.theme.NothingWhite
import kotlinx.coroutines.launch

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // We use ModalNavigationDrawer for the side menu
    // CompositionLocalProvider can be used to set layout direction to RTL for right drawer
    // But standard Material3 Drawer is on the left. To make it right, we wrap it.
    
    CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr) {
                    ModalDrawerSheet(
                        drawerContainerColor = NothingBlack,
                        modifier = Modifier.width(280.dp)
                    ) {
                        Spacer(Modifier.height(48.dp))
                        Text(
                            "MENÚ",
                            modifier = Modifier.padding(16.dp),
                            color = NothingWhite,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        HorizontalDivider(color = NothingDarkGrey)
                        TOP_LEVEL_DESTINATIONS.forEach { destination ->
                            val isSelected = currentDestination?.hierarchy?.any {
                                it.hasRoute(destination.route::class)
                            } == true

                            NavigationDrawerItem(
                                label = { Text(destination.label) },
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(destination.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                    scope.launch { drawerState.close() }
                                },
                                icon = { Icon(destination.icon, contentDescription = null) },
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = NothingDarkGrey,
                                    unselectedContainerColor = Color.Transparent,
                                    selectedIconColor = NothingRed,
                                    unselectedIconColor = NothingGrey,
                                    selectedTextColor = NothingWhite,
                                    unselectedTextColor = NothingGrey
                                ),
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(androidx.compose.ui.platform.LocalLayoutDirection provides androidx.compose.ui.unit.LayoutDirection.Ltr) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = NothingBlack
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        NavGraph(navController = navController)
                        
                        // Floating Hamburger Button on Top Right
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp)
                                .background(NothingBlack.copy(alpha = 0.5f), MaterialTheme.shapes.small)
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = NothingWhite)
                        }
                    }
                }
            }
        }
    }
}
