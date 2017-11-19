package au.com.brightcapital

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import au.com.brightcapital.data.DMFRealmMigration
import com.chibatching.kotpref.Kotpref
import io.realm.Realm
import java.io.InputStream
import io.realm.RealmConfiguration

class DMFApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
                .name("default.realm")
                .schemaVersion(3)
                .migration(DMFRealmMigration())
                .build()
        Realm.setDefaultConfiguration(config)

        // This will automatically trigger the migration if needed
        val realm = Realm.getDefaultInstance()

        Log.d("DMFApplication", "Realm file path: " + Realm.getDefaultConfiguration()?.path)

        Kotpref.init(this)

    }

    override fun onTerminate() {
        Realm.getDefaultInstance().close()
        super.onTerminate()
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
