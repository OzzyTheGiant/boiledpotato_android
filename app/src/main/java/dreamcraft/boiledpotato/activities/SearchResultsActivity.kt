package dreamcraft.boiledpotato.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.SparseArray
import androidx.recyclerview.widget.LinearLayoutManager
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.adapters.SearchResultsRecyclerViewAdapter
import dreamcraft.boiledpotato.models.Recipe
import kotlinx.android.synthetic.main.activity_search_results.*

class SearchResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        val recyclerView = search_results_component
        recyclerView.adapter = SearchResultsRecyclerViewAdapter(createSampleData())
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    fun createSampleData(): SparseArray<Recipe> {
        val recipe = Recipe(1, "Chicken Soup", 30, 4, "soup.jpg")
        val recipeList = SparseArray<Recipe>()
        for (i in 0..20) {
            recipeList.append(i, recipe)
        }
        return recipeList
    }
}
