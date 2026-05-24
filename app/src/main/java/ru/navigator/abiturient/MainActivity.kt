package ru.navigator.abiturient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.navigator.abiturient.ui.navigation.NavigatorAppHost
import ru.navigator.abiturient.ui.theme.NavigatorAbiturientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NavigatorAbiturientTheme {
                NavigatorAppHost()
            }
        }
    }
}
