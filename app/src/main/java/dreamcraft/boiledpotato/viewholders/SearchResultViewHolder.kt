package dreamcraft.boiledpotato.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_result_list_item.view.*

class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.recipe_image as ImageView
    val textView: TextView = itemView.recipe_name as TextView
}