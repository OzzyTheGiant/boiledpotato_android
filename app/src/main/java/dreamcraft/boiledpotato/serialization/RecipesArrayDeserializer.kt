package dreamcraft.boiledpotato.serialization

import android.util.SparseArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.JsonRecipesList
import java.lang.reflect.Type

class RecipesArrayDeserializer : JsonDeserializer<JsonRecipesList> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): JsonRecipesList {
        val recipes = SparseArray<Recipe>()
        val totalResults = json?.asJsonObject?.get("totalResults")?.asInt ?: 0
        val expires = json?.asJsonObject?.get("expires")?.asLong ?: 0

        json?.asJsonObject?.get("results")?.asJsonArray?.let {
            for (i in 0 until it.size()) {
                val result = it[i].asJsonObject
                val recipe = Recipe(
                    result.get("id").asInt,
                    result.get("title").asString,
                    result.get("readyInMinutes").asInt,
                    result.get("image").asString
                )
                recipes.append(i, recipe)
            }
        }

        return JsonRecipesList(recipes, totalResults, expires)
    }
}