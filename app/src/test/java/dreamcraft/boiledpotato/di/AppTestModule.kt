package dreamcraft.boiledpotato.di

import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.utilities.CoroutineContextProvider
import dreamcraft.boiledpotato.utilities.TestCoroutineContextProvider
import dreamcraft.boiledpotato.viewmodels.RecipeViewModel
import dreamcraft.boiledpotato.viewmodels.RecipeViewModelStub
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModelStub
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appTestModule : Module = module(override = true) {
    viewModel<SearchResultsViewModel> { SearchResultsViewModelStub() }
    viewModel<RecipeViewModel> { RecipeViewModelStub() }
    single<CoroutineContextProvider> { TestCoroutineContextProvider() }

    single<List<Recipe>> {
        val recipes = ArrayList<Recipe>(10)
        for (i in 0..9) recipes.add(i, Recipe(i.toLong(), "Chicken Soup $i", 60, "chicken-soup.jpg"))
        recipes
    }

    single(named("ingredients")) {
        listOf(
            "2 cups of Chicken Broth",
            "1/2 lb of Chicken Breast",
            "1 pack of noodles"
        )
    }

    single(named("instructions")) {
        listOf(
            "Grill chicken breast in pan",
            "Boil noodles in chicken broth",
            "Add chicken to soup",
            "Serve"
        )
    }

    single {
        val recipe = Recipe(1, "Chicken Soup", 60, "chicken-soup.jpg")
        recipe.ingredients = get(named("ingredients"))
        recipe.instructions = get(named("instructions"))
        recipe
    }
}