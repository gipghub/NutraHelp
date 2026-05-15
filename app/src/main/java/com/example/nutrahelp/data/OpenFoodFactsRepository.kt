package com.example.nutrahelp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

data class FoodSearchResult(
    val name: String,
    val caloriesPer100g: Int?,
    val proteinPer100g: Float?
)

object OpenFoodFactsRepository {
    private const val BASE_URL = "https://world.openfoodfacts.org/cgi/search.pl"

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
                val nutriments = product.optJSONObject("nutriments")
                val cal = nutriments?.optDouble("energy-kcal_100g")
                    ?.takeIf { !it.isNaN() && it > 0 }?.toInt()
                val protein = nutriments?.optDouble("proteins_100g")
                    ?.takeIf { !it.isNaN() && it >= 0 }?.toFloat()
                FoodSearchResult(name, cal, protein)
            }
        }.getOrDefault(emptyList())
    }
}