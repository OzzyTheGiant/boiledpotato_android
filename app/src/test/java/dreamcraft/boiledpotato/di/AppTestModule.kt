package dreamcraft.boiledpotato.di

import android.util.SparseArray
import androidx.room.Room
import dreamcraft.boiledpotato.database.AppDatabase
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.utilities.CoroutineContextProvider
import dreamcraft.boiledpotato.utilities.TestCoroutineContextProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appTestModule : Module = module(override = true) {
    single {
        Room
            .inMemoryDatabaseBuilder(androidApplication(), AppDatabase::class.java)
            .allowMainThreadQueries().build()
    }

    single { get<AppDatabase>().recipeDao() }
    single<CoroutineContextProvider> { TestCoroutineContextProvider() }

    single<List<Recipe>> {
        val recipes = ArrayList<Recipe>(10)
        for (i in 0..9) recipes.add(i, Recipe(i.toLong(), "Chicken Soup $i", 60, "chicken-soup.jpg"))
        recipes
    }

    single {
        val recipeArray = SparseArray<Recipe>()
        val recipes = get<List<Recipe>>()
        for (i in recipes.indices) recipeArray.append(i, recipes[i])
        recipeArray
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