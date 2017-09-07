package au.com.dmf.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class User (
        @PrimaryKey open var id: String = UUID.randomUUID().toString(),
        open var password: String = "",
        open var pin: Int = 0,
        open var email: String = "",
        open var name: String = "",
        open var sessionDuration: Int = 0
) : RealmObject()