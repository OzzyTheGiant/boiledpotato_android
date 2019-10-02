package dreamcraft.boiledpotato.viewmodels

import android.util.SparseArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.repositories.RecipeRepository
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.utilities.CoroutineContextProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class SearchResultsViewModel : ViewModel(), KoinComponent {
    // dependencies
    private val repository : RecipeRepository by inject()
    private val ctxProvider : CoroutineContextProvider by inject()
    private val maxResultsSize: Int by inject(named("webApiResultsSize"))

    // parcel data
    lateinit var searchKeywords: String
    lateinit var cuisine: String

    // Recipes data
    val resourceLiveData = MutableLiveData<Resource<RecipeSearchQuery>>()
    val recipes = SparseArray<Recipe>()
    var totalResults = 0

    fun fetchRecipes() {
        viewModelScope.launch {
            val resource = withContext(ctxProvider.IO) {
                if (searchKeywords != "favorites") {
                    repository.searchRecipes(searchKeywords, cuisine, maxResultsSize, recipes.size())
                } else {
                    repository.getFavoriteRecipes(maxResultsSize, recipes.size(), totalResults == 0)
                }
            }

            updateRecipeArray(resource)
            resourceLiveData.value = resource
        }

        resourceLiveData.value = Resource.Loading()
    }

    fun updateRecipeArray(resource: Resource<RecipeSearchQuery>) {
        if (resource is Resource.Success && resource.data != null) {
            if (totalResults == 0) totalResults = resource.data.totalResults

            // append recipes from current search query to master recipe array
            resource.data.recipes?.let { resultRecipes ->
                for (i in resultRecipes.indices) {
                    recipes.append(recipes.size(), resultRecipes[i]) // size = new key
                }
            }
        }
    }
}