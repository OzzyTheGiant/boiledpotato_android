package dreamcraft.boiledpotato.services

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiServiceCallback<T>(
    private val successCallback: (T) -> Unit
) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        response.body()?.let { successCallback(it) }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        return
    }
}