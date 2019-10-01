package dreamcraft.boiledpotato.di

import androidx.room.Room
import com.github.simonpercic.oklog3.OkLogInterceptor
import com.google.gson.GsonBuilder
import dreamcraft.boiledpotato.R
import dreamcraft.boiledpotato.database.AppDatabase
import dreamcraft.boiledpotato.models.Recipe
import dreamcraft.boiledpotato.models.RecipeSearchQuery
import dreamcraft.boiledpotato.repositories.RecipeRepository
import dreamcraft.boiledpotato.serialization.RecipeDeserializer
import dreamcraft.boiledpotato.serialization.RecipesArrayDeserializer
import dreamcraft.boiledpotato.services.RestApiHttpInterceptor
import dreamcraft.boiledpotato.services.RestApiService
import dreamcraft.boiledpotato.utilities.CoroutineContextProvider
import dreamcraft.boiledpotato.viewmodels.RecipeViewModel
import dreamcraft.boiledpotato.viewmodels.SearchResultsViewModel
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val appModule : Module = module {
    viewModel { SearchResultsViewModel() }
    viewModel { RecipeViewModel() }

    single { RecipeRepository() }
    single(named("webApiResultsSize")) { 10 }
    single { CoroutineContextProvider() }

    single { Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "BoiledPotato").build() }
    single { get<AppDatabase>().recipeDao() }

    single { // Retrofit API service
        val webApiProtocol = androidContext().getString(R.string.WEB_API_PROTOCOL)
        val webApiHost = androidContext().getString(R.string.WEB_API_URL)
        val webApiUrl = "$webApiProtocol://$webApiHost"
        val webApiKey = androidContext().getString(R.string.WEB_API_KEY)
        
        // create custom http client to add required api headers
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(RestApiHttpInterceptor(webApiHost, webApiKey))
            .addInterceptor(OkLogInterceptor.builder().build())
            .build()

        // create custom gson serialization object to deserialize search results into a SparseArray
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(RecipeSearchQuery::class.java, RecipesArrayDeserializer())
        gsonBuilder.registerTypeAdapter(Recipe::class.java, RecipeDeserializer())
        val gsonConverterFactory = GsonConverterFactory.create(gsonBuilder.create())

        // add all retrofit components together and build REST API service
        val retrofit = Retrofit.Builder()
            .baseUrl(webApiUrl)
            .client(httpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

        retrofit.create<RestApiService>()
    }
}