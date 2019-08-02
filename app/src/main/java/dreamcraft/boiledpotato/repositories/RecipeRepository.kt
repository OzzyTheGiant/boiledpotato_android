package dreamcraft.boiledpotato.repositories

import android.util.SparseArray
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.services.RestApiService
import dreamcraft.boiledpotato.services.RestApiServiceCallback
import org.koin.core.KoinComponent
import org.koin.core.inject

class RecipeRepository : KoinComponent {
    private val restApiService : RestApiService by inject()

    fun searchRecipes(
        searchKeywords: String,
        cuisine: String,
        resultSize: Int,
        offset: Int = 0,
        callback: (Resource<SparseArray<Recipe>>) -> Unit
    ) : Resource<SparseArray<Recipe>> {
        restApiService
            .getRecipes(searchKeywords, cuisine, resultSize, offset)
            .enqueue(RestApiServiceCallback<SparseArray<Recipe>>(callback))
        return Resource.Loading<SparseArray<Recipe>>()
    }
}