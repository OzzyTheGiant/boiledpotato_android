package dreamcraft.boiledpotato.di

import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.viewmodels.RecipeViewModel
import dreamcraft.boiledpotato.viewmodels.RecipeViewModelStub
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModelStub
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val appTestModule : Module = module(override = true) {
    viewModel<SearchResultsViewModel> { SearchResultsViewModelStub() }
    viewModel<RecipeViewModel> { RecipeViewModelStub() }

    single<List<Recipe>> {
        val recipes = ArrayList<Recipe>(10)
        for (i in 0..9) recipes.add(i, Recipe(i.toLong(), "Chicken Soup $i", 60, "chicken-soup.jpg"))
        recipes
    }
}