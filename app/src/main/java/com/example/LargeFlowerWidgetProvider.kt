package com.example

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import android.widget.RemoteViews
import com.example.data.AppDatabase
import com.example.data.Plant
import com.example.data.PlantUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LargeFlowerWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_WATER_LARGE = "com.example.sprout.ACTION_WATER_LARGE"
        const val EXTRA_PLANT_ID = "com.example.sprout.EXTRA_PLANT_ID_LARGE"

        fun triggerUpdate(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(context, LargeFlowerWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            
            val intent = Intent(context, LargeFlowerWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            }
            context.sendBroadcast(intent)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val db = AppDatabase.getDatabase(context.applicationContext)
        val plantDao = db.plantDao()
        val userProfileDao = db.userProfileDao()

        CoroutineScope(Dispatchers.IO).launch {
            val plants = plantDao.getAllPlantsFlow().firstOrNull() ?: emptyList()
            val activePlant = plants.firstOrNull { it.isWidgetActive } ?: plants.firstOrNull { it.isCollected }
            val profile = userProfileDao.getUserProfile()
            val env = profile?.defaultEnvironment ?: "Tropical"
            
            val imageUrl = activePlant?.let { PlantUtils.getPlantImageUrl(it, env) }
            val bitmap = imageUrl?.let { fetchBitmap(context, it) }

            withContext(Dispatchers.Main) {
                for (appWidgetId in appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, activePlant, bitmap)
                }
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_WATER_LARGE) {
            val plantId = intent.getStringExtra(EXTRA_PLANT_ID) ?: return
            
            // Instantly hide the button for this specific widget to give immediate feedback
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(context, LargeFlowerWidgetProvider::class.java)
            val views = RemoteViews(context.packageName, R.layout.large_flower_widget_layout)
            views.setViewVisibility(R.id.widget_water_button, View.GONE)
            appWidgetManager.updateAppWidget(thisAppWidget, views)

            val db = AppDatabase.getDatabase(context.applicationContext)
            val plantDao = db.plantDao()
            val userProfileDao = db.userProfileDao()

            CoroutineScope(Dispatchers.IO).launch {
                val plant = plantDao.getPlantById(plantId)
                val profile = userProfileDao.getUserProfile()
                if (plant != null) {
                    val currentStage = plant.stage
                    val nextStage = when (currentStage) {
                        "Seed", "Spore", "Cosmic Seed" -> "Sprout"
                        "Sprout", "Seedling" -> "Small Plant"
                        "Juvenile", "Small Plant" -> "Mature"
                        else -> "Mature"
                    }
                    val updatedPlant = plant.copy(
                        waterNeeds = "Good",
                        waterProgress = 1.0f,
                        lastWatered = System.currentTimeMillis(),
                        stage = nextStage
                    )
                    plantDao.updatePlant(updatedPlant)

                    if (profile != null) {
                        val updatedProfile = profile.copy(
                            totalWaterings = profile.totalWaterings + 1,
                            wateringStreak = profile.wateringStreak + if (Math.random() > 0.6) 1 else 0,
                            totalDaysWatered = profile.totalDaysWatered + if (Math.random() > 0.8) 1 else 0
                        )
                        userProfileDao.updateUserProfile(updatedProfile)
                    }

                    withContext(Dispatchers.Main) {
                        SproutWidgetProvider.triggerUpdate(context)
                        triggerUpdate(context)
                    }
                }
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        plant: Plant?,
        bitmap: Bitmap?
    ) {
        val views = RemoteViews(context.packageName, R.layout.large_flower_widget_layout)

        if (plant != null) {
            if (bitmap != null) {
                views.setImageViewBitmap(R.id.widget_plant_image, bitmap)
            } else {
                views.setImageViewResource(R.id.widget_plant_image, R.drawable.ic_launcher_foreground)
            }

            val waterIntent = Intent(context, LargeFlowerWidgetProvider::class.java).apply {
                action = ACTION_WATER_LARGE
                putExtra(EXTRA_PLANT_ID, plant.id)
            }
            val flag = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val waterPendingIntent = PendingIntent.getBroadcast(
                context,
                plant.id.hashCode() + 100,
                waterIntent,
                flag
            )
            views.setOnClickPendingIntent(R.id.widget_water_button, waterPendingIntent)

            if (plant.waterProgress >= 0.99f) {
                views.setViewVisibility(R.id.widget_water_button, View.GONE)
            } else {
                views.setViewVisibility(R.id.widget_water_button, View.VISIBLE)
            }
        } else {
            views.setViewVisibility(R.id.widget_water_button, View.GONE)
            views.setImageViewResource(R.id.widget_plant_image, R.drawable.ic_launcher_foreground)
        }

        val appIntent = Intent(context, MainActivity::class.java)
        val appPendingIntent = PendingIntent.getActivity(
            context,
            0,
            appIntent,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        views.setOnClickPendingIntent(R.id.widget_root, appPendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private suspend fun fetchBitmap(context: Context, url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val loader = coil.ImageLoader(context)
                val request = coil.request.ImageRequest.Builder(context)
                    .data(url)
                    .allowHardware(false)
                    .build()
                val result = loader.execute(request)
                if (result is coil.request.SuccessResult) {
                    (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
