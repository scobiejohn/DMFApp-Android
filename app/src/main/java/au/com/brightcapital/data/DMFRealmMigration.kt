package au.com.brightcapital.data

import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * Created by raymond on 19/11/17.
 */
class DMFRealmMigration: RealmMigration {

    override fun migrate(realm: DynamicRealm?, oldVersion: Long, newVersion: Long) {
        val schema = realm?.schema
        if (oldVersion.toInt() == 2) {
            val userSchema = schema?.get("User")
            userSchema?.addField("reportingPeriod", String::class.java)
        }
    }


}