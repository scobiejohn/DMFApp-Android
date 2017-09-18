package au.com.dmf.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User (
        @PrimaryKey open var name: String = "",
        open var password: String = "",
        open var pin: Int = 0,
        open var email: String = "",
        open var fundFile: String = "",
        open var sessionDuration: Int = 0,
        open var historyDataTimestamp: Int = -1,
        open var assetDate: String = "19700101"
) : RealmObject()