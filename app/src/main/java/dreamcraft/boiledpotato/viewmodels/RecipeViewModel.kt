package dreamcraft.boiledpotato.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.repositories.RecipeRepository
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.utilities.CoroutineContextProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject

class RecipeViewModel : ViewModel(), KoinComponent {
    // dependencies
    private val repository : RecipeRepository by inject()
    private val ctxProvider: CoroutineContextProvider by inject()

    // LiveData observables
    var recipeLiveData = MutableLiveData<Resource<Recipe>>()
    var favoriteLiveData = MutableLiveData<Resource<Boolean>>()

    // this will come from SearchResultsActivity
    lateinit var recipe: Recipe

    fun getRecipeDetails() {
        recipeLiveData.value = Resource.Loading()

        viewModelScope.launch {
            val resource = withContext(ctxProvider.IO) {
                repository.searchRecipeDetails(recipe)
            }

            resource.data?.let {
                // mark recipe from resource as favorite, since that check happened async
                it.isFavorite = recipe.isFavorite
                recipe = resource.data
            }

            recipeLiveData.value = resource
        }
    }

    fun getFavoriteStatus() = viewModelScope.launch {
        val resource = withContext(ctxProvider.IO) {
            repository.checkIfRecipeIsFavorite(recipe.id)
        }

        if (resource is Resource.Success) {
            recipe.isFavorite = resource.data!!
            favoriteLiveData.value = Resource.Success(false)
        }
    }

    fun toggleFavoriteStatus() = viewModelScope.launch(ctxProvider.IO) {
        val isFavorite = !recipe.isFavorite
        val resource = withContext(ctxProvider.IO) {
            repository.toggleRecipeAsFavorite(recipe.id, isFavorite)
        }

        if (resource is Resource.Success) recipe.isFavorite = isFavorite
        favoriteLiveData.value = resource
    }
}