package dreamcraft.boiledpotato.services

import dreamcraft.boiledpotato.models.RecipesSearchResults
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestApiService {
    @GET("/recipes/search")
    fun getRecipes(
        @Query("query") queryText: String,
        @Query("cuisine") cuisine: String,
        @Query("number") listSize: Int,
        @Query("offset") offset: Int
    ): Call<RecipesSearchResults>

    @GET("recipes/{id}/analyzedInstructions?stepBreakdown=false")
    fun getRecipeInstructions(@Path("id") recipeId: Int)

    @GET("recipes/{id}/ingredientWidget.json")
    fun getIngredients(@Path("id") recipeId: Int)
}