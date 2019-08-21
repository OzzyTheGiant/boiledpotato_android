package dreamcraft.boiledpotato.models

import android.util.SparseArray

/** this class is to model a json object returned from Retrofit */
data class JsonRecipesList(
    val recipes: SparseArray<Recipe> = SparseArray(),
    val totalResults: Int = 0,
    val expires: Long = 0
)