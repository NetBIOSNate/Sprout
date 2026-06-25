package com.example.ui

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Plant
import com.example.data.Friend
import com.example.data.SproutRepository
import com.example.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Nursery : Screen()
    object MyGarden : Screen()
    object Stats : Screen()
    object Settings : Screen()
    object PurchaseSeeds : Screen()
    object Friends : Screen()
    object Login : Screen()
    data class PlantDetail(val plantId: String) : Screen()
    object CareGuides : Screen()
}

class SproutViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SproutRepository

    val plants: StateFlow<List<Plant>>
    val userProfile: StateFlow<UserProfile?>
    val friends: StateFlow<List<Friend>>

    private val _registeredProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val registeredProfiles: StateFlow<List<UserProfile>> = _registeredProfiles.asStateFlow()

    // Navigation Backstack
    val screenHistory = mutableStateListOf<Screen>(Screen.Home)
    val currentScreen: Screen get() = screenHistory.lastOrNull() ?: Screen.Home

    // UI state filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _nurseryTab = MutableStateFlow("All") // "All", "Desert", "Nebula", "Tundra"
    val nurseryTab: StateFlow<String> = _nurseryTab.asStateFlow()

    private val _realOrMakeBelieveTab = MutableStateFlow("Real") // "Real", "Make-Believe"
    val realOrMakeBelieveTab: StateFlow<String> = _realOrMakeBelieveTab.asStateFlow()

    private val _plantAlerts = MutableStateFlow<List<String>>(emptyList())
    val plantAlerts: StateFlow<List<String>> = _plantAlerts.asStateFlow()
    
    private val dismissedAlerts = mutableSetOf<String>()

    fun dismissAlert(alert: String) {
        dismissedAlerts.add(alert)
        _plantAlerts.value = _plantAlerts.value.filter { it != alert }
    }

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SproutRepository(database.plantDao(), database.userProfileDao(), database.friendDao())

        plants = repository.allPlants.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        userProfile = repository.userProfile.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        friends = repository.allFriends.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Prepopulate database if needed
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
            refreshProfilesList()
        }

        // Watering schedule simulation loop
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(10000) // Check every 10 seconds for demo purposes
                val currentPlants = repository.allPlants.firstOrNull() ?: continue
                val activeProfile = repository.userProfile.firstOrNull()
                val notificationsEnabled = activeProfile?.notificationEnabled ?: true
                
                currentPlants.filter { it.isCollected }.forEach { plant ->
                    val drainRate = when(plant.waterLevel.lowercase()) {
                        "high" -> 0.05f // dries faster
                        "low" -> 0.01f  // dries slower
                        else -> 0.03f
                    }
                    
                    if (plant.waterProgress > 0f) {
                        val newProgress = (plant.waterProgress - drainRate).coerceAtLeast(0f)
                        val newNeeds = if (newProgress < 0.4f) "Thirsty" else "Good"
                        
                        if (newProgress < 0.4f) {
                            // Trigger alert if notification is enabled globally and for this plant
                            if (notificationsEnabled && plant.isNotificationEnabled) {
                                val alertMsg = "${plant.name} is thirsty! Time to water."
                                if (!_plantAlerts.value.contains(alertMsg) && !dismissedAlerts.contains(alertMsg)) {
                                    _plantAlerts.value = _plantAlerts.value + alertMsg
                                }
                            }
                        }
                        
                        repository.updatePlant(plant.copy(
                            waterProgress = newProgress,
                            waterNeeds = newNeeds
                        ))
                    }
                }
            }
        }
    }

    fun navigateTo(screen: Screen) {
        // Avoid duplicate screens successively
        if (screenHistory.lastOrNull() == screen) return
        screenHistory.add(screen)
    }

    fun navigateBack() {
        if (screenHistory.size > 1) {
            screenHistory.removeAt(screenHistory.size - 1)
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setNurseryTab(tab: String) {
        _nurseryTab.value = tab
    }

    fun setRealOrMakeBelieveTab(tab: String) {
        _realOrMakeBelieveTab.value = tab
    }

    fun waterPlant(plantId: String) {
        viewModelScope.launch {
            repository.waterPlant(plantId)
            val plant = repository.allPlants.firstOrNull()?.find { it.id == plantId }
            if (plant != null) {
                val alertMsg = "${plant.name} is thirsty! Time to water."
                dismissedAlerts.remove(alertMsg)
                _plantAlerts.value = _plantAlerts.value.filter { it != alertMsg }
            }
            com.example.SproutWidgetProvider.triggerUpdate(getApplication())
        }
    }

    fun setAsWidget(plantId: String) {
        viewModelScope.launch {
            repository.updateWidgetActive(plantId)
            com.example.SproutWidgetProvider.triggerUpdate(getApplication())
        }
    }

    fun buySeedPackage(seeds: Int) {
        viewModelScope.launch {
            repository.buySeedPackage(seeds)
        }
    }

    fun purchasePlant(plantId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            val success = repository.purchasePlant(plantId)
            if (success) {
                com.example.SproutWidgetProvider.triggerUpdate(getApplication())
                onSuccess()
            } else {
                onFailure()
            }
        }
    }

    fun toggleFavorite(plantId: String) {
        viewModelScope.launch {
            repository.toggleFav(plantId)
        }
    }

    fun updatePlant(plant: com.example.data.Plant) {
        viewModelScope.launch {
            repository.updatePlant(plant)
        }
    }

    fun resetPlantGrowth(plantId: String) {
        viewModelScope.launch {
            repository.resetPlantGrowth(plantId)
            com.example.SproutWidgetProvider.triggerUpdate(getApplication())
        }
    }

    fun toggleDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            val current = repository.userProfile.firstOrNull() ?: return@launch
            repository.updateProfile(current.copy(isDarkMode = isDarkMode))
        }
    }

    fun toggleNotification(isEnabled: Boolean) {
        viewModelScope.launch {
            val current = repository.userProfile.firstOrNull() ?: return@launch
            repository.updateProfile(current.copy(notificationEnabled = isEnabled))
        }
    }

    fun updateDefaultEnvironment(env: String) {
        viewModelScope.launch {
            val current = repository.userProfile.firstOrNull() ?: return@launch
            repository.updateProfile(current.copy(defaultEnvironment = env))
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            repository.updateProfile(profile)
        }
    }

    // MULTI-USER PROFILING AND AUTH
    fun refreshProfilesList() {
        viewModelScope.launch {
            _registeredProfiles.value = repository.getAllProfiles()
        }
    }

    fun registerNewProfile(
        name: String,
        username: String,
        passcode: String,
        avatarColor: Int,
        growthPref: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.createProfile(name, username, passcode, avatarColor, growthPref)
            if (success) {
                refreshProfilesList()
                onSuccess()
            } else {
                onFailure("Username already taken. Grow another stem!")
            }
        }
    }

    fun loginToProfile(
        username: String,
        passcode: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            val success = repository.loginToProfile(username, passcode)
            if (success) {
                refreshProfilesList()
                onSuccess()
            } else {
                onFailure("Invalid credentials or passcode.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutActiveProfile()
            refreshProfilesList()
            // Direct history back to home, which will request authentication
            screenHistory.clear()
            screenHistory.add(Screen.Home)
        }
    }

    // COMPANIONS / FRIENDS INTERACTIONS
    fun sendSeedsToFriend(friendUsername: String, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.sendSeedsToFriend(friendUsername)
            onFinished(success)
        }
    }

    fun addFriend(username: String, name: String, favPlant: String, onFinished: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.registerNewFriend(username, name, favPlant)
            onFinished(success)
        }
    }

    // INTERACTIVE BIOLOGICAL GROWTH ENGINE (GROWTH PATTERN)
    fun nurturePlant(plantId: String, onFinished: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val masterList = plants.value
            val plant = masterList.find { it.id == plantId }
            val profile = userProfile.value

            if (plant == null || profile == null) {
                onFinished(false, "Botanist or plant not found.")
                return@launch
            }

            if (profile.seedBalance < 50) {
                onFinished(false, "You need at least 50 seeds to buy growth fertilizer!")
                return@launch
            }

            // Deduct 50 seeds for nurturing
            val updatedProfile = profile.copy(seedBalance = profile.seedBalance - 50)
            repository.updateProfile(updatedProfile)

            // Advance growth stages!
            // Seed -> Spore -> Seedling -> Juvenile -> Mature -> Flowering -> Majestic Majestic
            val currentStage = plant.stage
            val nextStage = when (currentStage) {
                "Seed", "Spore", "Cosmic Seed" -> "Seedling"
                "Seedling" -> "Juvenile"
                "Juvenile" -> "Mature"
                "Mature" -> "Flowering"
                else -> "Majestic"
            }

            val isMatured = nextStage == "Mature" || nextStage == "Flowering" || nextStage == "Majestic"
            val updatedPlant = plant.copy(
                stage = nextStage,
                waterProgress = (plant.waterProgress + 0.15f).coerceAtMost(1f)
            )
            repository.updatePlant(updatedPlant)

            if (isMatured) {
                // Award completion stats
                val updatedWithMatured = updatedProfile.copy(
                    plantsMatured = updatedProfile.plantsMatured + 1,
                    seedBalance = updatedProfile.seedBalance + 150 // mature bonus!
                )
                repository.updateProfile(updatedWithMatured)
                com.example.SproutWidgetProvider.triggerUpdate(getApplication())
                onFinished(true, "Incredible! Your plant sprouted to $nextStage and earned +150 bonus seeds!")
            } else {
                com.example.SproutWidgetProvider.triggerUpdate(getApplication())
                onFinished(true, "Your nurturing accelerated the local growth pattern! Nurtured to $nextStage.")
            }
        }
    }
}
