package dreamcraft.boiledpotato.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.shimmer.ShimmerFrameLayout
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.adapters.SearchResultsRecyclerViewAdapter
import dreamcraft.boiledpotato.models.JsonRecipesList
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import kotlinx.android.synthetic.main.activity_search_results.*
import kotlinx.android.synthetic.main.error_message.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class SearchResultsActivity : AppCompatActivity() {
    private val viewModel : SearchResultsViewModel by viewModel()
    private val maxResultsSize: Int by inject(named("webApiResultsSize"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        back_button.setOnClickListener { this.finish() }

        // insert adapter and layout manager to search results recycler view
        recycler_view.adapter = SearchResultsRecyclerViewAdapter(viewModel.recipes)
        recycler_view.layoutManager = LinearLayoutManager(this)
        observeRecipes()

        setClickListeners()

        viewModel.searchKeywords = intent.getStringExtra(IntentExtras.SEARCH)
        viewModel.cuisine = intent.getStringExtra(IntentExtras.CUISINE)
        viewModel.getRecipes()
    }

    /** set click listener for Load More button and Retry buttons to get paginated search results */
    private fun setClickListeners() {
        val clickListener = View.OnClickListener { viewModel.getRecipes() }
        button_load_more.setOnClickListener(clickListener)
        button_retry_load.setOnClickListener(clickListener)
        button_retry.setOnClickListener(clickListener)
    }

    /** notify recycler view when the array of recipes has been given more recipes */
    private fun observeRecipes() {
        viewModel.resourceLiveData.observe(this, Observer<Resource<JsonRecipesList>> {
            when(it) {
                is Resource.Loading -> displayLoadingIndicator()
                is Resource.Success -> displaySearchResults()
                is Resource.Error -> displayErrorMessage(it.message ?: "")
            }
        })
    }

    /** display skeleton views or loading indicator depending on if recipe list is already loaded */
    private fun displayLoadingIndicator() {
        if (viewModel.recipes.size() == 0) {
            error_message.visibility = View.GONE
            skeleton_search_results.visibility = View.VISIBLE

            if (!skeleton_search_results.isShimmerStarted) {
                skeleton_search_results.startShimmer()
            }
        } else {
            button_load_more.visibility = View.GONE
            button_retry_load.visibility = View.GONE
            loading_indicator.visibility = View.VISIBLE
        }
    }

    /** reveal search results using RecyclerView */
    private fun displaySearchResults() {
        val count = viewModel.recipes.size() % maxResultsSize
        val resultSize = if (count > 0) count else maxResultsSize

        // check if it's the first page of search results to run this code once
        if (viewModel.recipes.size() in 1..maxResultsSize) {
            (skeleton_search_results as ShimmerFrameLayout).stopShimmer()
            skeleton_search_results.visibility = View.GONE
            activity_body.setBackgroundColor(ContextCompat.getColor(this, R.color.activity_background))
            recycler_view.visibility = View.VISIBLE
        }

        // display "Load More" button if results are paginated and not all results are loaded
        if (viewModel.totalResults > viewModel.recipes.size()) {
            button_load_more.visibility = View.VISIBLE
        } else { // otherwise hide
            button_load_more.visibility = View.GONE
        }

        // remove retry button used for errors and the loading indicator
        button_retry_load.visibility = View.GONE
        loading_indicator.visibility = View.GONE
        recycler_view.adapter?.notifyItemRangeInserted(viewModel.recipes.size() - resultSize, resultSize)
    }

    /** show error message on entire activity or at the bottom of a results list */
    private fun displayErrorMessage(message: String) {
        var errorMessage = when(message) { // use error code or http error message
            "000" -> resources.getString(R.string.NETWORK_ERROR)
            "400" -> resources.getString(R.string.DATA_ERROR)
            "500" -> resources.getString(R.string.SERVER_ERROR)
            else -> message
        }; errorMessage += ": " + resources.getString(R.string.try_again)

        if (viewModel.recipes.size() == 0) {
            skeleton_search_results.stopShimmer()
            skeleton_search_results.visibility = View.GONE
            error_text.text = errorMessage
            error_message.visibility = View.VISIBLE
        } else {
            button_retry_load.text = errorMessage
            button_load_more.visibility = View.GONE
            button_retry_load.visibility = View.VISIBLE
        }
    }
}
