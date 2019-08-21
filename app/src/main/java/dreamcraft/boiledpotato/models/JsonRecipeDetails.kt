package dreamcraft.boiledpotato.models

import android.util.SparseArray

/** this class is to model data that was returned from a json object in retrofit
 * which is then sent to the ViewModel to update a Recipe object */
data class JsonRecipeDetails(
    val ingredients: SparseArray<String>,
    val instructions: SparseArray<String>,
    val servings: Int
)