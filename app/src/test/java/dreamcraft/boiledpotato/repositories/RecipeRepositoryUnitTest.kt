package dreamcraft.boiledpotato.repositories

import com.nhaarman.mockitokotlin2.*
import dreamcraft.boiledpotato.database.RecipeDAO
import dreamcraft.boiledpotato.di.appModule
import dreamcraft.boiledpotato.di.appTestModule
import dreamcraft.boiledpotato.models.Favorite
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.rules.TestCoroutineRule
import dreamcraft.boiledpotato.services.RestApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get
import org.koin.test.mock.declareMock
import retrofit2.Response
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class RecipeRepositoryUnitTest : KoinTest {
    private lateinit var repository: RecipeRepository
    private lateinit var recipeDAO: RecipeDAO
    private lateinit var restApiService: RestApiService
    private lateinit var recipes: List<Recipe>
    private var maxResultsSize: Int = 0
    private val searchKeywords = "chicken"
    private val cuisine = "American"
    private val query = RecipeSearchQuery(1, searchKeywords, cuisine, 12, 999999999999999)

    @get:Rule val testCoroutineRule = TestCoroutineRule()

    @Before fun setUp() {
        startKoin {
            modules(listOf(appModule, appTestModule))
            declareMock<RestApiService>()
            declareMock<RecipeDAO>()
        }

        repository = get()
        recipeDAO = get()
        restApiService = get()
        recipes = get()
        maxResultsSize = get(named("webApiResultsSize"))
    }

    @Test fun returnsRecipesFromDatabase() = testCoroutineRule.runBlockingTest {
        whenever(recipeDAO.getSearchQuery(searchKeywords, cuisine)).doReturn(query)
        whenever(recipeDAO.getRecipesByQuery(query.id, maxResultsSize, 0)).doReturn(recipes)
        val resource = repository.searchRecipes(searchKeywords, cuisine, maxResultsSize)

        assert(resource is Resource.Success)
        assertEquals(recipes, resource.data!!.recipes)
        assertEquals(resource.data!!.recipes!!.size, query.recipes!!.size)
        verify(recipeDAO, times(1)).getSearchQuery(searchKeywords, cuisine)
        verify(recipeDAO, times(1)).getRecipesByQuery(query.id, maxResultsSize, 0)
    }

    @Test fun returnsRecipesFromRestApiIfQueryingForFirstTime() = testCoroutineRule.runBlockingTest {
        val response = Response.success(query)
        query.recipes = recipes

        whenever(recipeDAO.getSearchQuery(searchKeywords, cuisine)).doReturn(null)
        whenever(restApiService.getRecipes(searchKeywords, cuisine, maxResultsSize, 0)).doReturn(response)
        val resource = repository.searchRecipes(searchKeywords, cuisine, maxResultsSize)

        assertResource(resource)
        verify(recipeDAO, times(1)).getSearchQuery(searchKeywords, cuisine)
        verify(restApiService, times(1)).getRecipes(searchKeywords, cuisine, maxResultsSize, 0)
        verify(recipeDAO, times(1)).saveAll(resource.data!!, query.recipes)
    }

    @Test fun returnsRecipesFromRestApiIfQueryHasNoRecipes() = testCoroutineRule.runBlockingTest {
        val response = Response.success(query)
        query.recipes = recipes

        whenever(recipeDAO.getSearchQuery(searchKeywords, cuisine)).doReturn(query)
        whenever(recipeDAO.getRecipesByQuery(query.id, maxResultsSize, 0)).doReturn(listOf())
        whenever(restApiService.getRecipes(searchKeywords, cuisine, maxResultsSize, 0)).doReturn(response)
        val resource = repository.searchRecipes(searchKeywords, cuisine, maxResultsSize)

        assertResource(resource)
        verify(recipeDAO, times(1)).getSearchQuery(searchKeywords, cuisine)
        verify(restApiService, times(1)).getRecipes(searchKeywords, cuisine, maxResultsSize, 0)
        verify(recipeDAO, times(1)).getRecipesByQuery(query.id, maxResultsSize, 0)
        verify(recipeDAO, times(1)).saveAll(resource.data!!, query.recipes)
    }

    private fun assertResource(resource: Resource<RecipeSearchQuery>) {
        assert(resource is Resource.Success)
        assertEquals(recipes, resource.data!!.recipes)
        assertEquals(resource.data!!.recipes!!.size, query.recipes!!.size)
    }

    @Test fun returnsErrorResourceIfApiErrorOccursWhenFetchingRecipes() = testCoroutineRule.runBlockingTest {
        whenever(recipeDAO.getSearchQuery(searchKeywords, cuisine)).doReturn(null)
        whenever(restApiService.getRecipes(searchKeywords, cuisine, maxResultsSize, 0)).doAnswer {
            throw IOException()
        }

        val resource = repository.searchRecipes(searchKeywords, cuisine, maxResultsSize)

        assert(resource is Resource.Error)
        assertEquals("000", resource.message!!)

        verify(recipeDAO, times(1)).getSearchQuery(searchKeywords, cuisine)
        verify(restApiService, times(1)).getRecipes(searchKeywords, cuisine, maxResultsSize, 0)
    }

    @Test fun returnsRecipeDetailsFromRestAPI() = testCoroutineRule.runBlockingTest {
        val recipe = Recipe().apply{
            ingredients = listOf("ing 1", "ing 2")
            instructions = listOf("ins 1", "ins 2")
        }

        whenever(restApiService.getRecipeInformation(1)).doReturn(Response.success(recipe))
        val resource = repository.searchRecipeDetails(recipes[1])

        assert(resource is Resource.Success)
        assertEquals(resource.data!!.id, recipes[1].id)
        assertNotNull(resource.data!!.ingredients)
        assertNotNull(resource.data!!.instructions)
        verify(restApiService, times(1)).getRecipeInformation(1)
        verify(recipeDAO, times(1)).updateRecipe(resource.data!!)
    }

    @Test fun returnsErrorResourceIfApiErrorOccursWhenFetchingRecipeDetails() = testCoroutineRule.runBlockingTest {
        whenever(restApiService.getRecipeInformation(recipes[0].id)).doAnswer {
            throw IOException()
        }

        val resource = repository.searchRecipeDetails(recipes[0])
        assert(resource is Resource.Error)
        assertEquals("000", resource.message!!)

        verify(restApiService, times(1)).getRecipeInformation(recipes[0].id)
    }

    @Test fun returnsFavoriteRecipesFromDatabase() = testCoroutineRule.runBlockingTest {
        whenever(recipeDAO.getFavoriteRecipes(maxResultsSize, 0)).doReturn(recipes)
        whenever(recipeDAO.getFavoriteRecipeCount()).doReturn(12)
        val resource = repository.getFavoriteRecipes(maxResultsSize, 0, true)

        assert(resource is Resource.Success)
        assertEquals(resource.data!!.totalResults, 12)
        assertEquals(resource.data!!.recipes!!.size, recipes.size)
        verify(recipeDAO, times(1)).getFavoriteRecipes(maxResultsSize, 0)
        verify(recipeDAO, times(1)).getFavoriteRecipeCount()
    }

    @Test fun returnsErrorResourceIfNoFavoriteRecipesFound() = testCoroutineRule.runBlockingTest {
        whenever(recipeDAO.getFavoriteRecipes(maxResultsSize, 0)).doReturn(listOf())
        val resource = repository.getFavoriteRecipes(maxResultsSize, 0, false)

        assert(resource is Resource.Error)
        assertEquals("400", resource.message!!)
        verify(recipeDAO, times(1)).getFavoriteRecipes(maxResultsSize, 0)
    }

    @Test fun returnsRecipeFavoriteStatus () = testCoroutineRule.runBlockingTest {
        val id = recipes[0].id
        whenever(recipeDAO.checkIfRecipeIsFavorite(id)).doReturn(Favorite(id))
        val resource = repository.checkIfRecipeIsFavorite(id)

        assert(resource is Resource.Success)
        assert(resource.data!!)
        verify(recipeDAO, times(1)).checkIfRecipeIsFavorite(id)
    }

    @Test fun updatesRecipeFavoriteStatus() = testCoroutineRule.runBlockingTest {
        // test favorite was added
        var resource: Resource<Boolean> = repository.toggleRecipeAsFavorite(recipes[0].id, true)
        assert(resource is Resource.Success)
        assert(resource.data!!)
        verify(recipeDAO, times(1)).addRecipeToFavorites(any())

        // test favorite was removed
        resource = repository.toggleRecipeAsFavorite(recipes[0].id, false)
        assert(resource is Resource.Success)
        assert(resource.data!!)
        verify(recipeDAO, times(1)).removeRecipeFromFavorites(any())

        // test db error returned
        whenever(recipeDAO.addRecipeToFavorites(any())).doAnswer { throw IOException() }
        resource = repository.toggleRecipeAsFavorite(recipes[0].id, true)
        assert(resource is Resource.Error)
        assertEquals("400", resource.message!!)
        verify(recipeDAO, times(2)).addRecipeToFavorites(any())
    }

    @After fun tearDown() {
        query.recipes = null
        stopKoin()
    }
}