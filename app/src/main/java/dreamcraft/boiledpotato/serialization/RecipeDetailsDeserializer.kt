package dreamcraft.boiledpotato.serialization

import android.util.SparseArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dreamcraft.boiledpotato.models.JsonRecipeDetails
import java.lang.reflect.Type

class RecipeDetailsDeserializer : JsonDeserializer<JsonRecipeDetails> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): JsonRecipeDetails {
        val ingredients = SparseArray<String>()
        val instructions = SparseArray<String>()
        val jsonResponse = json?.asJsonObject
        val servings = jsonResponse?.get("servings")?.asInt ?: 1

        // Extract ingredients from array in json object
        jsonResponse?.get("extendedIngredients")?.asJsonArray?.let {
            for (i in 0 until it.size()) {
                // "original string" contains amount, unit, and ingredient pre-formatted
                ingredients.append(i, it[i].asJsonObject.get("originalString").asString)
            }
        }

        // Extract recipe instructions from array in json object
        jsonResponse?.get("analyzedInstructions")?.asJsonArray?.let {
            for (i in 0 until it.size()) { // outer loop is due to multiple sets of steps for different recipe components
                it[i].asJsonObject.get("steps").asJsonArray.let { steps ->
                    for (step in 0 until steps.size()) {
                        // each "step" is a json object with the step number, the step instruction string,
                        // and ingredients and equipment needed. Only the step string is needed
                        instructions.append(instructions.size(), steps[step].asJsonObject.get("step").asString)
                    }
                }
            }
        }

        return JsonRecipeDetails(ingredients, instructions, servings)
    }
}