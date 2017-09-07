package au.com.dmf.data

import au.com.dmf.utils.Constants
import java.time.LocalDate
import java.util.*


class Task {
    var timeStamp: Date? = Date(0)
    var details: String = ""
    var id: String = ""
    var type: String = Constants.TASK_TYPE_OTHER
    var status: String = Constants.TASK_STATUS_OTHER
}
