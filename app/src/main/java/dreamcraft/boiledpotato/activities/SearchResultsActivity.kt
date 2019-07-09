package dreamcraft.boiledpotato.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.adapters.SearchResultsRecyclerViewAdapter
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import kotlinx.android.synthetic.main.activity_search_results.*

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var view_model: SearchResultsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        // get view model and observe recipe list from search results
        view_model = ViewModelProviders.of(this).get(SearchResultsViewModel::class.java)

        // insert adapter and layout manager to search results recycler view if data is available
        view_model.recipeList.value?.let {
            recycler_view.adapter = SearchResultsRecyclerViewAdapter(it)
            recycler_view.layoutManager = LinearLayoutManager(this)
            // TODO: add observer to check for list updates. Do this after setting up repository
            // view_model.recipeList!!.observe(this, updateRecyclerView)
        } ?: run { // ...otherwise hide recycler view and show error message
            recycler_view.visibility = View.GONE
            result_message.visibility = View.VISIBLE
        }
    }

//    private fun updateRecyclerView(position) {
//        recycler_view.adapter.notifyItemRangeChanged()
//    }
}
