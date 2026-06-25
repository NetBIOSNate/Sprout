package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun getCareGuide(plantName: String, scientificName: String): CareGuideResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext CareGuideResult.Error("API_KEY_MISSING", "Gemini API Key is not configured. Please input your key in the Secrets panel in AI Studio manually with key name 'GEMINI_API_KEY'.")
        }

        val prompt = """
            Provide a complete houseplant care guide for the following plant:
            Name: $plantName
            Scientific Name: $scientificName
            
            Format the response strictly as a single JSON object with the following keys and content structure:
            {
              "careInstructions": "A detailed, structured paragraph or list with specific, helpful instructions regarding watering frequency, humidity, fertilization, temperature ranges, and pruning.",
              "lightingNeeds": "A precise description of the light requirements (e.g., Bright Indirect, Medium, low light tolerance, direct sun caution).",
              "toxicityWarnings": "A clear, specific statement explaining whether this plant is pet-friendly or toxic to cats and dogs (and any common symptoms if ingested)."
            }
            Do not include any markdown formatting, surrounding text, or backticks other than the raw JSON object itself.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
            put("generationConfig", JSONObject().apply {
                put("responseFormat", JSONObject().apply {
                    put("type", "APPLICATION_JSON")
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBody)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val code = response.code
                    val errBody = response.body?.string() ?: ""
                    Log.e("GeminiService", "API error: $code - $errBody")
                    return@withContext CareGuideResult.Error("API_ERROR", "Gemini API returned error: $code")
                }

                val responseBody = response.body?.string()
                if (responseBody.isNullOrEmpty()) {
                    return@withContext CareGuideResult.Error("EMPTY_RESPONSE", "Received empty response from Gemini.")
                }

                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext CareGuideResult.Error("NO_CANDIDATES", "No content candidates found.")
                }
                
                val textResponse = candidates
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                val cleanJsonString = textResponse.trim()
                val guideJson = JSONObject(cleanJsonString)
                
                val careInstructions = guideJson.optString("careInstructions", "No specific care instructions returned.")
                val lightingNeeds = guideJson.optString("lightingNeeds", "No standard lighting needs provided.")
                val toxicityWarnings = guideJson.optString("toxicityWarnings", "No toxicity information supplied.")

                CareGuideResult.Success(
                    careInstructions = careInstructions,
                    lightingNeeds = lightingNeeds,
                    toxicityWarnings = toxicityWarnings
                )
            }
        } catch (e: Exception) {
            Log.e("GeminiService", "Exception during call: ${e.message}", e)
            CareGuideResult.Error("EXCEPTION", e.message ?: "An unexpected error occurred.")
        }
    }

    suspend fun askCustomQuestion(plantName: String, scientificName: String, question: String): CustomQuestionResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext CustomQuestionResult.Error("Gemini API Key is not configured. Please add your key in the Secrets panel with key 'GEMINI_API_KEY'.")
        }

        val prompt = """
            You are an expert botanist. The user is asking a question about their $plantName ($scientificName).
            Question: "$question"
            Provide a helpful, precise, custom answer directly addressing their question. Be concise and accurate.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = requestJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBody)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext CustomQuestionResult.Error("Gemini API returned error: ${response.code}")
                }
                val responseBody = response.body?.string() ?: return@withContext CustomQuestionResult.Error("Empty response from AI service.")
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext CustomQuestionResult.Error("No candidates returned.")
                }
                val textResponse = candidates
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text")

                CustomQuestionResult.Success(textResponse)
            }
        } catch (e: Exception) {
            CustomQuestionResult.Error(e.message ?: "An error occurred while calling the Gemini API.")
        }
    }
}

sealed class CareGuideResult {
    data class Success(
        val careInstructions: String,
        val lightingNeeds: String,
        val toxicityWarnings: String
    ) : CareGuideResult()
    data class Error(val code: String, val message: String) : CareGuideResult()
}

sealed class CustomQuestionResult {
    data class Success(val answer: String) : CustomQuestionResult()
    data class Error(val message: String) : CustomQuestionResult()
}
