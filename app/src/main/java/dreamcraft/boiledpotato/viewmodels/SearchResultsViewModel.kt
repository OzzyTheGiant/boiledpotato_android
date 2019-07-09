package dreamcraft.boiledpotato.viewmodels

import android.util.SparseArray
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dreamcraft.boiledpotato.models.Recipe

class SearchResultsViewModel : ViewModel() {
    var recipeList = MutableLiveData<SparseArray<Recipe>>()

    init {
        val recipe = Recipe(1, "Chicken Soup", 30, 4, "soup.jpg")
        val recipes = SparseArray<Recipe>()
        for (i in 0..20) {
            recipes.append(i, recipe)
        }
        recipeList.value = recipes
    }
}