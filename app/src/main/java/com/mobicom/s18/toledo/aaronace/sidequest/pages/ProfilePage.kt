package com.mobicom.s18.toledo.aaronace.sidequest.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mobicom.s18.toledo.aaronace.sidequest.R

@Preview
@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.profile_bg),
            contentDescription = "Background Image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )

        Image(
            painter = painterResource(R.drawable.profile_character),
            contentDescription = "Sample Character",
            modifier = Modifier
                .size(400.dp)
                .align(Alignment.Center)
                .offset(y = -(90).dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(10))
        ) {
            Column(
                modifier = Modifier
            ) {
                Text("Juan De La Cruz",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                    fontSize = 30.sp,
                    color = Color(0xFF52B788)
                )
                Spacer(
                    modifier = Modifier
                    .height(16.dp))
                Text("Project Manager",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontSize = 20.sp)
                Spacer(
                    modifier = Modifier
                        .height(8.dp))
                HorizontalDivider(
                    modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally))
                Spacer(
                    modifier = Modifier
                        .height(16.dp))
                Text("50",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF52B788))
                Text("Task Completed",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontSize = 20.sp)
                Spacer(
                    modifier = Modifier
                        .height(160.dp)
                )
            }
        }
    }
}