package dreamcraft.boiledpotato.adapters

import android.content.Intent
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dreamcraft.boiledpotato.BuildConfig
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.activities.IntentExtras
import dreamcraft.boiledpotato.activities.RecipeActivity
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.viewholders.SearchResultViewHolder

class SearchResultsRecyclerViewAdapter(
    private val recipeArray: SparseArray<Recipe>,
    private val picasso : Picasso = Picasso.get()
) : RecyclerView.Adapter<SearchResultViewHolder>() {

    init {
        if (BuildConfig.DEBUG) picasso.setIndicatorsEnabled(true) // for development purposes
    }

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
        val url = holder.textView.context.getString(R.string.WEB_API_IMAGE_URL) + recipeArray[position].imageFileName
        val width = holder.textView.context.resources.displayMetrics.widthPixels
        val height = holder.textView.context.resources.getDimension(R.dimen.skeleton_search_result_height)
        val placeholder = holder.textView.context.resources.getDrawable(R.drawable.ic_sync_gray_32dp, null)
        val errorIcon = holder.textView.context.resources.getDrawable(R.drawable.ic_error_red_32dp, null)

        picasso
            .load(url)
            .placeholder(placeholder)
            .error(errorIcon)
            .resize(width, height.toInt())
            .centerCrop().into(holder.imageView)

        holder.textView.text = recipeArray[position].name
    }

    private fun startRecipeActivity(view: View, holder: SearchResultViewHolder) {
        val intent = Intent(view.context, RecipeActivity::class.java)
        intent.putExtra(IntentExtras.RECIPE, recipeArray[holder.adapterPosition])
        view.context.startActivity(intent)
    }
}