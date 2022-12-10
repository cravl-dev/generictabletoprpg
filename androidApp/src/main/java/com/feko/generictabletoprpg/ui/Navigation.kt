package com.feko.generictabletoprpg.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import com.feko.generictabletoprpg.ui.import.Import
import com.feko.generictabletoprpg.ui.spell.SpellDetails
import com.feko.generictabletoprpg.ui.spell.SpellOverview
import com.feko.generictabletoprpg.ui.spell.SpellSlots
import kotlinx.coroutines.launch

object Navigation {
    interface Destination {
        val screenTitle: String
        val route: String
        val isRootDestination: Boolean
        fun navHostComposable(
            navGraphBuilder: NavGraphBuilder,
            navController: NavHostController,
            appBarTitle: MutableState<String>
        )
    }

    private val destinations: List<Destination> =
        listOf(
            SpellDetails,
            SpellOverview,
            SpellSlots,
            Import
        )

    private val rootDestinations: List<Destination> =
        destinations.filter { it.isRootDestination }

    private val firstDestination: Destination =
        destinations.first { it is SpellSlots }

    @Composable
    private fun Host(
        navController: NavHostController,
        appBarTitle: MutableState<String>
    ) {
        NavHost(
            navController = navController,
            startDestination = firstDestination.route
        ) {
            destinations.forEach {
                it.navHostComposable(this, navController, appBarTitle)
            }
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    fun Drawer(
        drawerState: DrawerState,
        paddingValues: PaddingValues,
        navController: NavHostController,
        appBarTitle: MutableState<String>
    ) {
        val scope = rememberCoroutineScope()
        val activeDrawerItem = rememberSaveable { mutableStateOf(firstDestination.route) }
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    rootDestinations.forEach { destination ->
                        val route = destination.route
                        NavigationDrawerItem(
                            label = { Text(destination.screenTitle) },
                            selected = activeDrawerItem.value == route,
                            onClick = {
                                activeDrawerItem.value = route
                                scope.launch {
                                    drawerState.close()
                                }
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            })

                    }
                }
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            Host(navController, appBarTitle)
        }
    }
}