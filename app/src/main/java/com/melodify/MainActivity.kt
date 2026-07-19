package com.melodify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.melodify.musicapp.navigation.MelodifyNavHost
import com.melodify.musicapp.ui.theme.MelodifyTheme
import com.melodify.musicapp.ui.components.MelodifyBottomNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MelodifyTheme {  // ✅ استفاده از MelodifyTheme
                MelodifyApp()
            }
        }
    }
}

@Composable
fun MelodifyApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            MelodifyBottomNavigation(navController = navController)
        }
    ) { innerPadding ->
        MelodifyNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MelodifyTheme {  // ✅ استفاده از MelodifyTheme
        Greeting("Android")
    }
}