package dreamcraft.boiledpotato.repositories

import dreamcraft.boiledpotato.database.RecipeDAO
import dreamcraft.boiledpotato.models.*
import dreamcraft.boiledpotato.services.RestApiService
import org.koin.core.KoinComponent
import org.koin.core.inject

class RecipeRepository : Repository(), KoinComponent {
    // dependencies
    private val restApiService : RestApiService by inject()
    private val recipeDao: RecipeDAO by inject()

    /** fetch recipes from database or download from REST API if search never done before */
    suspend fun searchRecipes(keywords: String, cuisine: String, resultSize: Int, offset: Int = 0)
        : Resource<RecipeSearchQuery> {

        val apiResource : Resource<RecipeSearchQuery> // this contains retrofit response data
        val recipes : List<Recipe>
        var query = recipeDao.getSearchQuery(keywords, cuisine)

        recipes = if (query != null) // if query was previously made, get recipes in database
            recipeDao.getRecipesByQuery(query.id, resultSize, offset) else ArrayList()

        // Check if this search query has never been done before or if the data is stale
        if (query == null || query.isStale() || recipes.isEmpty()) {
            apiResource = callApi { restApiService.getRecipes(keywords, cuisine, resultSize, offset) }

            // save data or return Retrofit error in Resource if response successful and has data
            if (apiResource is Resource.Success && apiResource.data != null) {
                apiResource.data.keywords = keywords
                apiResource.data.cuisine = cuisine
                recipeDao.saveAll(apiResource.data, apiResource.data.recipes)
                query = apiResource.data
            } else if (query == null) return apiResource // if resource has error, skip and return cached data
        }

        if (recipes.isNotEmpty()) query.recipes = recipes

        // check if search query was performed before but no recipes are saved to the DB
        return if (query.recipes!!.isEmpty()) Resource.Error("404") else Resource.Success(query)
    }

    /** fetch Recipe with ingredients and instructions added */
    suspend fun searchRecipeDetails(recipe: Recipe): Resource<Recipe> {
        val resource : Resource<Recipe> = callApi { restApiService.getRecipeInformation(recipe.id) }

        resource.data?.let {
            it.id = recipe.id
            it.name = recipe.name
            it.prepMinutes = recipe.prepMinutes
            it.imageFileName = recipe.imageFileName
            recipeDao.updateRecipe(it)
        }

        return resource
    }

    /** fetch favorite recipes from database */
    suspend fun getFavoriteRecipes(resultSize: Int, offset: Int) : Resource<RecipeSearchQuery> {
        val query = RecipeSearchQuery()
        query.recipes = recipeDao.getFavoriteRecipes(resultSize, offset)
        return if (query.recipes!!.isEmpty()) Resource.Error("400") else Resource.Success(query)
    }

    /** add or remove recipe from Favorites database table */
    suspend fun toggleRecipeAsFavorite(id: Long, isFavorite: Boolean): Resource<Boolean> {
        return try {
            val favorite = Favorite(id)

            if (isFavorite) {
                recipeDao.addRecipeToFavorites(favorite)
            } else {
                recipeDao.removeRecipeFromFavorites(favorite)
            }

            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error("400")
        }
    }

    /** check if Recipe was added to Favorites database table */
    suspend fun checkIfRecipeIsFavorite(id: Long) : Resource<Boolean> {
        return Resource.Success(recipeDao.checkIfRecipeIsFavorite(id) != null)
    }
}