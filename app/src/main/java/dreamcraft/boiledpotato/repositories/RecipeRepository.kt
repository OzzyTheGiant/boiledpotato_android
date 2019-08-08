package dreamcraft.boiledpotato.repositories

import dreamcraft.boiledpotato.models.RecipesSearchResults
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
        callback: (Resource<RecipesSearchResults>) -> Unit
    ) : Resource<RecipesSearchResults> {
        restApiService
            .getRecipes(searchKeywords, cuisine, resultSize, offset)
            .enqueue(RestApiServiceCallback(callback))
        return Resource.Loading()
    }
}