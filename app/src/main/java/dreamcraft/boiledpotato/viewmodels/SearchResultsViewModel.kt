package dreamcraft.boiledpotato.viewmodels

import android.util.SparseArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipesSearchResults
import dreamcraft.boiledpotato.repositories.RecipeRepository
import dreamcraft.boiledpotato.repositories.Resource
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class SearchResultsViewModel : ViewModel(), KoinComponent {
    // dependencies
    private val repository : RecipeRepository by inject()
    private val maxResultsSize: Int by inject(named("webApiResultsSize"))

    lateinit var searchKeywords: String
    lateinit var cuisine: String
    val resourceLiveData = MutableLiveData<Resource<RecipesSearchResults>>()
    val recipes = SparseArray<Recipe>()
    var totalResults = 0


    fun getRecipes() {
        resourceLiveData.value = repository.searchRecipes(searchKeywords, cuisine, maxResultsSize, recipes.size())
        { response ->
            if (response is Resource.Success) {
                response.data?.let {
                    totalResults = it.totalResults
                    for (i in 0 until it.recipes.size()) {
                        recipes.append(recipes.size(), it.recipes.valueAt(i)) // size = new key
                    }
                }
            }
            resourceLiveData.value = response
        }
    }
}