package au.com.brightcapital.data

import au.com.brightcapital.utils.Constants
import java.util.*


class Task {
    var timeStamp: Date? = Date(0)
    var details: String = ""
    var id: String = ""
    var type: String = Constants.TASK_TYPE_OTHER
    var status: String = Constants.TASK_STATUS_OTHER
}
