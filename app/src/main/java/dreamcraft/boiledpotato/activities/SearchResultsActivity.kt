package dreamcraft.boiledpotato.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.adapters.SearchResultsRecyclerViewAdapter
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.utilities.RecyclerViewColumnSpacing
import dreamcraft.boiledpotato.utilities.Visibility
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import kotlinx.android.synthetic.main.activity_search_results.*
import kotlinx.android.synthetic.main.error_message.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class SearchResultsActivity : AppCompatActivity() {
    // dependencies
    public val viewModel : SearchResultsViewModel by viewModel()
    private val maxResultsSize: Int by inject(named("webApiResultsSize"))

    // callbacks
    private val clickListener = View.OnClickListener { viewModel.fetchRecipes() }
    private val liveDataObserver = Observer<Resource<RecipeSearchQuery>> { processResource(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        // observe recipe LiveData
        viewModel.resourceLiveData.observe(this, liveDataObserver)

        setClickListeners()
        setupRecyclerView()

        viewModel.searchKeywords = intent.getStringExtra(IntentExtras.SEARCH)
        viewModel.cuisine = intent.getStringExtra(IntentExtras.CUISINE)

        if (viewModel.searchKeywords == "favorites") activity_title.text = getString(R.string.FAVORITES)

        // if activity is restarted, current recipe list will be saved so just redisplay recipe list
        if (viewModel.recipes.size() == 0) viewModel.fetchRecipes()
        else processResource(viewModel.resourceLiveData.value!!)
    }

    /** insert adapter and layout manager to search results recycler view */
    private fun setupRecyclerView() {
        val viewportWidth = (resources.displayMetrics.widthPixels / resources.displayMetrics.density).toInt()
        val gapSize = resources.getDimension(R.dimen.padding_main).toInt()
        val colCount : Int // NOTE: column layouts have not been tested on tablets

        if (viewportWidth >= 600) { // screen size at least 600dp
            colCount = if (viewportWidth >= 960) 3 else 2
            recycler_view.layoutManager = StaggeredGridLayoutManager(colCount, StaggeredGridLayoutManager.VERTICAL)
            recycler_view.addItemDecoration(RecyclerViewColumnSpacing(gapSize)) // add column spacing
        } else {
            recycler_view.layoutManager = LinearLayoutManager(this)
        }
    }

    /** set click listener for Load More button and Retry buttons to get paginated search results */
    private fun setClickListeners() {
        button_load_more.setOnClickListener(clickListener)
        button_retry_load.setOnClickListener(clickListener)
        button_retry.setOnClickListener(clickListener)
        back_button.setOnClickListener { finish() }
    }

    /** notify recycler view when the array of recipes has been given more recipes */
    fun processResource(resource: Resource<RecipeSearchQuery>) {
        when(resource) {
            is Resource.Loading -> {
                toggleLoadingIndicators(View.VISIBLE)
                toggleErrorMessage(View.GONE)
                button_load_more.visibility = View.GONE
            }
            is Resource.Error -> {
                toggleLoadingIndicators(View.GONE)
                toggleErrorMessage(resource.message ?: "")
                button_load_more.visibility = View.GONE
            }
            is Resource.Success -> {
                toggleLoadingIndicators(View.GONE)
                displaySearchResults(resource)
            }
        }
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
    private fun displaySearchResults(resource: Resource<RecipeSearchQuery>) {
        // specify # of recipes the last http call had; Since Resource is Success, data is guaranteed
        val resultsSize = resource.data?.recipes?.size ?: 0

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val isFavorite = data?.getBooleanExtra(IntentExtras.IS_FAVORITE, false)
            val adapter : SearchResultsRecyclerViewAdapter

            // check if SearchResultsActivity is displaying favorite recipes and if recipe no longer favorite
            // so that it can be removed from the list upon resuming activity
            if (isFavorite != null && !isFavorite && viewModel.searchKeywords == "favorites") {
                adapter = (recycler_view.adapter as SearchResultsRecyclerViewAdapter)
                viewModel.recipes.remove(adapter.currentRecipeIndex)
                adapter.notifyItemRemoved(adapter.currentRecipeIndex)
            }
        }
    }
}
