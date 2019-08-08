package dreamcraft.boiledpotato.services

import dreamcraft.boiledpotato.repositories.Resource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RestApiServiceCallback<T>(
    private val sendResource: (Resource<T>) -> Unit
) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            sendResource(Resource.Success(responseBody))
        } else {
            val error = response.errorBody()?.string() ?: "500"
            sendResource(Resource.Error(error))
        }
    }

    override fun onFailure(call: Call<T>, error: Throwable) {
        if (error is IOException) {
            sendResource(Resource.Error("000"))
        } else {
            sendResource(Resource.Error("400"))
        }
    }
}