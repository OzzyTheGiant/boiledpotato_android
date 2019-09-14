package dreamcraft.boiledpotato.database

import androidx.room.*
import dreamcraft.boiledpotato.models.Favorite
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.models.RecipeSearchResults

@Dao abstract class RecipeDAO {
    // SearchQuery DB queries
    @Query("""SELECT * FROM SearchQueries WHERE Keywords = :keywords AND Cuisine = :cuisine""")
    abstract suspend fun getSearchQuery(keywords: String, cuisine: String) : RecipeSearchQuery?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveSearchQuery(query: RecipeSearchQuery) : Long

    // SearchResults DB queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveSearchResults(results: List<RecipeSearchResults>)

    // Recipe DB queries
    @Query("""
        SELECT R.* FROM Recipes as R JOIN SearchResults as S ON R.ID = S.RecipeID
        WHERE S.SearchID = :searchId LIMIT :limit OFFSET :offset
    """)
    abstract suspend fun getRecipesByQuery(searchId : Long, limit: Int, offset: Int) : List<Recipe>

    @Query("SELECT * FROM Recipes as R JOIN Favorites as F ON R.ID = F.RecipeID LIMIT :limit OFFSET :offset")
    abstract suspend fun getFavoriteRecipes(limit: Int, offset: Int) : List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveRecipes(recipes: List<Recipe>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun updateRecipe(recipe: Recipe)

    // Favorites queries
    @Query("SELECT * FROM Favorites WHERE RecipeID = :recipeId")
    abstract suspend fun checkIfRecipeIsFavorite(recipeId: Long) : Favorite?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun addRecipeToFavorites(recipe: Favorite)

    @Query("SELECT COUNT(1) FROM Favorites")
    abstract fun getFavoriteRecipeCount(): Int

    @Delete abstract suspend fun removeRecipeFromFavorites(recipe: Favorite)

    /** save search query metadata, list of recipes, and results mapping recipes to query,
     *  while generating new query ID */
    suspend fun saveAll(query: RecipeSearchQuery, recipes: List<Recipe>?) {
        val searchResults : List<RecipeSearchResults> // maps Query to Recipe results

        query.id = saveSearchQuery(query)

        if (recipes != null) {
            saveRecipes(recipes)

            if (recipes.isNotEmpty()) { // check if recipes were actually found based on keywords
                searchResults = ArrayList()

                recipes.forEach { recipe ->
                    // append SearchResult object with recipe id and search query id to array
                    searchResults.add(RecipeSearchResults(query.id, recipe.id))
                }

                saveSearchResults(searchResults)
            }
        }
    }
}