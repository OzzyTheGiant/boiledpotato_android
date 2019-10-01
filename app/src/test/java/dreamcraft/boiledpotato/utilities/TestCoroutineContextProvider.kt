package dreamcraft.boiledpotato.utilities

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class TestCoroutineContextProvider : CoroutineContextProvider() {
    override val IO: CoroutineContext by lazy { Dispatchers.Unconfined }
}