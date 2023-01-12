package com.begoml.uistatedelegate.features.home

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.begoml.uistatedelegate.forgotpassword.ForgotPasswordActivity

@Composable
fun HomeScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = "Home screen"
        )

        Button(onClick = {
            context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
        }) {
            Text(text = "Go To Old Flow")
        }
    }
}
