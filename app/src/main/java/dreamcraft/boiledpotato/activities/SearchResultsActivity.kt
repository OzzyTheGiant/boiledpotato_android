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
import dreamcraft.boiledpotato.repositories.Resource
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import kotlinx.android.synthetic.main.activity_search_results.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named

class SearchResultsActivity : AppCompatActivity() {
    private val viewModel : SearchResultsViewModel by viewModel()
    private val maxResultsSize: Int by inject(named("webApiResultsSize"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        // insert adapter and layout manager to search results recycler view
        recycler_view.adapter = SearchResultsRecyclerViewAdapter(viewModel.recipes)
        recycler_view.layoutManager = LinearLayoutManager(this)
        observeRecipes()

        viewModel.getRecipes(
            intent.getStringExtra(IntentExtras.SEARCH),
            intent.getStringExtra(IntentExtras.CUISINE)
        )
    }

    /** notify recycler view that the array of recipes has been given more recipes */
    private fun observeRecipes() {
        viewModel.resourceLiveData.observe(this, Observer<Resource<SparseArray<Recipe>>> {
            when(it) {
                is Resource.Loading -> {
                    result_message.text = getString(R.string.status_loading)
                }
                is Resource.Success -> {
                    val count = viewModel.recipes.size() % maxResultsSize
                    val resultSize = if (count > 0) count else maxResultsSize
                    if (viewModel.recipes.size() in 1..maxResultsSize) {
                        recycler_view.visibility = View.VISIBLE
                        result_message.visibility = View.GONE
                    }
                    recycler_view.adapter?.notifyItemRangeInserted(viewModel.recipes.size() - resultSize, resultSize)
                }
                is Resource.Error -> {
                    if (viewModel.recipes.size() == 0) {
                        result_message.text = it.message
                    } else {
                        // TODO: display smaller error message if list already set
                    }
                }
            }
        })
    }
}
