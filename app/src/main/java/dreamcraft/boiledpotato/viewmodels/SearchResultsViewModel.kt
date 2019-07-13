package dreamcraft.boiledpotato.viewmodels

import android.util.SparseArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.repositories.RecipeRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.named

class SearchResultsViewModel : ViewModel(), KoinComponent {
    private val repository : RecipeRepository by inject()
    private val webApiResultsSize: Int by inject(named("webApiResultsSize"))
    public val recipesContainer = MutableLiveData<SparseArray<Recipe>>()

    init { recipesContainer.value = SparseArray() }

    fun getRecipes(searchKeywords: String, cuisine: String) {
        repository.searchRecipes(searchKeywords, cuisine, webApiResultsSize) { response: SparseArray<Recipe> ->
            for (i in 0 until response.size()) {
                recipesContainer.value?.let {
                    it.append(it.size(), response.valueAt(i))
                }
            }
            recipesContainer.value = recipesContainer.value // trigger observer notification
        }
    }
}