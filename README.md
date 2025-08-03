# SideQuest (MOBICOM MCO)

A location-based task management application for Android.

## Overview

SideQuest is a task management application that combines traditional to-do functionality with location-based notifications. Users can create "quests" and associate them with specific geographic locations. The application provides proximity-based notifications when users are near quest locations.

## Features

- Create location-based tasks with OpenStreetMap integration
- Proximity notifications for nearby quests
- Quest completion tracking
- User ranking system based on completed quests
- Interactive OpenStreetMap for quest location management
- Swipe-to-delete quest management

## Requirements

- Android 6.0 (API level 23) or higher
- Location permissions for proximity notifications
- Internet connection for OpenStreetMap functionality

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/AART1125/MCO_MOBICOM.git
   ```

2. Open the project in Android Studio

3. Build and run the application on an Android device or emulator

## Usage

### Creating Quests
1. Open the application and navigate to the Home page
2. Tap on the Floating Action Button
3. Search for a location and select it
4. Enter quest title and description
6. Confirm quest creation

### Managing Quests
- View active and completed quests in separate tabs in the Home page
- View quest locations in the Map page
- Tap quests to view details and navigate to locations via OpenStreetMap
- Swipe left to delete quests
- Mark quests as complete when finished

### Location Notifications
The application monitors user location in the background and sends notifications when users are within proximity of quest locations. Location permissions must be granted for this functionality.

## Technical Details

### Built With
- Kotlin
- Jetpack Compose
- Material Design 3
- OpenStreetMap
- Nominatim
- Firebase
- Android Architecture Components

### Architecture
The application follows the MVVM (Model-View-ViewModel) architecture pattern with separate modules for UI components, data models, and other utils/services.

## Authors

Chiara Louise Jugno
Jalene Graciella Siazon
Aaron Ace Toledo
