package dreamcraft.boiledpotato

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toast_error.view.*

object GLOBALS {
    const val EXTRAS_SEARCH = "SEARCH_QUERY"
    const val EXTRAS_CUISINE = "CUISINE"
    const val TOAST_MESSAGE = "Please enter an ingredient"
}

class MainActivity : AppCompatActivity() {
    // custom toast view layout
    private var toastLayout: View? = null

    private val searchButtonClickListener = fun(_: View) = startSearchResultsActivity(search_field.text.toString())
    private val cuisineButtonClickListener = fun(view: View) = startSearchResultsActivity((view as Button).text.toString())

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toastLayout = layoutInflater.inflate(R.layout.toast_error, null) // init custom toast layout
        search_button.setOnClickListener(searchButtonClickListener)
        for (i in 0 until button_grid.childCount - 1) {
            // add click listener to every cuisine button
            (button_grid.getChildAt(i) as Button).setOnClickListener(cuisineButtonClickListener)
        }
    }

    /** Initiate search results activity by passing search keyword to be used in Spoonacular API */
    private fun startSearchResultsActivity (text: String) {
        if (text == "") { // show error message if no text in search field when clicking search button
            val toast = Toast(applicationContext)
            toast.view = toastLayout
            toast.view.toast_error_message.text = GLOBALS.TOAST_MESSAGE
            toast.show(); return
        }

        val intent = Intent(this, SearchResultsActivity::class.java)
            .apply { putExtra(GLOBALS.EXTRAS_SEARCH, text) }
        startActivity(intent)
    }
}
