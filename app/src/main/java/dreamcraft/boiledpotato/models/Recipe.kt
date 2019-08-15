package dreamcraft.boiledpotato.models

import android.os.Parcel
import android.os.Parcelable

data class Recipe(
    val id: Int,
    val name: String,
    val prepMinutes: Int,
    val imageFileName: String
) : Parcelable {

    var servings = 0
    lateinit var ingredients: Array<Ingredient>
    lateinit var instructions: Array<RecipeInstructions>

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    ) {
        servings = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(prepMinutes)
        parcel.writeString(imageFileName)
        parcel.writeInt(servings)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}