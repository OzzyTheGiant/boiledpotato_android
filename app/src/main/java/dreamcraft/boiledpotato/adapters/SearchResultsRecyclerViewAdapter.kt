package dreamcraft.boiledpotato.adapters

import android.content.Intent
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.activities.IntentExtras
import dreamcraft.boiledpotato.activities.RecipeActivity
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.utilities.ImageLoader
import dreamcraft.boiledpotato.viewholders.SearchResultViewHolder

class SearchResultsRecyclerViewAdapter(
    private val recipeArray: SparseArray<Recipe>
) : RecyclerView.Adapter<SearchResultViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_result_list_item, parent, false)
        val viewHolder = SearchResultViewHolder(view)
        view.setOnClickListener { startRecipeActivity(view, viewHolder) }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return recipeArray.size()
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        val imageLoader = ImageLoader(recipeArray[position].imageFileName, holder.imageView)
        imageLoader.loadImageToView()
        holder.textView.text = recipeArray[position].name
    }

    private fun startRecipeActivity(view: View, holder: SearchResultViewHolder) {
        val intent = Intent(view.context, RecipeActivity::class.java)
        intent.putExtra(IntentExtras.RECIPE, recipeArray[holder.adapterPosition])
        view.context.startActivity(intent)
    }
}