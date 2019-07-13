package dreamcraft.boiledpotato.models

data class Recipe(
    val id: Int,
    val name: String,
    val prepMinutes: Int,
    val imageFileName: String
) {
    var servings = 0
    lateinit var ingredients: Array<Ingredient>
    lateinit var instructions: Array<RecipeInstructions>
}