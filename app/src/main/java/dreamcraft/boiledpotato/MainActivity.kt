package dreamcraft.boiledpotato

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Intent
import android.graphics.PorterDuff
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toast_error.view.*

object GLOBALS {
    const val EXTRAS_SEARCH = "SEARCH_QUERY"
    const val EXTRAS_CUISINE = "CUISINE"
}

class MainActivity : AppCompatActivity() {
    private val searchButtonClickListener = fun(_: View) = startSearchResultsActivity(search_field.text.toString())
    private val cuisineButtonClickListener = fun (view: View) = checkCuisineOption(view)
    private val backButtonClickListener = fun(_: View) = finish()

    private var cuisineButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set click listeners on activity's buttons
        back_button.setOnClickListener(backButtonClickListener)
        search_button.setOnClickListener(searchButtonClickListener)

        var button: Button // add click listener to every cuisine button
        for (i in 0 until button_grid.childCount) {
            button = button_grid.getChildAt(i) as Button
            button.setOnClickListener(cuisineButtonClickListener)
        }
    }

    /** set the cuisine to be used as filter in search query */
    private fun checkCuisineOption(view: View) {
        val button = view as Button

        if (cuisineButton != null) { // reset previous checked button if a button was previously clicked
            changeCuisineButtonColors(cuisineButton!!, R.color.primary_dark, R.color.neutral)
        }

        if (button == cuisineButton) {
            cuisineButton = null; return
        } else { // set current cuisine button to gold with black icon and text
            changeCuisineButtonColors(button, R.color.neutral, R.color.primary_dark)
        }
        cuisineButton = button
    }

    /** change colors of specified cuisine button to show whether it has been marked as "checked" */
    private fun changeCuisineButtonColors(button: Button, foregroundColor: Int, backgroundColor: Int) {
        button.background.setColorFilter(
            ContextCompat.getColor(applicationContext, backgroundColor),
            PorterDuff.Mode.SRC_ATOP
        )
        button.compoundDrawables[1].setColorFilter(
            ContextCompat.getColor(applicationContext, foregroundColor),
            PorterDuff.Mode.SRC_ATOP
        )
        button.setTextColor(ContextCompat.getColor(applicationContext, foregroundColor))
    }

    /** show toast with provided notification message */
    @SuppressLint("InflateParams")
    private fun displayToast(message: String = "Please enter an ingredient") {
        val toast = Toast(applicationContext)
        toast.view = layoutInflater.inflate(R.layout.toast_error, null) // custom toast layout
        toast.view.toast_error_message.text = message
        toast.show()
    }

    /** Initiate search results activity by passing search keyword and cuisine to fetch recipes */
    private fun startSearchResultsActivity (queryText: String = "") {
        val cuisine = cuisineButton?.text.toString()
        // show error message if no text in search field when clicking search button
        if (queryText == "") { displayToast(); return }

        // create intent, add query and/or cuisine, and start search results activity
        val intent = Intent(this, SearchResultsActivity::class.java)
        intent.putExtra(GLOBALS.EXTRAS_SEARCH, queryText)
        intent.putExtra(GLOBALS.EXTRAS_CUISINE, cuisine)
        startActivity(intent)
    }
}
