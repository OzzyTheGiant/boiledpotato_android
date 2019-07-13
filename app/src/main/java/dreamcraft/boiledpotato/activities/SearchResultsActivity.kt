package dreamcraft.boiledpotato.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.adapters.SearchResultsRecyclerViewAdapter
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import kotlinx.android.synthetic.main.activity_search_results.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class SearchResultsActivity : AppCompatActivity() {
    private val viewModel : SearchResultsViewModel by viewModel()
    private val resultsSize: Int by inject(named("webApiResultsSize"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        // insert adapter and layout manager to search results recycler view if data is available
        viewModel.recipesContainer.value?.let {
            recycler_view.adapter = SearchResultsRecyclerViewAdapter(it)
            recycler_view.layoutManager = LinearLayoutManager(this)
            observeRecipes()
        } ?: run { // ...otherwise hide recycler view and show error message
            recycler_view.visibility = View.GONE
            result_message.visibility = View.VISIBLE
        }

        viewModel.getRecipes(
            intent.getStringExtra(IntentExtras.SEARCH),
            intent.getStringExtra(IntentExtras.CUISINE)
        )
    }

    /** notify recycler view that the array of recipes has been given more recipes */
    private fun observeRecipes() {
        viewModel.recipesContainer.observe(this, Observer<SparseArray<Recipe>> {
            if (it.size() > 0) {
                recycler_view.adapter?.notifyItemRangeInserted(it.size() - resultsSize, resultsSize)
                recycler_view.visibility = View.VISIBLE
                result_message.visibility = View.GONE
            }
        })
    }
}
