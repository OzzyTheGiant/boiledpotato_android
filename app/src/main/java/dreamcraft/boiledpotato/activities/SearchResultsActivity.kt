package dreamcraft.boiledpotato.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.adapters.SearchResultsRecyclerViewAdapter
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.utilities.Visibility
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import kotlinx.android.synthetic.main.activity_search_results.*
import kotlinx.android.synthetic.main.error_message.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class SearchResultsActivity : AppCompatActivity() {
    private val viewModel : SearchResultsViewModel by viewModel()
    private val maxResultsSize: Int by inject(named("webApiResultsSize"))
    private val clickListener = View.OnClickListener { viewModel.getRecipes() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        observeRecipes()
        setClickListeners()

        // insert adapter and layout manager to search results recycler view
        recycler_view.layoutManager = LinearLayoutManager(this)

        viewModel.searchKeywords = intent.getStringExtra(IntentExtras.SEARCH)
        viewModel.cuisine = intent.getStringExtra(IntentExtras.CUISINE)

        if (viewModel.searchKeywords == "favorites") activity_title.text = getString(R.string.FAVORITES)

        // if activity is restarted, current recipe list will be saved so just redisplay recipe list
        if (viewModel.recipes.size() == 0) viewModel.getRecipes()
        else displaySearchResults()
    }

    /** set click listener for Load More button and Retry buttons to get paginated search results */
    private fun setClickListeners() {
        button_load_more.setOnClickListener(clickListener)
        button_retry_load.setOnClickListener(clickListener)
        button_retry.setOnClickListener(clickListener)
        back_button.setOnClickListener { finish() }
    }

    /** notify recycler view when the array of recipes has been given more recipes */
    private fun observeRecipes() {
        viewModel.resourceLiveData.observe(this, Observer<Resource<RecipeSearchQuery>> {
            when(it) {
                is Resource.Loading -> {
                    toggleLoadingIndicators(View.VISIBLE)
                    toggleErrorMessage(View.GONE)
                    button_load_more.visibility = View.GONE
                }
                is Resource.Error -> {
                    toggleLoadingIndicators(View.GONE)
                    toggleErrorMessage(it.message ?: "")
                    button_load_more.visibility = View.GONE
                }
                is Resource.Success -> {
                    toggleLoadingIndicators(View.GONE)
                    displaySearchResults()
                }
            }
        })
    }

    /** display skeleton views or loading indicator depending on if recipe list is already loaded */
    private fun toggleLoadingIndicators(@Visibility visibility: Int) {
        if (viewModel.recipes.size() in 1..maxResultsSize) { // first http call for recipes
            skeleton_search_results.hideShimmer()
            skeleton_search_results.visibility = View.GONE
        }

        if (viewModel.recipes.size() != 0) {
            loading_indicator.visibility = visibility // button, not a shimmer layout
        }
    }

    /** reveal search results using RecyclerView */
    private fun displaySearchResults() {
        // specify # of recipes the last http call had; Since Resource is Success, data is guaranteed
        val resource = viewModel.resourceLiveData.value as Resource.Success
        val resultsSize = resource.data!!.recipes?.size ?: 0

        if (recycler_view.adapter == null) {
            recycler_view.adapter = SearchResultsRecyclerViewAdapter(viewModel.recipes)
        }

        body.setBackgroundColor(ContextCompat.getColor(this, R.color.activity_background))
        recycler_view.visibility = View.VISIBLE

        // display "Load More" button if results are paginated and not all results are loaded
        button_load_more.visibility =
            if (viewModel.totalResults > viewModel.recipes.size()) View.VISIBLE else View.GONE

        recycler_view.adapter?.notifyItemRangeInserted(viewModel.recipes.size() - resultsSize, resultsSize)
    }

    /** show error message on entire activity or at the bottom of a results list */
    private fun toggleErrorMessage(message: String) {
        var errorMessage = when(message) { // use error code or http error message
            "000" -> getString(R.string.NETWORK_ERROR)
            "400" -> getString(R.string.DATA_ERROR)
            "500" -> getString(R.string.SERVER_ERROR)
            "404" -> getString(R.string.NOT_FOUND_ERROR)
            else -> message
        }; errorMessage += ": " + getString(R.string.try_again)

        if (viewModel.recipes.size() == 0) {
            error_text.text = errorMessage
        } else {
            button_retry_load.text = errorMessage
        }

        toggleErrorMessage(View.VISIBLE)
    }

    private fun toggleErrorMessage(@Visibility visibility: Int) {
        if (viewModel.recipes.size() == 0) {
            error_message.visibility = visibility
        } else {
            button_retry_load.visibility = visibility
        }
    }
}
