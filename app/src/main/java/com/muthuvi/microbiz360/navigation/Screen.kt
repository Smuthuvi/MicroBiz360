package com.muthuvi.microbiz360.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Products : Screen("products")
    object Sales : Screen("sales")
    object Payments : Screen("payments")
}