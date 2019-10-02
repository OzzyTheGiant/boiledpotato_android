package dreamcraft.boiledpotato.activities

import android.content.Intent
import android.util.SparseArray
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.RobolectricApplication
import dreamcraft.boiledpotato.di.appTestModule
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.viewholders.SearchResultViewHolder
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import kotlinx.android.synthetic.main.activity_search_results.*
import kotlinx.android.synthetic.main.error_message.*
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.robolectric.Shadows.shadowOf

@RunWith(AndroidJUnit4::class)
class SearchResultsActivityUnitTest : KoinTest {
    private var app = getApplicationContext<RobolectricApplication>()
    private lateinit var scenario : ActivityScenario<SearchResultsActivity>

    // ViewModel data
    private val searchKeywords = "chicken"
    private val cuisine = ""
    private val query = RecipeSearchQuery(1, searchKeywords, cuisine, 12, 1234567890)
    private val recipeList : List<Recipe> by inject()
    private val recipeArray : SparseArray<Recipe> by inject()
    private val recipesLiveData = MutableLiveData<Resource<RecipeSearchQuery>>()

    @get:Rule val testRule = TestName()
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before fun setUp() {
        val intent = Intent(app, SearchResultsActivity::class.java)
        intent.putExtra(IntentExtras.SEARCH, searchKeywords)
        intent.putExtra(IntentExtras.CUISINE, cuisine)

        // set up mocks
        loadKoinModules(appTestModule)
        declareMock<SearchResultsViewModel> {
            whenever(resourceLiveData).doReturn(recipesLiveData)
            whenever(totalResults).doReturn(query.totalResults)
            whenever(recipes).doReturn(when(testRule.methodName) {
                // set up sample recipe data and LiveData for recycler view testing; otherwise a
                // nasty IndexOutOfBoundsException appears if the recipes are not preset
                "displaysShimmerLayoutAndFetchesRecipesOnStartup" -> SparseArray()
                "displaysErrorMessageIfErrorResourceGiven" -> SparseArray()
                else -> {
                    recipesLiveData.value = Resource.Success(query)
                    recipeArray
                }
            })
        }

        if (query.recipes == null) query.recipes = recipeList
        scenario = ActivityScenario.launch(intent)

        scenario.onActivity {
            it.recycler_view.measure(0, 0)
            it.recycler_view.layout(0, 0, 100, 1000)
        }
    }

    @Test fun displaysShimmerLayoutAndFetchesRecipesOnStartup() {
        // check back button and title are present
        onView(withId(R.id.back_button)).check(matches(not(doesNotExist())))
        onView(withId(R.id.activity_title)).check(matches(not(doesNotExist())))

        // check shimmer layout is present and running
        onView(withId(R.id.skeleton_search_results)).check(matches(not(doesNotExist())))

        // check all other views are GONE
        onView(withId(R.id.recycler_view)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.button_load_more)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.button_retry_load)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.loading_indicator)).check(matches(withEffectiveVisibility(Visibility.GONE)))

        // assert viewModel was called to fetch recipes
        scenario.onActivity { activity ->
              verify(activity.viewModel, times(1)).fetchRecipes()
        }
    }

    @Test fun displaysRecyclerViewWithRecipes() {
        scenario.onActivity { activity ->
            // check recycler view is visible
            onView(withId(R.id.recycler_view)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.skeleton_search_results)).check(matches(withEffectiveVisibility(Visibility.GONE)))

            // check recycler view has all recipes provided and it's layout is displaying some items
            assertEquals(activity.recycler_view.adapter?.itemCount, recipeArray.size())
            assert(activity.recycler_view.layoutManager?.childCount ?: 0 > 0)

            // check recycler view items have the proper data
            val viewHolder = activity.recycler_view.findViewHolderForAdapterPosition(0) as SearchResultViewHolder
            assertEquals(viewHolder.textView.text, recipeArray.get(0).name)

            // check Load More button is visible
            onView(withId(R.id.button_load_more)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
    }

    @Test fun displaysErrorMessageIfErrorResourceGiven() {
        scenario.onActivity { activity ->
            val resource = Resource.Error<RecipeSearchQuery>("000")
            activity.processResource(resource)
            onView(withId(R.id.error_message)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

            // check Retry button fetches recipes
            onView(withId(R.id.button_retry)).perform(click())
            verify(activity.viewModel, times(2)).fetchRecipes()
        }
    }

    @Test fun appendsMoreRecipesWhenLoadMoreButtonClicked() {
        scenario.onActivity { activity ->
            // check button behavior
            activity.button_load_more.performClick()
            verify(activity.viewModel, times(1)).fetchRecipes()

            // set up last few recipes to be displayed
            val remainingRecipes = ArrayList<Recipe>()

            for (i in 1..query.totalResults - recipeArray.size()) {
                remainingRecipes.add(Recipe(recipeArray.size().toLong(), "Chicken Soup", 60, "chk.jpg"))
                recipeArray.append(recipeArray.size(), remainingRecipes.last())
            }

            query.recipes = remainingRecipes
            recipesLiveData.value = Resource.Success(query)

            // check last two recipes were added and Load More button disappears
            assertEquals(activity.recycler_view.adapter?.itemCount, recipeArray.size())
            onView(withId(R.id.button_load_more)).check(matches(withEffectiveVisibility(Visibility.GONE)))

            query.recipes = null // clear recipes for next test
        }
    }

    @Test fun displaysErrorMessageOnLoadMoreButton() {
        scenario.onActivity { activity ->
            activity.processResource(Resource.Error("000"))
            onView(withId(R.id.button_load_more)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.button_retry_load)).apply {
                check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
                check(matches(withText(
                    activity.getString(R.string.NETWORK_ERROR) + ": " + activity.getString(R.string.try_again)
                )))
            }
        }
    }

    @Test fun startsRecipeActivityWhenRecipeClicked() {
        scenario.onActivity { activity ->
            activity.recycler_view.getChildAt(0).performClick()

            val intent = shadowOf(activity).nextStartedActivity
            val shadowIntent = shadowOf(intent)
            val holder = activity.recycler_view.findViewHolderForAdapterPosition(0) as SearchResultViewHolder
            val recipe: Recipe = intent.getParcelableExtra(IntentExtras.RECIPE)

            assertEquals(RecipeActivity::class.java, shadowIntent.intentClass)
            assertEquals(0L, recipe.id)
            assertEquals(holder.textView.text, recipe.name)
        }
    }

    @After fun tearDown() = stopKoin()
}