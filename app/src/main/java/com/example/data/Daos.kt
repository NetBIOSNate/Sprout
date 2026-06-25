package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun getAllPlantsFlow(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :id LIMIT 1")
    suspend fun getPlantById(id: String): Plant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlants(plants: List<Plant>)

    @Update
    suspend fun updatePlant(plant: Plant)

    @Delete
    suspend fun deletePlant(plant: Plant)

    @Query("UPDATE plants SET isWidgetActive = (id = :plantId)")
    suspend fun setActiveWidget(plantId: String)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE isActive = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE isActive = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Query("SELECT * FROM user_profile WHERE username = :username LIMIT 1")
    suspend fun getProfileByUsername(username: String): UserProfile?

    @Query("SELECT * FROM user_profile")
    suspend fun getAllProfiles(): List<UserProfile>

    @Query("UPDATE user_profile SET isActive = 0")
    suspend fun deactivateAllProfiles()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Update
    suspend fun updateUserProfile(profile: UserProfile)
}

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends")
    fun getAllFriendsFlow(): Flow<List<Friend>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<Friend>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriend(friend: Friend)

    @Update
    suspend fun updateFriend(friend: Friend)

    @Query("DELETE FROM friends WHERE username = :username")
    suspend fun deleteFriend(username: String)
}
