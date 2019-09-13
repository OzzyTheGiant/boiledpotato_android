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
    val recipeLiveData = MutableLiveData<Resource<Recipe>>()
    val favoriteLiveData = MutableLiveData<Resource<Boolean>>()

    fun getRecipeDetails() {
        repositoryJob = viewModelScope.launch(Dispatchers.IO) { // start a new co-routine for DB and Network operations
            val resource = repository.searchRecipeDetails(recipe)
            recipe = resource.data ?: recipe
            recipeLiveData.postValue(resource) // async LiveData update due to IO thread
        }
        recipeLiveData.value = Resource.Loading()
    }

    fun checkIfRecipeIsFavorite() {
        repositoryJob = viewModelScope.launch(Dispatchers.IO) {
            val resource = repository.checkIfRecipeIsFavorite(recipe.id)
            if (resource is Resource.Success) {
                recipe.isFavorite = resource.data!!
                favoriteLiveData.postValue(Resource.Success(false))
            }
        }
    }

    fun toggleRecipeAsFavorite() {
        repositoryJob = viewModelScope.launch(Dispatchers.IO) {
            val isFavorite = !recipe.isFavorite
            val resource = repository.toggleRecipeAsFavorite(recipe.id, isFavorite)
            if (resource is Resource.Success) recipe.isFavorite = isFavorite
            favoriteLiveData.postValue(resource)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repositoryJob.cancel() // cancel DB/API call if in progress
    }
}