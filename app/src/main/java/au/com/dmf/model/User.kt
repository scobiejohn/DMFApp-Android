package au.com.dmf.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class User (
        @PrimaryKey open var id: String = "",
        open var password: String = "",
        open var pin: Int = 0,
        open var email: String = "",
        open var name: String = "",
        open var fundFile: String = "",
        open var sessionDuration: Int = 0
) : RealmObject()