package dreamcraft.boiledpotato.viewmodels

import org.koin.core.inject
import org.koin.core.qualifier.named

class RecipeViewModelStub : RecipeViewModel() {
    private val testIngredients : List<String> by inject(named("ingredients"))
    private val testInstructions : List<String> by inject(named("instructions"))

    var getRecipeDetailsCalls = 0
    var checkIfRecipeIsFavoriteCalls = 0
    var toggleRecipeAsFavoriteCalls = 0

    override fun getRecipeDetails() {
        getRecipeDetailsCalls++
        recipe.ingredients = testIngredients
        recipe.instructions = testInstructions
    }

    override fun checkIfRecipeIsFavorite() {
        checkIfRecipeIsFavoriteCalls++
        recipe.isFavorite = true
    }

    override fun toggleRecipeAsFavorite() {
        toggleRecipeAsFavoriteCalls++
        recipe.isFavorite = !recipe.isFavorite
    }
}