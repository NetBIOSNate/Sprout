package com.example.data

object PlantUtils {
    /**
     * Determines the correct plant image URL based on its growth stage and the current environment.
     */
    fun getPlantImageUrl(plant: Plant, environment: String): String {
        val stageLower = plant.stage.lowercase()
        val envLower = environment.lowercase()
        
        val stagePrompt = when {
            stageLower.contains("seed") || stageLower.contains("spore") -> "seed_in_soil"
            stageLower.contains("sprout") || stageLower.contains("seedling") -> "tiny_sprout_emerging_from_soil"
            stageLower.contains("small") || stageLower.contains("juvenile") || stageLower.contains("shoot") || stageLower.contains("cutting") || stageLower.contains("bulb") -> "young_growing_plant_in_pot"
            else -> "mature_beautiful_plant_in_pot"
        }
        
        val promptRaw = "$stagePrompt ${plant.name} $envLower environment digital art illustration"
        val promptEncoded = java.net.URLEncoder.encode(promptRaw, "UTF-8")
        
        return "https://image.pollinations.ai/prompt/$promptEncoded?width=512&height=512&nologo=true"
    }

    /**
     * Gets a detailed descriptive reaction text of the plant's response to the active environment.
     */
    fun getEnvironmentReactionText(plant: Plant, environment: String): String {
        val envLower = environment.lowercase()
        val catLower = plant.category.lowercase()

        if (envLower == catLower) {
            return "🌿 Thriving perfectly in its native ${plant.category} climate! Foliage remains lush and healthy."
        }

        return when (envLower) {
            "tundra" -> "❄️ Shivering! Non-Arctic species are frost-tipped. Leaves slow cell division but gain beautiful crystal icy patterns."
            "desert" -> "🔥 Intense sun stress! Adapting via thick cuticles and showing a warm amber/bronze sunbaked golden hue."
            "nebula" -> "✨ Magical astral synergy! Resonating with starlight, drawing cosmic gases, and emitting sparkling nebula pollen."
            else -> "💚 Feeling cozy and safe."
        }
    }

    data class NextWateringDateResult(
        val dateString: String,
        val daysRemaining: Int,
        val intervalDaysUsed: Int,
        val calculationReasoning: String
    )

    /**
     * Parses the care instructions to find suggested watering interval (in days).
     * If no pattern is found, uses a default based on the plant's waterLevel capability.
     */
    fun getWateringIntervalDays(plant: Plant, careInstructions: String?): Int {
        if (careInstructions.isNullOrBlank()) {
            return when (plant.waterLevel.lowercase()) {
                "high" -> 3
                "medium" -> 7
                "low" -> 14
                else -> 7
            }
        }

        val text = careInstructions.lowercase()

        // Scan for specific days keywords
        // Try to match "every X days" or "once every X days"
        val patternEveryDays = Regex("""(?:every|once every|\b)\s*(\d+)\s*(?:-\s*\d+\s*)?days\b""")
        val matchEveryDays = patternEveryDays.find(text)
        if (matchEveryDays != null) {
            val daysStr = matchEveryDays.groupValues[1]
            val days = daysStr.toIntOrNull()
            if (days != null && days in 1..90) {
                return days
            }
        }

        if (text.contains("twice a week") || text.contains("2 times a week")) {
            return 3
        }

        if (text.contains("once a week") || text.contains("weekly") || text.contains("every week")) {
            return 7
        }

        if (text.contains("every two weeks") || text.contains("every 2 weeks") || text.contains("bi-weekly") || text.contains("biweekly") || text.contains("once in two weeks")) {
            return 14
        }

        if (text.contains("every three weeks") || text.contains("every 3 weeks")) {
            return 21
        }

        if (text.contains("once a month") || text.contains("monthly") || text.contains("every month")) {
            return 30
        }

        if (text.contains("daily") || text.contains("every day") || text.contains("once a day")) {
            return 1
        }

        // Default fallback to waterLevel
        return when (plant.waterLevel.lowercase()) {
            "high" -> 3
            "medium" -> 7
            "low" -> 14
            else -> 7
        }
    }

    /**
     * Calculates the exact next watering info based on plant and AI care manual instructions.
     */
    fun getNextWateringInfo(plant: Plant, careInstructions: String?): NextWateringDateResult {
        val intervalDays = getWateringIntervalDays(plant, careInstructions)
        
        // Base time is either the last watered time, or the current time if never watered
        val baseTimeMs = if (plant.lastWatered > 0L) plant.lastWatered else System.currentTimeMillis()
        val nextWaterTimeMs = baseTimeMs + (intervalDays.toLong() * 24L * 60L * 60L * 1000L)
        val currentTimeMs = System.currentTimeMillis()
        
        val diffMs = nextWaterTimeMs - currentTimeMs
        val daysRemaining = Math.ceil(diffMs.toDouble() / (1000.0 * 60.0 * 60.0 * 24.0)).toInt()
        
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        val formattedDate = sdf.format(java.util.Date(nextWaterTimeMs))
        
        val isExplicit = !careInstructions.isNullOrBlank() && (
            careInstructions.contains("days", ignoreCase = true) ||
            careInstructions.contains("week", ignoreCase = true) ||
            careInstructions.contains("monthly", ignoreCase = true) ||
            careInstructions.contains("daily", ignoreCase = true)
        )
        
        val explanation = if (isExplicit) {
            "💡 Calculated directly from AI Care Manual: recommended interval is $intervalDays days."
        } else {
            "📊 Derived from natural water capability (${plant.waterLevel}): recommended interval is $intervalDays days."
        }
        
        return NextWateringDateResult(
            dateString = formattedDate,
            daysRemaining = if (daysRemaining < 0) 0 else daysRemaining,
            intervalDaysUsed = intervalDays,
            calculationReasoning = explanation
        )
    }
}
