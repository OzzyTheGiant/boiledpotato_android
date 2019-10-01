package dreamcraft.boiledpotato.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import dreamcraft.boiledpotato.di.appTestModule
import dreamcraft.boiledpotato.models.Recipe
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
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RecipeViewModelUnitTest : KoinTest {
    private val viewModel : RecipeViewModel by inject()
    private val repository : RecipeRepository by inject()
    private val recipe : Recipe by inject()
    private val recipeObserver : Observer<Resource<Recipe>> = mock()
    private val favoriteObserver : Observer<Resource<Boolean>> = mock()

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule val testCoroutineRule = TestCoroutineRule()

    @Before fun setUp() {
        loadKoinModules(appTestModule)
        declareMock<RecipeRepository>()
        viewModel.recipe = recipe
        viewModel.recipeLiveData.observeForever(recipeObserver)
        viewModel.favoriteLiveData.observeForever(favoriteObserver)
    }

    @Test fun getsIngredientsAndInstructionsFromRepository() = testCoroutineRule.runBlockingTest {
        val resource = Resource.Success(recipe)
        whenever(repository.searchRecipeDetails(recipe)).doReturn(resource)
        viewModel.getRecipeDetails()

        assertEquals(viewModel.recipe.ingredients, resource.data?.ingredients)
        assertEquals(viewModel.recipe.instructions, resource.data?.instructions)

        // assert any observer of this LiveData received the value update
        verify(recipeObserver, times(1)).onChanged(resource)
    }

    @Test fun checksIfRecipeIsFavorite() = testCoroutineRule.runBlockingTest {
        whenever(repository.checkIfRecipeIsFavorite(viewModel.recipe.id)).doReturn(Resource.Success(true))
        viewModel.getFavoriteStatus()
        assert(viewModel.recipe.isFavorite)
        verify(favoriteObserver, times(1)).onChanged(any())
    }

    @Test fun togglesFavoriteStatus() = testCoroutineRule.runBlockingTest {
        val resource = Resource.Success(true)
        whenever(repository.toggleRecipeAsFavorite(viewModel.recipe.id, true)).doReturn(resource)
        viewModel.toggleFavoriteStatus()
        assert(viewModel.recipe.isFavorite)
        verify(favoriteObserver, times(1)).onChanged(resource)
    }

    @After fun tearDown() {
        viewModel.recipeLiveData.removeObserver(recipeObserver)
        viewModel.favoriteLiveData.removeObserver(favoriteObserver)
        stopKoin()
    }
}