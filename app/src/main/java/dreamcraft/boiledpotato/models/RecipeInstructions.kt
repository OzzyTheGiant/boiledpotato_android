package dreamcraft.boiledpotato.models

data class RecipeInstructions(
    val instructionName : String,
    val forRecipe: Recipe,
    val steps : Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as RecipeInstructions
        if (instructionName != other.instructionName) return false
        if (forRecipe != other.forRecipe) return false
        if (!steps.contentEquals(other.steps)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = instructionName.hashCode()
        result = 31 * result + forRecipe.hashCode()
        result = 31 * result + steps.contentHashCode()
        return result
    }
}