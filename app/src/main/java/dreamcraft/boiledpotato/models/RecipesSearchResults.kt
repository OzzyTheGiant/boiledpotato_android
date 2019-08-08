package dreamcraft.boiledpotato.models

import android.util.SparseArray

data class RecipesSearchResults(
    val recipes: SparseArray<Recipe> = SparseArray(),
    val totalResults: Int = 0,
    val expires: Long = 0
) {}