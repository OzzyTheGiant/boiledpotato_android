package dreamcraft.boiledpotato.viewmodels

import android.util.SparseArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.repositories.RecipeRepository
import dreamcraft.boiledpotato.repositories.Resource
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class SearchResultsViewModel : ViewModel(), KoinComponent {
    private val repository : RecipeRepository by inject()
    private val webApiResultsSize: Int by inject(named("webApiResultsSize"))
    public val recipes = SparseArray<Recipe>()
    public val resourceLiveData = MutableLiveData<Resource<SparseArray<Recipe>>>()

    fun getRecipes(searchKeywords: String, cuisine: String) {
        resourceLiveData.value = repository.searchRecipes(searchKeywords, cuisine, webApiResultsSize) { response ->
            if (response is Resource.Success) {
                response.data?.let {
                    for (i in 0 until it.size()) {
                        recipes.append(recipes.size(), it.valueAt(i)) // size = new key
                    }
                }
            }
            resourceLiveData.value = response
        }
    }
}