package dreamcraft.boiledpotato.database

import androidx.room.*
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
        WHERE S.SearchID = :searchId
    """)
    abstract suspend fun getRecipesByQuery(searchId : Long) : List<Recipe>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun saveRecipes(recipes: List<Recipe>)

    @Update abstract suspend fun updateRecipe(recipe: Recipe)

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