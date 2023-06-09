package com.example.cotam

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cotam.common.Constants.TOPIC
import com.example.cotam.data.UserData
import com.example.cotam.presentation.screens.MainScreen
import com.example.cotam.presentation.screens.MessageScreen
import com.example.cotam.presentation.screens.PeopleScreen
import com.example.cotam.presentation.screens.SettingsScreen
import com.example.cotam.presentation.screens.SplashScreen
import com.example.cotam.presentation.screens.auth.AuthScreen
import com.example.cotam.presentation.screens.auth.ForgotPasswordScreen
import com.example.cotam.presentation.screens.auth.SignInScreen
import com.example.cotam.presentation.screens.auth.SignUpScreen
import com.example.cotam.ui.theme.CoTamTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoTamTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navig()
                }
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

    }
}

@Composable
fun Navig() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash_screen") {

        composable("main") {
            MainScreen(navController = navController)
        }
        composable("message") {

            val userData = navController
                .previousBackStackEntry
                ?.arguments
                ?.getParcelable<UserData>("userData")

            userData?.let {
                MessageScreen(
                    navController = navController,
                    userData = userData
                )
            }

        }
        composable("sign_in") {
            SignInScreen(navController)
        }
        composable("sign_up") {
            SignUpScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController)
        }
        composable("people") {
            PeopleScreen(navController)
        }
        composable("auth") {
            AuthScreen(navController)
        }
        composable("forgot_password") {
            ForgotPasswordScreen()
        }
        composable("splash_screen") {
            SplashScreen(navController)
        }

    }





}




