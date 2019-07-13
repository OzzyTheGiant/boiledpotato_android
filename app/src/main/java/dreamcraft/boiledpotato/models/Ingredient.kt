package dreamcraft.boiledpotato.models

data class Ingredient(
    val name: String,
    val amount: Int,
    val unit: String,
    val forRecipe: Recipe
) {}