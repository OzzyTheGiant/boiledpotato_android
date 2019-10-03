package dreamcraft.boiledpotato.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import dreamcraft.boiledpotato.di.appModule
import dreamcraft.boiledpotato.di.appTestModule
import dreamcraft.boiledpotato.models.Favorite
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.IOException
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RecipeDAOUnitTest : KoinTest, CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default
    private val db : AppDatabase by inject()
    private val recipeDAO: RecipeDAO by inject()
    private val recipes: List<Recipe> by inject()
    private val query = RecipeSearchQuery().apply {
        keywords = "chicken"
        cuisine = "American"
        totalResults = 12
        expires = 1234567890L
    }

    @Before fun setUp() {
        unloadKoinModules(appModule)
        loadKoinModules(appTestModule)
    }

    @Throws(Exception::class)
    @Test fun savesAndRetrievesRecipeSearchQueries() {
        runBlocking {
            val id = recipeDAO.saveSearchQuery(query)
            val query = recipeDAO.getSearchQuery(query.keywords, query.cuisine)

            assertEquals(id, query!!.id)
            this@RecipeDAOUnitTest.apply {
                assertEquals(this.query.keywords, query.keywords)
                assertEquals(this.query.cuisine, query.cuisine)
                assertEquals(this.query.totalResults, query.totalResults)
                assertEquals(this.query.expires, query.expires)
            }
        }
    }

    @Throws(Exception::class)
    @Test fun savesAllDataAndUpdatesRecipesAndRetrievesRecipesByQuery() {
        runBlocking {
            recipeDAO.saveAll(query, recipes)
            recipes[0].ingredients = listOf("ing 1", "ing 2")
            recipes[0].instructions = listOf("ins 1", "ins 2")
            recipeDAO.updateRecipe(recipes[0])

            val query = recipeDAO.getSearchQuery(query.keywords, query.cuisine)
            val recipes = recipeDAO.getRecipesByQuery(query!!.id, 10, 0)

            this@RecipeDAOUnitTest.apply {
                assertEquals(this.recipes.size, recipes.size)
                assertEquals(this.recipes[0].ingredients!!.size, recipes[0].ingredients!!.size)
                assertEquals(this.recipes[0].instructions!!.size, recipes[0].instructions!!.size)
                for (i in this.recipes.indices) assertEquals(this.recipes[i].id, recipes[i].id)
            }
        }
    }

    @Throws(Exception::class)
    @Test fun addsAndChecksAndRemovesFavoriteStatusAndRetrievesAllFavoriteRecipes() {
        runBlocking {
            val favorite = Favorite(recipes[0].id)

            recipeDAO.saveRecipes(recipes)
            recipeDAO.addRecipeToFavorites(favorite)

            val isFavorite = recipeDAO.checkIfRecipeIsFavorite(recipes[0].id)
            val isNotFavorite = recipeDAO.checkIfRecipeIsFavorite(recipes[1].id)
            val favorites = recipeDAO.getFavoriteRecipes(10, 0)
            var favoriteCount = recipeDAO.getFavoriteRecipeCount()

            assertNotNull(isFavorite); assertNull(isNotFavorite)
            assertEquals(isFavorite!!.recipeId, recipes[0].id)
            assertEquals(1, favorites.size)
            assertEquals(favorites[0].id, recipes[0].id)
            assertEquals(1, favoriteCount)

            recipeDAO.removeRecipeFromFavorites(favorite)
            favoriteCount = recipeDAO.getFavoriteRecipeCount()

            assertEquals(0, favoriteCount)
        }
    }

    @After @Throws(IOException::class) fun tearDown() {
        db.close(); stopKoin()
    }
}