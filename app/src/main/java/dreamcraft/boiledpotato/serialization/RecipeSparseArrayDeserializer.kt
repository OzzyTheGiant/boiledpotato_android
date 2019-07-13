package dreamcraft.boiledpotato.serialization

import android.util.SparseArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dreamcraft.boiledpotato.models.Recipe
import java.lang.reflect.Type

class RecipeSparseArrayDeserializer : JsonDeserializer<SparseArray<Recipe>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): SparseArray<Recipe> {
        val recipes = SparseArray<Recipe>()
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
        return recipes
    }
}