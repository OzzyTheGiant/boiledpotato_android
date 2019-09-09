package dreamcraft.boiledpotato.services

import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestApiService {
    @GET("/recipes/search")
    suspend fun getRecipes(
        @Query("query") queryText: String,
        @Query("cuisine") cuisine: String,
        @Query("number") listSize: Int,
        @Query("offset") offset: Int
    ): Response<RecipeSearchQuery>

    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(@Path("id") recipeId: Long): Response<Recipe>
}