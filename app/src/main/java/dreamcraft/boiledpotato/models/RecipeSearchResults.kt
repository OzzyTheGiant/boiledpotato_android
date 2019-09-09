package dreamcraft.boiledpotato.models

import androidx.room.*

/** This class models a many to many relationship between a RecipeSearchQueries and Recipes.
 *  Ideally this class should not exist and we should be able to label search recipes with
 *  cuisine and make them searchable by keywords in recipe name, but the API does not provide
 *  cuisine name on recipe data, so recipes searched without a cuisine filter will be saved to
 *  DB but when adding a cuisine filter later, even if the recipe is part of a cuisine, it will
 *  not be marked as such and so an unnecessary API call would have to be made. This should
 *  alleviate that problem */
@Entity(
    tableName = "SearchResults",
    indices = [Index(value = ["SearchID", "RecipeID"], unique = true)],
    foreignKeys = [ // ForeignKey is an annotation used as a class to define Key columns
        ForeignKey(
            entity = RecipeSearchQuery::class,
            parentColumns = ["ID"],
            childColumns = ["SearchID"]
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["ID"],
            childColumns = ["RecipeID"]
        )
    ]
)
class RecipeSearchResults() {
    @PrimaryKey @Embedded var pk = CompositeKey(0, 0)

    constructor(searchId: Long, recipeId: Long) : this() {
        pk.searchId = searchId; pk.recipeId = recipeId
    }

    data class CompositeKey (
        @ColumnInfo(name = "SearchID")  var searchId: Long,
        @ColumnInfo(name = "RecipeID")  var recipeId: Long
    )
}