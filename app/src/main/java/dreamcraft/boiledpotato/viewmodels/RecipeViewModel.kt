package dreamcraft.boiledpotato.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.JsonRecipeDetails
import dreamcraft.boiledpotato.repositories.RecipeRepository
import dreamcraft.boiledpotato.repositories.Resource
import org.koin.core.KoinComponent
import org.koin.core.inject

class RecipeViewModel : ViewModel(), KoinComponent {
    private val repository : RecipeRepository by inject()
    lateinit var recipe: Recipe
    val resourceLiveData = MutableLiveData<Resource<JsonRecipeDetails>>()

    fun getRecipeDetails() {
        resourceLiveData.value = repository.searchRecipeDetails(recipe.id) { response ->
            response.data?.let { recipeDetails ->
                recipe.ingredients = recipeDetails.ingredients
                recipe.instructions = recipeDetails.instructions
                recipe.servings = recipeDetails.servings
            }
            resourceLiveData.value = response
        }
    }
}