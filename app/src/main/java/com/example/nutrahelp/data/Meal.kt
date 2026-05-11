package com.example.nutrahelp.data

data class Meal(
    val name: String,
    val calories: Int,
    val proteinGrams: Int,
    val description: String,
    val category: MealCategory
)

enum class MealCategory(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner"),
    SNACK("Snack")
}
