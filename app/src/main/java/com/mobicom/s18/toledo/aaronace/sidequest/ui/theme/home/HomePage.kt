package com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobicom.s18.toledo.aaronace.sidequest.R
import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel
import com.mobicom.s18.toledo.aaronace.sidequest.data.sampleQuests
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.fontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(
    quests: List<QuestModel> = sampleQuests,
    viewModel: HomeViewModel = viewModel()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val activeQuests = viewModel.getActiveQuests()
    val selectedQuest by viewModel.selectedQuest

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Text(
                    text = "My ",
                    fontFamily = fontFamily,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Quests",
                    fontFamily = fontFamily,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF40916C)
                )
            }
            Icon(
                painter = painterResource(R.drawable.filter),
                contentDescription = "Filter",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
            )
        }
        LazyColumn {
            item {
                Spacer(modifier = Modifier.padding(top = 24.dp))
            }
            items(
                items = activeQuests,
                key = { it.id }
            ) { quest ->
                SwipeToDelete(
                    quest = quest,
                    onRemove = { viewModel.deleteQuest(quest) },
                    modifier = Modifier.animateItem(tween(200)),
                    onClick = { viewModel.selectQuest(quest)}
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }

    // Bottom Sheet
    selectedQuest?.let { quest ->
        ModalBottomSheet(
            onDismissRequest = { viewModel.selectQuest(null) },
            sheetState = sheetState,
        ) {
            // Sheet content
            QuestBottomSheet(
                quest = quest,
                onComplete = { viewModel.completeQuest(quest) }
            )
        }
    }
}

@Preview
@Composable
fun HomePreview() {
    HomePage(quests = sampleQuests)
}

@Composable
fun QuestCard(
    quest: QuestModel,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(Color(0xFFF9A620))
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = quest.title,
                    fontFamily = fontFamily,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(top = 4.dp)
                ){
                    Icon(
                        painter = painterResource(R.drawable.location),
                        contentDescription = null,
                        tint = Color(0xFF40916C),
                        modifier = Modifier
                            .size(16.dp)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = quest.location,
                        fontFamily = fontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF71727A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = quest.details,
                    fontFamily = fontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF71727A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun QuestBottomSheet(
    quest: QuestModel,
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .fillMaxHeight(0.5f)
    ) {
        Text(
            text = quest.title,
            fontFamily = fontFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.location),
                contentDescription = null,
                tint = Color(0xFF40916C),
                modifier = Modifier
                    .size(16.dp)
                    .padding(end = 4.dp)
            )
            Text(
                text = quest.location,
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF71727A),
            )
        }
        Text(
            text = quest.details,
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF71727A),
            modifier = Modifier
                .padding(top = 6.dp)
        )
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        Button(
            onClick = onComplete,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF40916C)
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Complete Quest",
                fontFamily = fontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun SwipeToDelete(
    quest: QuestModel,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val swipeToDeleteState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            if(state == SwipeToDismissBoxValue.EndToStart) {
                coroutineScope.launch {
                    delay(0.5.seconds)
                    onRemove()
                }
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = swipeToDeleteState,
        backgroundContent = {
            val backgroundColor by animateColorAsState(
                when (swipeToDeleteState.dismissDirection) {
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> Color.Transparent
                },
                label = "Background color animation"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .padding(end = 24.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.trash),
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                )
            }
        },
        modifier = modifier
    ) {
        QuestCard(quest = quest, onClick = onClick)
    }
}
