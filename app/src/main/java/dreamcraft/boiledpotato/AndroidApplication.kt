package dreamcraft.boiledpotato

import android.app.Application
import dreamcraft.boiledpotato.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

open class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@AndroidApplication)
            modules(appModule)
        }
    }
}