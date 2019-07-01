package dreamcraft.boiledpotato

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toast_error.view.*

object GLOBALS {
    const val EXTRAS_SEARCH = "SEARCH_RESULT"
    const val TOAST_MESSAGE = "Please enter an ingredient"
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_button.setOnClickListener { startSearchResultsActivity(search_field.text) }
        cuisine_button_american.setOnClickListener { startSearchResultsActivity(cuisine_button_american.text) }
        cuisine_button_mexican.setOnClickListener { startSearchResultsActivity(cuisine_button_mexican.text) }
        cuisine_button_chinese.setOnClickListener { startSearchResultsActivity(cuisine_button_chinese.text) }
        cuisine_button_japanese.setOnClickListener { startSearchResultsActivity(cuisine_button_japanese.text) }
        cuisine_button_indian.setOnClickListener { startSearchResultsActivity(cuisine_button_indian.text) }
        cuisine_button_french.setOnClickListener { startSearchResultsActivity(cuisine_button_french.text) }
        cuisine_button_italian.setOnClickListener { startSearchResultsActivity(cuisine_button_italian.text) }
        cuisine_button_spanish.setOnClickListener { startSearchResultsActivity(cuisine_button_spanish.text) }
        cuisine_button_british.setOnClickListener { startSearchResultsActivity(cuisine_button_british.text) }
    }

    /** Initiate search results activity by passing search keyword to be used in Spoonacular API */
    @SuppressLint("InflateParams")
    private fun startSearchResultsActivity(text: CharSequence) {
        if (text.toString() == "") {
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
