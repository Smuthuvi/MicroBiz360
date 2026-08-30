package com.muthuvi.microbiz360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.muthuvi.microbiz360.navigation.MicroBiz360NavHost
import com.muthuvi.microbiz360.ui.theme.MicroBiz360Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MicroBiz360Theme {
                MicroBiz360NavHost()
            }
        }
    }
}