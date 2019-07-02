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
    private val searchButtonClickListener = fun(_: View) = startSearchResultsActivity(search_field.text.toString())
    private val cuisineButtonClickListener = fun(view: View) = startSearchResultsActivity((view as Button).text.toString())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_button.setOnClickListener(searchButtonClickListener)
        cuisine_button_american.setOnClickListener(cuisineButtonClickListener)
        cuisine_button_mexican.setOnClickListener(cuisineButtonClickListener)
        cuisine_button_chinese.setOnClickListener(cuisineButtonClickListener)
        cuisine_button_japanese.setOnClickListener(cuisineButtonClickListener)
        cuisine_button_indian.setOnClickListener(cuisineButtonClickListener)
        cuisine_button_french.setOnClickListener(cuisineButtonClickListener)
        cuisine_button_italian.setOnClickListener(cuisineButtonClickListener)
        cuisine_button_spanish.setOnClickListener(cuisineButtonClickListener)
        cuisine_button_british.setOnClickListener(cuisineButtonClickListener)
    }

    /** Initiate search results activity by passing search keyword to be used in Spoonacular API */
    @SuppressLint("InflateParams")
    private fun startSearchResultsActivity (text: String) {
        if (text == "") {
            val toast = Toast(applicationContext)
            toast.view = layoutInflater.inflate(R.layout.toast_error, null)
            toast.view.toast_error_message.text = GLOBALS.TOAST_MESSAGE
            toast.show(); return
        }

        val intent = Intent(this, SearchResultsActivity::class.java)
            .apply { putExtra(GLOBALS.EXTRAS_SEARCH, text) }
        startActivity(intent)
    }
}
