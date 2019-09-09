package dreamcraft.boiledpotato.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/** this class is to model a json object returned from Retrofit */
@Entity(tableName = "SearchQueries") data class RecipeSearchQuery(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")             var id: Long = 0,
    @ColumnInfo(name = "Keywords")       var keywords: String = "",
    @ColumnInfo(name = "Cuisine")        var cuisine: String = "",
    @ColumnInfo(name = "ResultsAmount")  var totalResults: Int = 0,
    @ColumnInfo(name = "ExpirationTime") var expires: Long = 0
) {
    @Ignore var recipes: List<Recipe>? = null

    fun isStale() : Boolean =  expires > System.currentTimeMillis()
}