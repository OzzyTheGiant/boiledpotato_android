package dreamcraft.boiledpotato.utilities

import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import android.view.View

class RecyclerViewColumnSpacing(private val space: Int = 4) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.left = space
        outRect.right = space
    }
}