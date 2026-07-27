package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey val id: String,
    val name: String,
    val scientificName: String,
    val origin: String,
    val about: String,
    val light: String,
    val waterLevel: String, // "Low", "Medium", "High"
    val soil: String,
    val stage: String, // e.g., "Juvenile", "Seedling", "Mature"
    val imageUrl: String,
    val isCollected: Boolean,
    val isWishlist: Boolean,
    val waterNeeds: String, // "Good", "Thirsty", "Saturated"
    val waterProgress: Float, // 0f to 1f
    val isWidgetActive: Boolean,
    val category: String, // "Desert", "Nebula", "Tundra", "Tropical", etc.
    val seedPrice: Int,
    val rarity: String, // "Common", "Epic", "Legendary", "Rare"
    val lastWatered: Long = 0L,
    val careInstructions: String? = null,
    val isNotificationEnabled: Boolean = true
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String = "Jane Doe",
    val seedBalance: Int = 2450,
    val totalWaterings: Int = 1204,
    val wateringStreak: Int = 14,
    val totalDaysWatered: Int = 128,
    val plantsMatured: Int = 18,
    val seedsPlanted: Int = 56,
    val activePlantId: String = "monstera_deliciosa",
    val isDarkMode: Boolean = false,
    val notificationEnabled: Boolean = true,
    val defaultEnvironment: String = "Tundra", // Tundra, Desert...
    val selectedTheme: String = "Default",
    val selectedFont: String = "System",
    val unlockedThemes: String = "Default",
    val unlockedFonts: String = "System",
    val completedAchievements: String = "",
    val username: String = "janedoe",
    val passcode: String = "1234",
    val isActive: Boolean = true,
    val collectedPlantIds: String = "monstera_deliciosa,pilea,string_of_pearls,snake_plant",
    val wishlistPlantIds: String = "lunar_fern",
    val avatarColor: Int = 0xFF4CAF50.toInt(),
    val xp: Int = 0,
    val userLevel: Int = 1
)

@Entity(tableName = "friends")
data class Friend(
    @PrimaryKey val username: String,
    val name: String,
    val level: Int,
    val seedBalance: Int,
    val favoritePlantName: String,
    val gardenBonusPercentage: Int,
    val lastSeedsSentDate: Long = 0L,
    val avatarColor: Int = 0xFF388E3C.toInt()
)
