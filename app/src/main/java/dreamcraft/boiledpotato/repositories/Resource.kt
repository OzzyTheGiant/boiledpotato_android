package dreamcraft.boiledpotato.repositories

/** A wrapper class to contain result data from repository sources or error message if data retrieval failed */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String) : Resource<T>(message = message)
    class Loading<T>() : Resource<T>()
}