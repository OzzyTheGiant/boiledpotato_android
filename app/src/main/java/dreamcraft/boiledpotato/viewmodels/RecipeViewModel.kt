package dreamcraft.boiledpotato.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.repositories.RecipeRepository
import dreamcraft.boiledpotato.repositories.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class RecipeViewModel : ViewModel(), KoinComponent {
    private val repository : RecipeRepository by inject()
    private lateinit var repositoryJob : Job
    lateinit var recipe: Recipe // this will come from SearchResultsActivity
    val resourceLiveData = MutableLiveData<Resource<Recipe>>()

    fun getRecipeDetails() {
        repositoryJob = viewModelScope.launch(Dispatchers.IO) { // start a new co-routine for DB and Network operations
            val resource = repository.searchRecipeDetails(recipe)
            recipe = resource.data ?: recipe
            resourceLiveData.postValue(resource) // async LiveData update due to IO thread
        }
        resourceLiveData.value = Resource.Loading()
    }

    override fun onCleared() {
        super.onCleared()
        repositoryJob.cancel() // cancel DB/API call if in progress
    }
}