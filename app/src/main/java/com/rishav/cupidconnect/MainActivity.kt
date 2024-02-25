package com.rishav.cupidconnect

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType


private lateinit var sharedPreferences: SharedPreferences

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        setContent {
            CupidConnectApp()
        }
    }
}

@Composable
fun CupidConnectApp() {
    val isFirstLaunch by remember { mutableStateOf(isFirstLaunch()) }

    if (isFirstLaunch) {
        SplashPager()
    } else {
        RegisterLoginContent()
    }
}

fun isFirstLaunch(): Boolean {
    // Assume if the "isFirstLaunch" key doesn't exist in SharedPreferences, it's the first launch
    return !sharedPreferences.contains("isFirstLaunch")
}

fun markFirstLaunch() {
    // Mark the app as not first launch by saving a flag in SharedPreferences
    sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SplashPager() {
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
                    RegisterLoginContent()
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
fun RegisterLoginContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { /* Handle register action */ }) {
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