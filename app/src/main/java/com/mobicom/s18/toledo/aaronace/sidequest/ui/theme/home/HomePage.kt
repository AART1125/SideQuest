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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.mobicom.s18.toledo.aaronace.sidequest.model.toDateString
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.fontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.time.Duration.Companion.seconds
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(viewModel: HomeViewModel = viewModel()) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    //val quests by viewModel.quests.collectAsState()
    val quests by viewModel.questsState
    val isLoading by viewModel.isLoading
    val selectedQuest by viewModel.selectedQuest

    val selectedTab by viewModel.selectedTab
    val currentTabQuests = when (selectedTab) {
        0 -> quests.filter { !it.completed }
        1 -> quests.filter { it.completed }
        else -> emptyList()
    }

    // State for confirmation and undo
    val (pendingDeleteQuest, setPendingDeleteQuest) = remember { mutableStateOf<QuestModel?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp)
            .background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
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
            /*Icon(
                painter = painterResource(R.drawable.filter),
                contentDescription = "Filter",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
            )*/
        }

        // Active and Completed quests tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.White,
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Color(0xFF40916C)
                    )
                }
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { viewModel.selectedTab(0) },
                text = {
                    Text(
                        text = "Active",
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedTab == 0) Color(0xFF40916C) else Color.Gray
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { viewModel.selectedTab(1) },
                text = {
                    Text(
                        text = "Completed",
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedTab == 1) Color(0xFF40916C) else Color.Gray
                    )
                }
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            if (currentTabQuests.isEmpty()) {
                // No quests
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "No active quests available" else "No completed quests yet",
                        fontFamily = fontFamily,
                        fontSize = 16.sp,
                        color = Color(0xFF71727A)
                    )
                }
            } else {
                // Display quests for current tab
                LazyColumn (modifier = Modifier.padding(horizontal = 24.dp)) {
                    item {
                        Spacer(modifier = Modifier.padding(top = 24.dp))
                    }
                    items(
                        items = currentTabQuests,
                        key = { it.id }
                    ) { quest ->
                        SwipeToDelete(
                            quest = quest,
                            onRemove = {setPendingDeleteQuest(quest)},
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

        }
    }

    // Confirmation dialog
    pendingDeleteQuest?.let { quest ->
        AlertDialog(
            onDismissRequest = { setPendingDeleteQuest(null) },
            title = { Text("Delete Quest") },
            text = { Text("Are you sure you want to delete this quest?") },
            confirmButton = {
                TextButton(onClick = {
                    setPendingDeleteQuest(null)
                    viewModel.deleteQuest(quest)
                    Toast.makeText(context, "Quest deleted", Toast.LENGTH_SHORT).show()
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { setPendingDeleteQuest(null) }) { Text("Cancel") }
            }
        )
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
                    // Adjust color bar based on completion status
                    .background(
                        if (quest.completed) Color(0xFF40916C) else Color(0xFFF9A620)
                    )
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

// Bottom Sheet
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

        // Show date of completion if quest is completed
        if (quest.completed) {
            Row {
                Text(
                    text = "Completed on: ",
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF40916C),
                    modifier = Modifier
                        .padding(top = 12.dp)
                )
                Text(
                    text = quest.completedAt.toDateString(),
                    fontFamily = fontFamily,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF71727A),
                    modifier = Modifier
                        .padding(top = 12.dp)
                )
            }
        }
        Spacer(
            modifier = Modifier
                .weight(1f)
        )

        // Show complete button only if quest is not completed
        if (!quest.completed) {
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
