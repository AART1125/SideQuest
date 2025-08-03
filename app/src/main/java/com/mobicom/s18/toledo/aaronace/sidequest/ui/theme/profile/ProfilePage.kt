package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobicom.s18.toledo.aaronace.sidequest.R
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.auth.AuthViewModel

@Preview
@Composable
fun ProfilePage(
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {

    val username by profileViewModel.username
    val userRank by profileViewModel.userRank
    val completedQuests by profileViewModel.completedQuests

    Box(modifier = Modifier.fillMaxSize()) {
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

        TextButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y = 20.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White // Change as needed
            ),
            onClick = {authViewModel.logout({})}
        ) {

            Image(
                painter = painterResource(R.drawable.exit_img),
                contentDescription = null,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text("Logout")
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(10))
        ) {
            Column(
                modifier = Modifier
            ) {
                Text("$username", // change to "username"
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                    fontSize = 30.sp,
                    color = Color(0xFF52B788)
                )
                Spacer(
                    modifier = Modifier
                    .height(16.dp))
                Text("$userRank", // change to "userRank"
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
                Text("$completedQuests", // change to "$completedQuests"
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF52B788))
                Text("Quests Completed",
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