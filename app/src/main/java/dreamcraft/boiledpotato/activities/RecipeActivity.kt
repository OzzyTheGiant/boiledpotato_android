package dreamcraft.boiledpotato.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.SpannedString
import android.text.TextUtils
import android.text.style.BulletSpan
import android.util.SparseArray
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.utilities.ImageLoader
import dreamcraft.boiledpotato.utilities.NumberListSpan
import dreamcraft.boiledpotato.viewmodels.RecipeViewModel
import kotlinx.android.synthetic.main.activity_recipe.*
import kotlinx.android.synthetic.main.error_message.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecipeActivity : AppCompatActivity() {
    private val viewModel : RecipeViewModel by viewModel()
    private lateinit var imageLoader : ImageLoader

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        // bind data
        viewModel.recipe = intent.getParcelableExtra(IntentExtras.RECIPE)
        recipe_name.text = viewModel.recipe.name
        servings.text = getString(R.string.recipe_label_servings, "0")
        prep_time.text = getString(R.string.min, viewModel.recipe.prepMinutes.toString())

        // fetch image from internet/cache
        imageLoader = ImageLoader(viewModel.recipe.imageFileName, recipe_image)
        imageLoader.loadImageToView()

        // set click listeners
        button_retry.setOnClickListener { viewModel.getRecipeDetails() }

        // adjust UI for error messages
        resizeErrorMessage()
        observeRecipeDetails()

        if (viewModel.recipe.ingredients == null || viewModel.recipe.instructions == null) {
            viewModel.getRecipeDetails() // fill in recipe details if missing
        } else {
            displayRecipeDetails()
        }
    }

    /** when body is fully drawn, change height of error_message to fit where labels and lists would be */
    private fun resizeErrorMessage() {
        body.post {
            val height =
                (ingredients_heading.measuredHeight +
                ingredients_list.measuredHeight) * 2 +
                servings.measuredHeight

            error_message.layoutParams.height = height
        }
    }

    /** create bullet lists for ingredients and recipe instructions when data is fetched from repository */
    private fun observeRecipeDetails() {
        viewModel.resourceLiveData.observe(this, Observer<Resource<Recipe>> {
            when (it) {
                is Resource.Loading -> displayLoadingIndicators()
                is Resource.Success -> displayRecipeDetails()
                is Resource.Error -> displayErrorMessage(it.message ?: "")
            }
        })
    }

    /** remove error message from view and display shimmering placeholders */
    private fun displayLoadingIndicators() {
        error_message.visibility = View.GONE
        togglePlaceholders(View.VISIBLE)
    }

    /** hide loading indicators or error message and display recipe details from json Resource */
    private fun displayRecipeDetails() {
        togglePlaceholders(View.GONE)
        servings.text = getString(R.string.recipe_label_servings, viewModel.recipe.servings.toString())
        ingredients_list.text = viewModel.recipe.ingredients?.let { createBulletList(it) }
        instructions_list.text = viewModel.recipe.instructions?.let { createBulletList(it, true) }
    }

    /** create an ordered list with bullets or numbers, merged into one string with line breaks */
    private fun createBulletList(stringArray: List<String>, isNumbered: Boolean = false) : CharSequence {
        var textList = SpannedString("") // will hold all the list items in one string of text

        // return a Not Found error message if array contains no list items
        if (stringArray.isEmpty()) return getString(R.string.NOT_FOUND_ERROR)

        for (i in stringArray.indices) {
            // create bullet span and then append to list item
            val span = if (isNumbered) NumberListSpan(16, 72, i + 1) else BulletSpan(20) // 20dp gap
            val listItem = SpannableString(stringArray[i] + "\n")

            listItem.setSpan(span, 0, stringArray[i].length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            textList = TextUtils.concat(textList, listItem) as SpannedString // merge list items
        }

        return textList
    }

    /** show error message if exception thrown during http call or json data handling */
    private fun displayErrorMessage(message: String) {
        var errorMessage = when(message) { // use error code or http error message
            "000" -> getString(R.string.NETWORK_ERROR)
            "400" -> getString(R.string.DATA_ERROR)
            "500" -> getString(R.string.SERVER_ERROR)
            else -> message
        }; errorMessage += ": " +  getString(R.string.try_again)

        error_message.visibility = View.VISIBLE
        error_text.text = errorMessage
        togglePlaceholders(View.GONE)
    }

    private fun togglePlaceholders(visibility: Int) {
        when(visibility) {
            View.VISIBLE -> {
                ingredients_placeholder.showShimmer(true)
                instructions_placeholder.showShimmer(true)
            }
            View.GONE -> {
                ingredients_placeholder.hideShimmer()
                instructions_placeholder.hideShimmer()
            }
        }

        ingredients_placeholder.visibility = visibility
        instructions_placeholder.visibility = visibility
    }
}
