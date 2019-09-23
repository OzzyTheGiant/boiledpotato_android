package dreamcraft.boiledpotato.activities

import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import android.widget.TextView
import android.os.Build

class UnitTestCustomMatchers {
    companion object {
        fun withTextColor(colorId: Int): Matcher<View> {
            return object: TypeSafeMatcher<View>()
            {
                override fun describeTo(description: Description) {
                    description.appendText("has text color $colorId")
                }

                override fun matchesSafely(view: View): Boolean {
                    val expectedColor: Int = if (Build.VERSION.SDK_INT <= 22) {
                        view.context.resources.getColor(colorId, null)
                    } else {
                        view.context.getColor(colorId)
                    }

                    return (view as TextView).currentTextColor == expectedColor
                }

            }
        }
    }
}