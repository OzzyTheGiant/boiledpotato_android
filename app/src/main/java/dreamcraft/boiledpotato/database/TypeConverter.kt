package dreamcraft.boiledpotato.database

import androidx.room.TypeConverter

class TypeConverter {
    private val delimiter = "#!"

    @TypeConverter fun createTextFromStringList(strings: List<String>?) : String {
        var text = ""
        if (strings == null) return text
        for (i in strings.indices) {
            text += strings[i]
            if (i < strings.size - 1) text += delimiter
        }
        return text
    }

    @TypeConverter fun createStringListFromText(text: String) : List<String>? {
        if (text == "") return null
        return text.split(delimiter)
    }
}