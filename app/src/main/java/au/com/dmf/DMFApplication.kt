package au.com.dmf

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import com.chibatching.kotpref.Kotpref
import io.realm.Realm
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import io.realm.RealmConfiguration



class DMFApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(2)
                .build()
        Realm.setDefaultConfiguration(config)

        Log.d("DMFApplication", "Realm file path: " + Realm.getDefaultConfiguration()?.path)

        Kotpref.init(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    private fun copyBundledRealmFile(inputStream: InputStream, outFileName: String) {

    }

}
