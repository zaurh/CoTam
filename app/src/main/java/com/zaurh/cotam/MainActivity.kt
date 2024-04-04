@file:OptIn(ExperimentalAnimationApi::class)

package com.zaurh.cotam

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.zaurh.cotam.common.Constants.TOPIC
import com.zaurh.cotam.data.remote.UserData
import com.zaurh.cotam.data_store.StoreSettings
import com.zaurh.cotam.presentation.screens.AccountScreen
import com.zaurh.cotam.presentation.screens.MainScreen
import com.zaurh.cotam.presentation.screens.MessageScreen
import com.zaurh.cotam.presentation.screens.PeopleScreen
import com.zaurh.cotam.presentation.screens.SettingsScreen
import com.zaurh.cotam.presentation.screens.SplashScreen
import com.zaurh.cotam.presentation.screens.auth.AuthScreen
import com.zaurh.cotam.presentation.screens.auth.ForgotPasswordScreen
import com.zaurh.cotam.presentation.screens.auth.SignInScreen
import com.zaurh.cotam.presentation.screens.auth.SignUpScreen
import com.zaurh.cotam.presentation.screens.viewmodel.AuthViewModel
import com.zaurh.cotam.presentation.screens.viewmodel.StorageViewModel
import com.zaurh.cotam.presentation.screens.viewmodel.UserViewModel
import com.zaurh.cotam.ui.theme.CoTamTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val dataStore = StoreSettings(context)
            var darkTheme by remember { mutableStateOf(false) }
            val savedDarkMode = dataStore.getDarkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()


            CoTamTheme(darkTheme = savedDarkMode.value ?: false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Navig(
                        darkTheme = savedDarkMode.value ?: false,
                    ) {
                        scope.launch {
                            darkTheme = !(savedDarkMode.value ?: false)
                            dataStore.saveDarkMode(darkTheme)
                        }
                    }
                }
            }
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun Navig(
    darkTheme: Boolean,
    onThemeUpdated: () -> Unit
) {
    val navController = rememberAnimatedNavController()
    val userViewModel = hiltViewModel<UserViewModel>()
    val storageViewModel = hiltViewModel<StorageViewModel>()

    AnimatedNavHost(navController = navController, startDestination = "splash_screen") {
        composable("main",
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) + fadeIn(animationSpec = tween(600))
            }, enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            }
        ) {
            MainScreen(navController = navController, userViewModel = userViewModel)
        }
        composable(
            "message",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
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
        composable(
            "sign_in",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popEnterTransition = {

                slideInHorizontally(
                    initialOffsetX = { -8000 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                )
                fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            SignInScreen(navController)
        }
        composable(
            "sign_up",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            SignUpScreen(navController)
        }
        composable(
            "settings",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popEnterTransition = {

                slideInHorizontally(
                    initialOffsetX = { -8000 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                )
                fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            SettingsScreen(
                navController = navController,
                userViewModel = userViewModel,
                darkTheme = darkTheme,
                onThemeUpdated = onThemeUpdated
            )
        }
        composable(
            "account",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popEnterTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                )
                fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            val userData =
                navController.previousBackStackEntry?.arguments?.getParcelable<UserData>("userData")

            userData?.let {
                AccountScreen(
                    navController = navController,
                    userData = it,
                    userViewModel = userViewModel,
                    storageViewModel = storageViewModel
                )
            }
        }
        composable(
            "people",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popEnterTransition = {

                slideInHorizontally(
                    initialOffsetX = { -8000 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                )
                fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            PeopleScreen(navController)
        }
        composable(
            "auth",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popEnterTransition = {

                slideInHorizontally(
                    initialOffsetX = { -8000 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                )
                fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            AuthScreen(navController)
        }
        composable(
            "forgot_password",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            ForgotPasswordScreen()
        }
        composable(
            "splash_screen",
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeIn(animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -300 },
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(animationSpec = tween(600))
            },
        ) {
            SplashScreen(navController)
        }

    }


}




