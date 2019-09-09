package dreamcraft.boiledpotato.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dreamcraft.boiledpotato.database.TypeConverter

@Entity(tableName="Recipes")
@TypeConverters(TypeConverter::class)
data class Recipe(
    @PrimaryKey @ColumnInfo(name="ID")  var id: Long = 0,
    @ColumnInfo(name="Name")            var name: String = "",
    @ColumnInfo(name="PreparationTime") var prepMinutes: Int = 0,
    @ColumnInfo(name="ImageFileName")   var imageFileName: String = ""
) : Parcelable {

    @ColumnInfo(name = "Servings") var servings = 0
    @ColumnInfo(name = "Ingredients") var ingredients: List<String>? = null
    @ColumnInfo(name = "Instructions") var instructions: List<String>? = null

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: ""
    ) {
        servings = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
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