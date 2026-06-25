package com.example

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.widget.RemoteViews
import com.example.data.AppDatabase
import com.example.data.Plant
import com.example.data.PlantUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SproutWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_WATER = "com.example.sprout.ACTION_WATER"
        const val EXTRA_PLANT_ID = "com.example.sprout.EXTRA_PLANT_ID"

        fun triggerUpdate(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisAppWidget = ComponentName(context, SproutWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget)
            
            val intent = Intent(context, SproutWidgetProvider::class.java).apply {
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

        if (intent.action == ACTION_WATER) {
            val plantId = intent.getStringExtra(EXTRA_PLANT_ID) ?: return
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
        val views = RemoteViews(context.packageName, R.layout.sprout_widget_layout)

        if (plant != null) {
            views.setTextViewText(R.id.widget_plant_name, plant.name)

            if (bitmap != null) {
                try {
                    val circular = getCircularBitmap(bitmap)
                    views.setImageViewBitmap(R.id.widget_plant_image, circular)
                } catch (e: Exception) {
                    e.printStackTrace()
                    views.setImageViewResource(R.id.widget_plant_image, R.drawable.ic_launcher_foreground)
                }
            } else {
                views.setImageViewResource(R.id.widget_plant_image, R.drawable.ic_launcher_foreground)
            }

            val now = System.currentTimeMillis()
            val diffMs = now - plant.lastWatered
            val activeLastWateredText = if (plant.lastWatered == 0L) {
                if (plant.waterProgress > 0.85f) "Just now" else "2 hours ago"
            } else {
                val diffSecs = diffMs / 1000
                val diffMins = diffSecs / 60
                val diffHours = diffMins / 60
                when {
                    diffSecs < 10 -> "Just now"
                    diffSecs < 60 -> "$diffSecs secs ago"
                    diffMins < 60 -> "$diffMins mins ago"
                    diffHours < 24 -> "$diffHours hours ago"
                    else -> "${diffHours / 24} days ago"
                }
            }
            views.setTextViewText(R.id.widget_plant_last_watered, "Last watered: $activeLastWateredText")

            val hydrationPercentage = (plant.waterProgress * 100).toInt()
            views.setTextViewText(R.id.widget_progress_text, "Hydration: $hydrationPercentage%")
            views.setProgressBar(R.id.widget_progress_bar, 100, hydrationPercentage, false)

            val waterIntent = Intent(context, SproutWidgetProvider::class.java).apply {
                action = ACTION_WATER
                putExtra(EXTRA_PLANT_ID, plant.id)
            }
            val flag = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val waterPendingIntent = PendingIntent.getBroadcast(
                context,
                plant.id.hashCode(),
                waterIntent,
                flag
            )
            views.setOnClickPendingIntent(R.id.widget_water_button, waterPendingIntent)

            if (plant.waterProgress >= 0.99f) {
                views.setTextViewText(R.id.widget_water_button, "Wet")
            } else {
                views.setTextViewText(R.id.widget_water_button, "Water")
            }

        } else {
            views.setTextViewText(R.id.widget_plant_name, "No active plant selected")
            views.setTextViewText(R.id.widget_plant_last_watered, "Open Sprout to select a plant!")
            views.setTextViewText(R.id.widget_progress_text, "Hydration: -")
            views.setProgressBar(R.id.widget_progress_bar, 100, 0, false)
            views.setTextViewText(R.id.widget_water_button, "Open")
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

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = Math.min(bitmap.width, bitmap.height)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint()
        paint.isAntiAlias = true

        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val srcRect = Rect(
            (bitmap.width - size) / 2,
            (bitmap.height - size) / 2,
            (bitmap.width + size) / 2,
            (bitmap.height + size) / 2
        )
        val dstRect = Rect(0, 0, size, size)
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint)

        return output
    }
}
