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
import com.nhaarman.mockitokotlin2.*
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.RobolectricApplication
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.viewmodels.RecipeViewModel
import kotlinx.android.synthetic.main.toast_error.view.*
import org.hamcrest.CoreMatchers.containsString
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.mock.declareMock
import org.robolectric.shadows.ShadowToast

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

    @Test fun displaysErrorMessageWhenRecipeDataCouldNotBeFetched() {
        scenario.onActivity { activity ->
            // test Network Error message displayed
            activity.processRecipeResource(Resource.Error("000"))
            onView(withId(R.id.ingredients_placeholder)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.instructions_placeholder)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.error_text)).check(matches(
                withText(activity.getString(R.string.NETWORK_ERROR) + ": " + activity.getString(R.string.try_again))
            ))

            // perform click on Retry button to redisplay loading indicators
            onView(withId(R.id.button_retry)).perform(click())
            verify(activity.viewModel, times(2)).getRecipeDetails()

            activity.processRecipeResource(Resource.Loading())
            onView(withId(R.id.ingredients_placeholder)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.instructions_placeholder)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }

    @Test fun togglesFavoriteStatus() {
        scenario.onActivity { activity ->
            onView(withId(R.id.button_favorite)).perform(click())
            verify(activity.viewModel, times(1)).toggleFavoriteStatus()

            // check recipe marked as favorite
            sampleRecipe.isFavorite = true
            activity.processFavoriteResource(Resource.Success(true))
            assertEquals(ShadowToast.getTextOfLatestToast(), activity.getString(R.string.MARKED_FAVORITE))

            // check recipe favorite status removed
            sampleRecipe.isFavorite = false
            activity.processFavoriteResource(Resource.Success(true))
            assertEquals(ShadowToast.getTextOfLatestToast(), activity.getString(R.string.MARKED_UNFAVORITE))

            // check error message given if an error occurred
            activity.processFavoriteResource(Resource.Error("000"))
            assertEquals(ShadowToast.getLatestToast().view.toast_error_message.text, activity.getString(R.string.DATA_ERROR))
        }
    }

    @After fun tearDown() = stopKoin()
}