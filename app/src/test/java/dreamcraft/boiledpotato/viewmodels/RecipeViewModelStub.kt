package dreamcraft.boiledpotato.viewmodels

class RecipeViewModelStub : RecipeViewModel() {
    var getRecipeDetailsCalls = 0
    var checkIfRecipeIsFavoriteCalls = 0
    var toggleRecipeAsFavoriteCalls = 0

    override fun getRecipeDetails() {
        getRecipeDetailsCalls++
        recipe.ingredients = ArrayList()
        (recipe.ingredients as ArrayList<String>).add("2 cups of Chicken Broth")
        (recipe.ingredients as ArrayList<String>).add("1/2 lb of Chicken Breast")
        (recipe.ingredients as ArrayList<String>).add("1 pack of noodles")
        recipe.instructions = ArrayList()
        (recipe.instructions as ArrayList<String>).add("Grill chicken breast in pan")
        (recipe.instructions as ArrayList<String>).add("Boil noodles in chicken broth")
        (recipe.instructions as ArrayList<String>).add("Add chicken to soup")
        (recipe.instructions as ArrayList<String>).add("Serve")
    }

    override fun checkIfRecipeIsFavorite() {
        checkIfRecipeIsFavoriteCalls++
        recipe.isFavorite = true
    }

    override fun toggleRecipeAsFavorite() {
        toggleRecipeAsFavoriteCalls++
        recipe.isFavorite = !recipe.isFavorite
    }
}