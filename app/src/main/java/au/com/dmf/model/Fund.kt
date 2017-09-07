package au.com.dmf.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Fund (
    @PrimaryKey open var id: String = UUID.randomUUID().toString(),
    open var multipliers: String = "",
    open var multiplier: String = "2x",
    open var mode: String = "passive",
    open var name: String = "",
    open var amount: Int = 0
) : RealmObject()
