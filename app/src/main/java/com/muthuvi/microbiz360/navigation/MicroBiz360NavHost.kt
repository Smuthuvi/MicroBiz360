package com.muthuvi.microbiz360.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.muthuvi.microbiz360.ui.screens.dashboard.DashboardScreen
import com.muthuvi.microbiz360.ui.screens.payments.PaymentsScreen
import com.muthuvi.microbiz360.ui.screens.products.ProductsScreen
import com.muthuvi.microbiz360.ui.screens.sales.SalesScreen

@Composable
fun MicroBiz360NavHost() {

    val navController = rememberNavController()

    var pendingSaleAmount by remember {
        mutableStateOf(0.0)
    }

    var todaySales by remember {
        mutableStateOf(0)
    }

    var todayRevenue by remember {
        mutableStateOf(0.0)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {

        composable(Screen.Dashboard.route) {

            DashboardScreen(
                todaySales = todaySales,
                todayRevenue = todayRevenue,
                onProductsClick = {
                    navController.navigate(Screen.Products.route)
                }
            )
        }

        composable(Screen.Products.route) {

            ProductsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onStartSale = {
                    navController.navigate(Screen.Sales.route)
                }
            )
        }

        composable(Screen.Sales.route) {

            SalesScreen(
                onBack = {
                    navController.popBackStack()
                },
                onProceedToPayment = { total ->

                    pendingSaleAmount = total

                    navController.navigate(Screen.Payments.route)
                }
            )
        }

        composable(Screen.Payments.route) {

            PaymentsScreen(
                amount = pendingSaleAmount,
                onBack = {
                    navController.popBackStack()
                },
                onPaymentCompleted = {

                    todaySales += 1
                    todayRevenue += pendingSaleAmount

                    pendingSaleAmount = 0.0

                    navController.navigate(Screen.Dashboard.route) {

                        popUpTo(Screen.Dashboard.route) {
                            inclusive = false
                        }

                        launchSingleTop = true
                    }
                }
            )
        }
    }
}