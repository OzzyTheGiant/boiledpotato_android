package dreamcraft.boiledpotato.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dreamcraft.boiledpotato.models.Favorite
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.models.RecipeSearchResults

@Database(
    entities = [
        Recipe::class,
        RecipeSearchQuery::class,
        RecipeSearchResults::class,
        Favorite::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDAO
}