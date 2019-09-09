package dreamcraft.boiledpotato.repositories

import retrofit2.Response
import java.io.IOException

/** base Repository class for any repository objects that need to call an API function */
open class Repository {
    private val NETWORK_ERROR_CODE = "000"
    private val DATA_ERROR_CODE = "400"
    private val SERVER_ERROR_CODE = "500"

    protected suspend fun <T> callApi(action: suspend () -> Response<T>) : Resource<T> {
        val response : Response<T>
        val responseBody : T?

        try {
            response = action()
        } catch (e: Exception) {
            return Resource.Error(if (e is IOException) NETWORK_ERROR_CODE else DATA_ERROR_CODE)
        }

        responseBody = response.body()

        return if (response.isSuccessful && responseBody != null) {
            Resource.Success(responseBody)
        } else {
            Resource.Error(response.errorBody()?.string() ?: SERVER_ERROR_CODE)
        }
    }
}