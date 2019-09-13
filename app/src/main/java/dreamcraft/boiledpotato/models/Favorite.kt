package dreamcraft.boiledpotato.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "Favorites")
data class Favorite(@PrimaryKey @ColumnInfo(name = "RecipeID") val recipeId : Long)