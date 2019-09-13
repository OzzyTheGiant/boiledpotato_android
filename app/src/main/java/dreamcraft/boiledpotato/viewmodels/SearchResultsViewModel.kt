package dreamcraft.boiledpotato.viewmodels

import android.util.SparseArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.repositories.RecipeRepository
import dreamcraft.boiledpotato.repositories.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class SearchResultsViewModel : ViewModel(), KoinComponent {
    // dependencies
    private val repository : RecipeRepository by inject()
    private val maxResultsSize: Int by inject(named("webApiResultsSize"))

    private lateinit var repositoryJob: Job
    lateinit var searchKeywords: String
    lateinit var cuisine: String
    val resourceLiveData = MutableLiveData<Resource<RecipeSearchQuery>>()
    val recipes = SparseArray<Recipe>()
    var totalResults = 0

    fun getRecipes() {
        repositoryJob = viewModelScope.launch(Dispatchers.IO) {
            val resource = if (searchKeywords != "favorites") {
                repository.searchRecipes(searchKeywords, cuisine, maxResultsSize, recipes.size())
            } else repository.getFavoriteRecipes(maxResultsSize, recipes.size())

            if (resource is Resource.Success && resource.data != null) {
                totalResults = resource.data.totalResults

                // append recipes from current search query to master recipe array
                resource.data.recipes?.let { resultRecipes ->
                    for (i in resultRecipes.indices) {
                        recipes.append(recipes.size(), resultRecipes[i]) // size = new key
                    }
                }
            }

            resourceLiveData.postValue(resource)  // set value async since operating on co-routine
        }

        resourceLiveData.value = Resource.Loading()
    }

    override fun onCleared() {
        super.onCleared()
        repositoryJob.cancel() // cancel DB/API call if in progress
    }
}