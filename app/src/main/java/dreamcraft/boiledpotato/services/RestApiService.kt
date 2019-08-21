package dreamcraft.boiledpotato.services

import dreamcraft.boiledpotato.models.JsonRecipeDetails
import dreamcraft.boiledpotato.models.JsonRecipesList
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
    ): Call<JsonRecipesList>

    @GET("recipes/{id}/information")
    fun getRecipeInformation(@Path("id") recipeId: Int): Call<JsonRecipeDetails>
}