package dreamcraft.boiledpotato.serialization

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DeserializersUnitTest {
    private val recipesArrayDeserializer = RecipesArrayDeserializer()
    private val recipeDeserializer = RecipeDeserializer()
    private val queryJson = JsonObject()
    private val recipeJson = JsonObject()

    @Test fun recipesArrayDeserializerCreatesRecipeSearchQuery() {
        val results = JsonArray(2)
        for (i in 0..1) {
            results.add(JsonObject().apply {
                addProperty("id", i)
                addProperty("title", "Chicken Soup")
                addProperty("readyInMinutes", 60)
                addProperty("image", "chk.jpg")
            })
        }

        queryJson.apply {
            addProperty("totalResults", 12)
            addProperty("expires", 1234567890L)
            add("results", results)
        }

        val query = recipesArrayDeserializer.deserialize(queryJson, null, null)
        assertEquals(query.recipes!!.size, queryJson.get("results").asJsonArray.size())
        assertEquals(query.totalResults, queryJson.get("totalResults").asInt)
        assertEquals(query.expires, queryJson.get("expires").asLong)
    }

    @Test fun recipeDeserializerCreatesRecipe() {
        val ingredients = JsonArray(3).apply {
            for (i in 0..2) {
                add(JsonObject().apply {
                    addProperty("originalString", "ingredient $i")
                })
            }
        }

        val instructions = JsonArray().apply {
            add(JsonObject().apply{
                add("steps", JsonArray().apply {
                    for (i in 0..2) add(JsonObject().apply {
                        addProperty("step", "instruction $i")
                    })
                })
            })
        }

        recipeJson.apply {
            addProperty("servings", 4)
            add("extendedIngredients", ingredients)
            add("analyzedInstructions", instructions)
        }

        val recipe = recipeDeserializer.deserialize(recipeJson, null, null)
        assertEquals(recipe.servings, 4)
        assertEquals(recipe.ingredients!!.size, ingredients.size())
        assertEquals(recipe.instructions!!.size, instructions[0].asJsonObject.get("steps").asJsonArray.size())
    }
}