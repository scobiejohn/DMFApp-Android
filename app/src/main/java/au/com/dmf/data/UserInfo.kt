package au.com.dmf.data

import com.chibatching.kotpref.KotprefModel

object UserInfo : KotprefModel() {
    var username by stringPref()
    var password by stringPref()
    var filename by stringPref()
    var pin by stringPref()
    var appVersion by stringPref()
    var appStartTime by intPref()
}