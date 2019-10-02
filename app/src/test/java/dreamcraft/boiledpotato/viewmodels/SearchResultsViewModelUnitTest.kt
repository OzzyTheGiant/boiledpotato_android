package dreamcraft.boiledpotato.viewmodels

import android.util.SparseArray
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import dreamcraft.boiledpotato.di.appTestModule
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.repositories.RecipeRepository
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.rules.TestCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SearchResultsViewModelUnitTest : KoinTest {
    private val viewModel : SearchResultsViewModel by inject()
    private val repository : RecipeRepository by inject()
    private val recipesList : List<Recipe> by inject()
    private val maxResultsSize : Int by inject(named("webApiResultsSize"))
    private val searchKeywords = "chicken"
    private val cuisine = "American"
    private val query = RecipeSearchQuery()
    private val recipesObserver : Observer<Resource<RecipeSearchQuery>> = mock()

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val testCoroutineRule = TestCoroutineRule()

    @Before fun setUp() {
        loadKoinModules(appTestModule)
        declareMock<RecipeRepository>()
        viewModel.searchKeywords = searchKeywords
        viewModel.cuisine = cuisine
        viewModel.resourceLiveData.observeForever(recipesObserver)
    }

    @Test fun fetchesRecipesFromRepository() = testCoroutineRule.runBlockingTest {
        val resource = Resource.Success(query)
        query.recipes = recipesList
        query.totalResults = 12
        whenever(repository.searchRecipes(searchKeywords, cuisine, maxResultsSize)).doReturn(resource)
        viewModel.fetchRecipes()

        // ensure all recipes were retrieved and in the array
        assertEquals(viewModel.recipes.size(), recipesList.size)
        assertEquals(query.totalResults, viewModel.totalResults)
        for (i in recipesList.indices) assertEquals(recipesList[i].id, viewModel.recipes.get(i).id)

        verify(repository, times(1)).searchRecipes(searchKeywords, cuisine, maxResultsSize)
        verify(recipesObserver, times(1)).onChanged(resource)
    }

    @After fun tearDown() {
        viewModel.resourceLiveData.removeObserver(recipesObserver)
        stopKoin()
    }
}