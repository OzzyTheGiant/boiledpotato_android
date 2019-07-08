package dreamcraft.boiledpotato.models

class Recipe(
    var id: Int,
    var name: String,
    var prepMinutes: Int,
    var servings: Int,
    var imageFileName: String
) {}