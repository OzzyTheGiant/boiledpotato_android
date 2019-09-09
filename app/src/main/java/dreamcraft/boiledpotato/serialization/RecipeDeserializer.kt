package dreamcraft.boiledpotato.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dreamcraft.boiledpotato.models.Recipe
import java.lang.reflect.Type

class RecipeDeserializer : JsonDeserializer<Recipe> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Recipe {
        val recipe = Recipe()
        val ingredients = ArrayList<String>()
        val instructions = ArrayList<String>()
        val jsonResponse = json?.asJsonObject

        recipe.servings = jsonResponse?.get("servings")?.asInt ?: 1

        // Extract ingredients from array in json object
        jsonResponse?.get("extendedIngredients")?.asJsonArray?.let {
            for (i in 0 until it.size()) {
                // "original string" contains amount, unit, and ingredient pre-formatted
                ingredients.add(it[i].asJsonObject.get("originalString").asString)
            }
        }

        // Extract recipe instructions from array in json object
        jsonResponse?.get("analyzedInstructions")?.asJsonArray?.let {
            for (i in 0 until it.size()) { // outer loop is due to multiple sets of steps for different recipe components
                it[i].asJsonObject.get("steps").asJsonArray.let { steps ->
                    for (step in 0 until steps.size()) {
                        // each "step" is a json object with the step number, the step instruction string,
                        // and ingredients and equipment needed. Only the step string is needed
                        instructions.add(steps[step].asJsonObject.get("step").asString)
                    }
                }
            }
        }

        recipe.ingredients = ingredients
        recipe.instructions = instructions
        return recipe
    }
}