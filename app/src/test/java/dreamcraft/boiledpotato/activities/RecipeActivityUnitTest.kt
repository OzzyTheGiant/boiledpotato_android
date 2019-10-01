package dreamcraft.boiledpotato.activities

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.RobolectricApplication
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.viewmodels.RecipeViewModel
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock

@RunWith(AndroidJUnit4::class)
class RecipeActivityUnitTest : KoinTest {
    private val app = getApplicationContext<RobolectricApplication>()
    private lateinit var scenario: ActivityScenario<RecipeActivity>
    private val sampleRecipe = Recipe(1L, "Chicken Soup", 60, "chicken-soup.jpg")

    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        val intent = Intent(app, RecipeActivity::class.java)
        intent.putExtra(IntentExtras.RECIPE, sampleRecipe)

        declareMock<RecipeViewModel> {
            // stub properties (you can do this as they have getters and setters)
            whenever(recipeLiveData).doReturn(MutableLiveData())
            whenever(favoriteLiveData).doReturn(MutableLiveData())
            whenever(recipe).doReturn(sampleRecipe)
        }

        scenario = ActivityScenario.launch(intent)
    }

    @Test fun displaysProvidedRecipeDataOnStartUp() {

        // check parcel Recipe data came in and filled UI
        onView(withId(R.id.recipe_name)).check(matches(withText(sampleRecipe.name)))
        onView(withId(R.id.servings)).check(matches(withText("${sampleRecipe.servings} Servings")))
        onView(withId(R.id.prep_time)).check(matches(withText("${sampleRecipe.prepMinutes} min")))

        // check ingredients and instructions placeholders are visible
        onView(withId(R.id.ingredients_placeholder)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.instructions_placeholder)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test fun displaysIngredientsAndInstructionsAfterFetchingData() {
        scenario.onActivity { activity ->
            val resource = Resource.Success(sampleRecipe)
            activity.processRecipeResource(resource)

            // check ingredients and instructions visible and placeholders are gone
            onView(withId(R.id.ingredients_list)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.ingredients_placeholder)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.instructions_list)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.instructions_placeholder)).check(matches(withEffectiveVisibility(Visibility.GONE)))

            // check that ingredients and instructions are set after calling repository
            activity.viewModel.recipe.ingredients?.let { for (i in it.indices) {
                onView(withId(R.id.ingredients_list)).check(matches(withText(containsString(it[i]))))
            }}
            activity.viewModel.recipe.instructions?.let { for (i in it.indices) {
                onView(withId(R.id.instructions_list)).check(matches(withText(containsString(it[i]))))
            }}

            // check that repository methods were called
            verify(activity.viewModel, times(1)).getRecipeDetails()
            verify(activity.viewModel, times(1)).getFavoriteStatus()
        }
    }

    @Test fun togglesFavoriteIcon() {
        scenario.onActivity { activity ->
            activity.processFavoriteResource(Resource.Success(false))

            // test favorite button toggles isFavorite in Recipe
            onView(withId(R.id.button_favorite)).perform(click())
            verify(activity.viewModel, times(1)).toggleFavoriteStatus()
            activity.processFavoriteResource(Resource.Success(false))
        }
    }

    @After fun tearDown() = stopKoin()
}