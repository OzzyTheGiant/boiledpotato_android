package dreamcraft.boiledpotato.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.activities.UnitTestCustomMatchers.Companion.withTextColor
import kotlinx.android.synthetic.main.toast_error.view.*
import org.hamcrest.Matchers.*
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowToast

@RunWith(AndroidJUnit4::class)
class MainActivityUnitTest {
    private lateinit var scenario: ActivityScenario<MainActivity>

    @Before fun setUp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test fun displaysUICorrectlyOnStartUp() {
        // check back button and title are present
        onView(withId(R.id.back_button)).check(matches(not(doesNotExist())))
        onView(withId(R.id.back_button)).check(matches(not(doesNotExist())))

        // check search component is present
        onView(withId(R.id.favorites_button)).check(matches(not(doesNotExist())))
        onView(withId(R.id.search_field)).check(matches(not(doesNotExist())))
        onView(withId(R.id.search_button)).check(matches(not(doesNotExist())))

        // check all buttons are present
        onView(withId(R.id.cuisine_button_american)).check(matches(not(doesNotExist())))
        onView(withId(R.id.cuisine_button_mexican)).check(matches(not(doesNotExist())))
        onView(withId(R.id.cuisine_button_chinese)).check(matches(not(doesNotExist())))
        onView(withId(R.id.cuisine_button_japanese)).check(matches(not(doesNotExist())))
        onView(withId(R.id.cuisine_button_indian)).check(matches(not(doesNotExist())))
        onView(withId(R.id.cuisine_button_french)).check(matches(not(doesNotExist())))
        onView(withId(R.id.cuisine_button_italian)).check(matches(not(doesNotExist())))
        onView(withId(R.id.cuisine_button_spanish)).check(matches(not(doesNotExist())))
        onView(withId(R.id.cuisine_button_british)).check(matches(not(doesNotExist())))
    }

    @Test fun changesCuisineButtonColorsOnToggle() {
        onView(withId(R.id.cuisine_button_american)).perform(click())
        onView(withId(R.id.cuisine_button_american))
            .check(matches(withTextColor(R.color.neutral)))

        // check that pressing another cuisine button deselects the previous one
        onView(withId(R.id.cuisine_button_mexican)).perform(click())
        onView(withId(R.id.cuisine_button_american))
            .check(matches(withTextColor(R.color.primary)))
        onView(withId(R.id.cuisine_button_mexican))
            .check(matches(withTextColor(R.color.neutral)))

        // check that color returns to default if deselected
        onView(withId(R.id.cuisine_button_mexican)).perform(click())
        onView(withId(R.id.cuisine_button_mexican))
            .check(matches(withTextColor(R.color.primary)))
    }

    @Test fun displaysToastErrorMessageIfSearchFieldIsEmpty() {
        onView(withId(R.id.search_button)).perform(click())
        assertThat(
            ShadowToast.getLatestToast().view.toast_error_message.text.toString(),
            equalTo("Please enter an ingredient")
        )
    }

    @Test fun stopsWhenBackButtonIsPressed() {
        onView(withId(R.id.back_button)).perform(click())
        scenario.onActivity { assert(it.isFinishing) }
    }

    @Test fun startsSearchResultsActivityWhenFavoritesButtonIsClicked() {
        onView(withId(R.id.favorites_button)).perform(click())
        scenario.onActivity { activity ->
            val intent = shadowOf(activity).nextStartedActivity
            val shadowIntent = shadowOf(intent)

            assertEquals(SearchResultsActivity::class.java, shadowIntent.intentClass)
            assertEquals("favorites", intent.getStringExtra(IntentExtras.SEARCH))
        }
    }

    @Test fun startsSearchResultsActivityWhenSearchButtonPressed() {
        onView(withId(R.id.search_field)).perform(typeText("chicken"))
        onView(withId(R.id.search_button)).perform(click())

        scenario.onActivity { activity ->
            val intent = shadowOf(activity).nextStartedActivity
            val shadowIntent = shadowOf(intent)

            assertEquals(SearchResultsActivity::class.java, shadowIntent.intentClass)
            assertEquals("chicken", intent.getStringExtra(IntentExtras.SEARCH))
        }
    }

    @After fun tearDown() = stopKoin()
}