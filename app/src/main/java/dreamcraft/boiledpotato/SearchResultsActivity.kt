package dreamcraft.boiledpotato

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class SearchResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        Toast.makeText(applicationContext, intent.getCharSequenceExtra(GLOBALS.EXTRAS_SEARCH), Toast.LENGTH_SHORT).show()
    }
}
