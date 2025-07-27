package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobicom.s18.toledo.aaronace.sidequest.R

@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit
) {
    val questGreen = Color(0xFF509A72)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.landing_background),
            contentDescription = "Background with a decorative dashed line and exclamation mark",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 32.dp, top = 20.dp)
        ) {
            Text(
                text = "TIME TO\nGO ON A\nLITTLE",
                color = Color.Black,
                fontSize = 66.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 64.sp
            )
            Text(
                text = "SIDE\nQUEST",
                color = questGreen,
                fontSize = 66.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 64.sp
            )
        }

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = questGreen
            )
        ) {
            Text(
                text = "Let's Go!",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LandingScreenPreview() {
    LandingScreen(
        onNavigateToLogin = {}
    )
}