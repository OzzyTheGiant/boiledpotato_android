package dreamcraft.boiledpotato.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.search_result_list_item.view.*

class SearchResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView: ImageView? = null
    var textView: TextView? = null

    init {
        this.imageView = itemView.recipe_image as ImageView
        this.textView = itemView.recipe_name as TextView
    }
}