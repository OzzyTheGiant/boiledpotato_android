package dreamcraft.boiledpotato.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.models.Recipe

class RecipeActivity : AppCompatActivity() {
    lateinit var recipe: Recipe

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe)

        recipe = intent.getParcelableExtra(IntentExtras.RECIPE)

        Toast.makeText(this, recipe.name, Toast.LENGTH_SHORT).show()
    }
}
