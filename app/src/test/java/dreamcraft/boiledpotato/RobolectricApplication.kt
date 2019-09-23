package dreamcraft.boiledpotato

import com.squareup.picasso.Picasso
import org.mockito.Answers
import org.mockito.Mockito

class RobolectricApplication : AndroidApplication() {
    override fun onCreate() {
        super.onCreate()

        if (!isPicassoInitialized) {
            isPicassoInitialized = true
            val picasso = Mockito.mock(Picasso::class.java, Answers.RETURNS_MOCKS)
            Picasso.setSingletonInstance(picasso)
        }
    }

    companion object {
        private var isPicassoInitialized = false
    }
}