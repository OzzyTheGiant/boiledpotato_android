package dreamcraft.boiledpotato.services

import okhttp3.Interceptor
import okhttp3.Response

class RestApiHttpInterceptor(
    private val webApiUrl: String,
    private val webApiKey: String
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder()
            .header("X-RapidAPI-Host", webApiUrl)
            .header("X-RapidAPI-Key", webApiKey)
            .method(request.method(), request.body())
            .build()

        return chain.proceed(request)
    }
}