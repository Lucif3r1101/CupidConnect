package com.rishav.cupidconnect

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType

private lateinit var sharedPreferences: SharedPreferences

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        auth = Firebase.auth
        setContent {
            val navController = rememberNavController()
            CupidConnectApp(navController)
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Register : Screen("register")
    object RegisterPage : Screen("registerPage")
    // Define other screens here
}

@Composable
fun CupidConnectApp(navController: NavHostController) {
    val isFirstLaunch by remember { mutableStateOf(isFirstLaunch()) }

    NavHost(navController = navController, startDestination = if (isFirstLaunch) Screen.Splash.route else Screen.Register.route) {
        composable(Screen.Splash.route) {
            SplashPager(onRegisterClick = { navController.navigate(Screen.Register.route) })
        }
        composable(Screen.Register.route) {
            RegisterLoginContent(onRegisterClick = { navController.navigate(Screen.RegisterPage.route) })
        }
        composable(Screen.RegisterPage.route) {
            RegisterPage()
        }
        // Define other screens here
    }
}



fun isFirstLaunch(): Boolean {
    return !sharedPreferences.contains("isFirstLaunch")
}

fun markFirstLaunch() {
    sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashPager(onRegisterClick: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = {4})

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 64.dp),
            pageSpacing = 24.dp,
            state = pagerState
        ) { page ->
            when (page) {
                0 -> PageContent("Page 1 Content")
                1 -> PageContent("Page 2 Content")
                2 -> PageContent("Page 3 Content")
                3 -> {
                    markFirstLaunch()
                    RegisterLoginContent(onRegisterClick)
                }
                else -> error("Invalid page: $page")
            }
        }
        DotsIndicator(
            modifier = Modifier.padding(vertical = 8.dp),
            dotCount = pagerState.pageCount,
            type = ShiftIndicatorType(dotsGraphic = DotGraphic(color = MaterialTheme.colors.primary)),
            pagerState = pagerState
        )
    }
}
@Composable
fun PageContent(content: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = content,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun RegisterLoginContent(onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onRegisterClick) {
            Text(text = "Register")
        }
        Text(
            text = "Already have an account?",
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Button(onClick = { /* Handle login action */ }) {
            Text(text = "Login")
        }
    }
}

@Composable
fun RegisterPage() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }
    val confirmPasswordVisibility = remember { mutableStateOf(false) }
    val passwordMatches = password == confirmPassword
    val photoUri = remember { mutableStateOf<Uri?>(null) } // Declare photoUri as MutableState
    // Define state variables for Snackbar visibility and error message
    val snackbarVisibleState = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        PasswordField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visibility = passwordVisibility,
            onToggleClick = { passwordVisibility.value = !passwordVisibility.value },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        PasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visibility = confirmPasswordVisibility,
            onToggleClick = { confirmPasswordVisibility.value = !confirmPasswordVisibility.value },
            modifier = Modifier.padding(vertical = 8.dp)
        )
        if(!passwordMatches) {
            Text(
                text = "Passowrd do not match",
                color = Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)

            )
        }
        SelectPhoto(photoUri = photoUri) // Call the composable function to handle photo selection
        Button(
            onClick = {
                if (passwordMatches) {
                    // Handle registration action when passwords match
                } else {
                    // Set error message
                    errorMessage.value = "Passwords do not match"
                    // Show Snackbar
                    snackbarVisibleState.value = true
                }
            },
            enabled = passwordMatches // Disable button if passwords don't match
        ) {
            Text(text = "Register")
        }

// Display Snackbar if passwords don't match
        if (snackbarVisibleState.value) {
            Snackbar(
                action = {
                    Button(
                        onClick = { snackbarVisibleState.value = false }
                    ) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(errorMessage.value)
            }
        }

    }
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: @Composable (() -> Unit),
    visibility: MutableState<Boolean>,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        visualTransformation = if (visibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { onToggleClick() }) {
                Icon(
                    imageVector = if (visibility.value) Icons.Default.Person else Icons.Default.Person,
                    contentDescription = if (visibility.value) "Hide password" else "Show password"
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun SelectPhoto(photoUri: MutableState<Uri?>) {
    val getContent = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            photoUri.value = it // Modify photoUri directly
        }
    }
    Button(onClick = { getContent.launch("image/*") }) {
        Text(text = "Select Photo")
    }
}
