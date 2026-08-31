package com.muthuvi.microbiz360.ui.screens.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DashboardScreen() {
    Scaffold { innerPadding ->
        Text(
            text = "MicroBiz360 Dashboard",
            modifier = Modifier.padding(innerPadding)
        )
    }
}