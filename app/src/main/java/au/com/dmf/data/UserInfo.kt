package au.com.dmf.data

import com.chibatching.kotpref.KotprefModel

object UserInfo : KotprefModel() {
    var pin by stringPref(default = "")
    var appVersion by stringPref()
    var appStartTime by intPref()
    var ticketSubmitted by booleanPref(default = false)
}