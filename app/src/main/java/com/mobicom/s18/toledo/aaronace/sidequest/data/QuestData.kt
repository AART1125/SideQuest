package com.mobicom.s18.toledo.aaronace.sidequest.data

import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel

val sampleQuests = listOf(
    QuestModel(
        "1",
        "Buy Groceries",
        "Milk, Eggs, Bread, Cheese",
        "SM Supermarket",
        completed = false
    ),
    QuestModel(
        "2",
        "Go Green",
        "Buy a plant or succulent and learn how to take care of it",
        "Farmers Garden, General, MacArthur Ave",
        completed = false
    ),
    QuestModel(
        "3",
        "Coffee Connoisseur",
        "Try a specialty coffee in a new cafe. This text overflows too, but we've got that covered!",
        "Cubao Expo, Cubao Quezon City",
        completed = false
    ),
    QuestModel(
        "4",
        "Food Trip",
        "Try 3 street food you've never eaten before",
        "UP Diliman, Quezon City",
        completed = false
    ),
    QuestModel(
        "5",
        "Bookworm",
        "Buy a secondhand book at Biblio",
        "Alabang Town Center, 1780 Theater Dr, Alabang, Muntinlupa",
        completed = false
    ),
    QuestModel(
        "6",
        "Org Life",
        "Attend general assembly happening at G101",
        "De La Salle University, 2401 Taft Ave, Manila",
        completed = false
    ),
    QuestModel(
        "7",
        "A Completed Quest",
        "This quest has been completed so it doesn't show in My Quests!",
        "De La Salle University, 2401 Taft Ave, Manila",
        completed = true
    )
)