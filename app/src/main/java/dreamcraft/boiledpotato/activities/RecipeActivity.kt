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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.models.JsonRecipeDetails
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.utilities.NumberListSpan
import dreamcraft.boiledpotato.viewmodels.RecipeViewModel
import kotlinx.android.synthetic.main.activity_recipe.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RecipeActivity : AppCompatActivity() {
    private val viewModel : RecipeViewModel by viewModel()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        viewModel.recipe = intent.getParcelableExtra(IntentExtras.RECIPE)
        recipe_name.text = viewModel.recipe.name
        servings.text = getString(R.string.recipe_label_servings, "0")
        prep_time.text = getString(R.string.min, viewModel.recipe.prepMinutes.toString())

        observeRecipeDetails()
        viewModel.getRecipeDetails()
        // TODO: add image data
    }

    /** create bullet lists for ingredients and recipe instructions when data is fetched from repository */
    private fun observeRecipeDetails() {
        viewModel.resourceLiveData.observe(this, Observer<Resource<JsonRecipeDetails>> {
            when(it) {
                is Resource.Success -> {
                    ingredients_placeholder.stopShimmer()
                    instructions_placeholder.stopShimmer()
                    ingredients_placeholder.visibility = View.GONE
                    instructions_placeholder.visibility = View.GONE

                    servings.text = getString(R.string.recipe_label_servings, viewModel.recipe.servings.toString())
                    ingredients_list.text = createBulletList(viewModel.recipe.ingredients)
                    instructions_list.text = createBulletList(viewModel.recipe.instructions, true)
                }
                is Resource.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun createBulletList(stringArray: SparseArray<String>, isNumbered: Boolean = false) : CharSequence {
        var textList = SpannedString("") // will hold all the list items in one string of text

        // return a Not Found error message if array contains no list items
        if (stringArray.size() == 0) return getString(R.string.NOT_FOUND_ERROR)

        for (i in 0 until stringArray.size()) {
            // create bullet span and then append to list item
            val span = if (isNumbered) NumberListSpan(16, 72, i + 1) else BulletSpan(20) // 20dp gap
            val listItem = SpannableString(stringArray[i] + "\n")

            listItem.setSpan(span, 0, stringArray[i].length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            textList = TextUtils.concat(textList, listItem) as SpannedString // merge list items
        }

        return textList
    }
}
