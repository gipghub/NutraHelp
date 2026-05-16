package com.example.nutrahelp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

data class FoodSearchResult(
    val name: String,
    val caloriesPer100g: Int?,
    val proteinPer100g: Float?,
    val carbsPer100g: Float?,
    val fatPer100g: Float?,
    val fiberPer100g: Float? = null,
    val sugarsPer100g: Float? = null,
    val sodiumPer100g: Float? = null
)

object OpenFoodFactsRepository {
    private const val BASE_URL = "https://world.openfoodfacts.org/cgi/search.pl"

    suspend fun searchByBarcode(barcode: String): FoodSearchResult? = withContext(Dispatchers.IO) {
        try {
            val json = URL("https://world.openfoodfacts.org/api/v0/product/$barcode.json").readText()
            val root = JSONObject(json)
            if (root.optInt("status") != 1) return@withContext null
            val product = root.optJSONObject("product") ?: return@withContext null
            val name = product.optString("product_name").trim()
            if (name.isBlank()) return@withContext null
            val n = product.optJSONObject("nutriments")
            FoodSearchResult(
                name = name,
                caloriesPer100g = n?.optDouble("energy-kcal_100g")?.takeIf { !it.isNaN() && it > 0 }?.toInt(),
                proteinPer100g = n?.optDouble("proteins_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat(),
                carbsPer100g = n?.optDouble("carbohydrates_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat(),
                fatPer100g = n?.optDouble("fat_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat(),
                fiberPer100g = n?.optDouble("fiber_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat(),
                sugarsPer100g = n?.optDouble("sugars_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat(),
                sodiumPer100g = n?.optDouble("sodium_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat()
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun search(query: String): List<FoodSearchResult> = withContext(Dispatchers.IO) {
        runCatching {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val url = "$BASE_URL?search_terms=$encoded&action=process&json=1" +
                "&fields=product_name,nutriments&page_size=10"
            val json = URL(url).readText()
            val root = JSONObject(json)
            val products = root.getJSONArray("products")
            (0 until products.length()).mapNotNull { i ->
                val product = products.getJSONObject(i)
                val name = product.optString("product_name").trim()
                if (name.isBlank()) return@mapNotNull null
                val n = product.optJSONObject("nutriments")
                val cal = n?.optDouble("energy-kcal_100g")?.takeIf { !it.isNaN() && it > 0 }?.toInt()
                val protein = n?.optDouble("proteins_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat()
                val carbs = n?.optDouble("carbohydrates_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat()
                val fat = n?.optDouble("fat_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat()
                val fiber = n?.optDouble("fiber_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat()
                val sugars = n?.optDouble("sugars_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat()
                val sodium = n?.optDouble("sodium_100g")?.takeIf { !it.isNaN() && it >= 0 }?.toFloat()
                FoodSearchResult(name, cal, protein, carbs, fat, fiber, sugars, sodium)
            }
        }.getOrDefault(emptyList())
    }
}