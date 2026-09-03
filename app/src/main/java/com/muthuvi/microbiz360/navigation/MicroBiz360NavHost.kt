package com.muthuvi.microbiz360.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.muthuvi.microbiz360.ui.screens.auth.LoginScreen
import com.muthuvi.microbiz360.ui.screens.dashboard.DashboardScreen
import com.muthuvi.microbiz360.ui.screens.payments.PaymentsScreen
import com.muthuvi.microbiz360.ui.screens.products.ProductsScreen
import com.muthuvi.microbiz360.ui.screens.sales.SalesScreen

import com.muthuvi.microbiz360.data.model.Product
import com.muthuvi.microbiz360.data.repository.ProductRepository
import com.muthuvi.microbiz360.data.repository.SalesRepository
import com.muthuvi.microbiz360.data.repository.AuthRepository
import com.muthuvi.microbiz360.ui.screens.auth.RegisterScreen
import com.muthuvi.microbiz360.utils.getFriendlyAuthError

@Composable
fun MicroBiz360NavHost() {

    val navController = rememberNavController()

    var pendingSaleAmount by remember {
        mutableStateOf(0.0)
    }

    var pendingQuantity by remember {
        mutableStateOf(1)
    }

    var todaySales by remember {
        mutableStateOf(0)
    }

    var todayRevenue by remember {
        mutableStateOf(0.0)
    }

    var showPaymentSuccess by remember {
        mutableStateOf(false)
    }

    var products by remember {
        mutableStateOf<List<Product>>(emptyList())
    }

    var authLoading by remember {
        mutableStateOf(false)
    }

    var authError by remember {
        mutableStateOf<String?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        composable(Screen.Login.route) {

            LoginScreen(
                isLoading = authLoading,
                errorMessage = authError,

                onSignIn = { email, password ->

                    authLoading = true
                    authError = null

                    AuthRepository.signIn(
                        email = email,
                        password = password,

                        onSuccess = {

                            ProductRepository.seedProductsIfEmpty(

                                onSuccess = {

                                    ProductRepository.observeProducts(
                                        onProductsChanged = { firebaseProducts ->
                                            products = firebaseProducts
                                        },
                                        onFailure = {
                                            // Product observer failure
                                        }
                                    )

                                    SalesRepository.observeTodaySummary(
                                        onSummaryChanged = { salesCount, revenue ->
                                            todaySales = salesCount
                                            todayRevenue = revenue
                                        },
                                        onFailure = {
                                            // Dashboard observer failure
                                        }
                                    )

                                    authLoading = false

                                    navController.navigate(
                                        Screen.Dashboard.route
                                    ) {

                                        popUpTo(Screen.Login.route) {
                                            inclusive = true
                                        }

                                        launchSingleTop = true
                                    }
                                },

                                onFailure = { exception ->
                                    authLoading = false

                                    authError =
                                        exception.message
                                            ?: "Unable to load business data."
                                }
                            )
                        },

                        onFailure = { exception ->

                            authLoading = false
                            authError = getFriendlyAuthError(exception)
                        }
                    )
                },

                onCreateAccount = {

                    authError = null

                    navController.navigate(
                        Screen.Register.route
                    )
                }
            )
        }

        composable(Screen.Register.route) {

            RegisterScreen(
                isLoading = authLoading,
                errorMessage = authError,

                onRegister = { email, password ->

                    authLoading = true
                    authError = null

                    AuthRepository.register(
                        email = email,
                        password = password,

                        onSuccess = {

                            ProductRepository.seedProductsIfEmpty(

                                onSuccess = {

                                    ProductRepository.observeProducts(
                                        onProductsChanged = { firebaseProducts ->
                                            products = firebaseProducts
                                        },
                                        onFailure = {
                                            // Product observer failure
                                        }
                                    )

                                    SalesRepository.observeTodaySummary(
                                        onSummaryChanged = { salesCount, revenue ->
                                            todaySales = salesCount
                                            todayRevenue = revenue
                                        },
                                        onFailure = {
                                            // Dashboard observer failure
                                        }
                                    )

                                    authLoading = false

                                    navController.navigate(
                                        Screen.Dashboard.route
                                    ) {

                                        popUpTo(Screen.Login.route) {
                                            inclusive = true
                                        }

                                        launchSingleTop = true
                                    }
                                },

                                onFailure = { exception ->

                                    authLoading = false

                                    authError =
                                        exception.message
                                            ?: "Unable to initialize business data."
                                }
                            )
                        },

                        onFailure = { exception ->

                            authLoading = false
                            authError = getFriendlyAuthError(exception)
                        }
                    )
                },

                onBackToLogin = {

                    authError = null

                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Dashboard.route) {

            DashboardScreen(
                todaySales = todaySales,
                todayRevenue = todayRevenue,
                showPaymentSuccess = showPaymentSuccess,
                onProductsClick = {
                    showPaymentSuccess = false
                    navController.navigate(Screen.Products.route)
                },
                onLogout = {

                    AuthRepository.signOut()

                    products = emptyList()
                    todaySales = 0
                    todayRevenue = 0.0
                    showPaymentSuccess = false
                    pendingSaleAmount = 0.0

                    navController.navigate(Screen.Login.route) {

                        popUpTo(Screen.Dashboard.route) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Products.route) {

            ProductsScreen(
                products = products,
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
                onProceedToPayment = { quantity, total ->

                    pendingQuantity = quantity
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
                onPaymentCompleted = { paymentMethod ->

                    val product = products.firstOrNull {
                        it.id == "sugar_1kg"
                    }

                    if (product != null) {

                        SalesRepository.completeSale(
                            productId = product.id,
                            productName = product.name,
                            unitPrice = product.price,
                            currentStock = product.stock,
                            quantity = pendingQuantity,
                            totalAmount = pendingSaleAmount,
                            paymentMethod = paymentMethod,

                            onSuccess = {

                                showPaymentSuccess = true
                                pendingSaleAmount = 0.0
                                pendingQuantity = 1

                                navController.navigate(Screen.Dashboard.route) {

                                    popUpTo(Screen.Dashboard.route) {
                                        inclusive = false
                                    }

                                    launchSingleTop = true
                                }
                            },

                            onFailure = {
                                // Transaction write failed
                            }
                        )
                    }
                }
            )
        }
    }
}
