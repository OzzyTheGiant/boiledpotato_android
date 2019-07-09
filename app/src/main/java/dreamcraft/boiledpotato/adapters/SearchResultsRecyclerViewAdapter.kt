package dreamcraft.boiledpotato.adapters

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.viewholders.SearchResultViewHolder

class SearchResultsRecyclerViewAdapter(
    private val recipeList: SparseArray<Recipe>
) : RecyclerView.Adapter<SearchResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result_list_item, parent, false)
        return SearchResultViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recipeList.size()
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.textView?.text = recipeList[position].name
    }

    // Insert a new item to the RecyclerView on a predefined position
    fun insert(position: Int, recipe: Recipe) {
        recipeList.append(position, recipe)
        notifyItemRangeInserted(position, 10)
    }
}