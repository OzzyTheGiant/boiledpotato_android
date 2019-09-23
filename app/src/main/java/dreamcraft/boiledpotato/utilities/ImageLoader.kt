package dreamcraft.boiledpotato.utilities

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.squareup.picasso.Picasso
import dreamcraft.boiledpotato.BuildConfig
import dreamcraft.boiledpotato.R

/** this class is just a data holding class to retrieve settings for images loaded by Picasso */
class ImageLoader(
    imageFileName: String,
    private val view: ImageView
) {
    private val url = view.context.getString(R.string.WEB_API_IMAGE_URL) + imageFileName
    private val placeholder: Drawable = view.context.resources.getDrawable(R.drawable.ic_sync_gray_32dp, null)
    private val errorIcon: Drawable = view.context.resources.getDrawable(R.drawable.ic_error_red_32dp, null)
    private var picasso : Picasso = Picasso.get()

    init {
        if (BuildConfig.DEBUG) picasso.setIndicatorsEnabled(true) // for development purposes
    }

    fun loadImageToView() {
        picasso
            .load(url)
            .placeholder(placeholder)
            .error(errorIcon)
            .fit()
            .centerCrop()
            .into(view)
    }
}