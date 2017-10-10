package au.com.dmf.services

import au.com.dmf.data.UserInfo
import au.com.dmf.model.User
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.StringRequestListener
import com.beust.klaxon.*
import com.mcxiaoke.koi.HASH
import com.vicpin.krealmextensions.queryFirst
import org.json.JSONObject

object JiraServiceManager {

    const val TICKET_BASE_PATH = "https://stratanow.atlassian.net/rest/api/2/issue/"
    const val TICKET_QUERY_PATH = "https://stratanow.atlassian.net/rest/api/2/search"
    const val JIRA_AUTH = "Basic c29uZ3dhcmUuc3lkQGdtYWlsLmNvbTpSYXltNjc4OQ==" //"Basic Y29udGFjdEBkYXJsaW5nbWFjcm9mdW5kOmRtZmlvdXNlcjE3"
    const val CONTENT_TYPE = "application/json"


    fun getTasks(startAt: Int, callback: (JsonArray<JsonObject>, Int) -> Unit) {
        val user = User().queryFirst()
        AndroidNetworking.get(TICKET_QUERY_PATH)
                .addQueryParameter("jql", "project=CS&description~" + HASH.sha256(user!!.name) )
                .addQueryParameter("startAt", startAt.toString())
                .addQueryParameter("maxResults", "10")
                .addQueryParameter("fields", "summary,description,status,created,creator,customfield_10200,customfield_10201,transitions")
                .addHeaders("Authorization", JIRA_AUTH)
                .addHeaders("Content-Type", CONTENT_TYPE)
                .setPriority(Priority.LOW)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String?) {
                        val parser = Parser()
                        val stringBuilder = StringBuilder(response)
                        val json = parser.parse(stringBuilder) as JsonObject
                        val total = json.int("total")
                        val issues = json.array<JsonObject>("issues")
                        callback(issues!!, total!!)
                    }

                    override fun onError(anError: ANError?) {
                        println(anError)
                    }
                })
    }

    fun createTicket(type: String, fundName: String, fundMode: String? = null, amount: String? = null, multiplier: String? = null, tos: String? = null,
                     success: () -> Unit, failure: () -> Unit) {
        val user = User().queryFirst()
        var description = when(type) {
            "Redeem Funds" -> "Redemption of " + amount!! + " from - " + fundName
            "Switch Fund Mode" -> "Switch Fund mode to " + fundMode!!.toUpperCase() + " for - " + fundName
            "Transfer Funds" -> "Transfer of " + amount!! + " from - " + fundName
            "Fund Multiplier" -> "Change Multiplier to " + multiplier!! + " for - " + fundName
            "Introduction Request" -> "Introduced to " + tos!!
            "Social Media Share" -> "Social Media Share"
            "Cash Allocation Change" -> "Cash Allocation Change Request for - " + fundName + " : " + amount!!
            else -> ""
        }

        val json = JSONObject()
        val fieldsJSON = JSONObject()
        val projectJSON = JSONObject()
        projectJSON.put("key", "CS")
        fieldsJSON.put("project", projectJSON)
        fieldsJSON.put("summary", description)
        fieldsJSON.put("description", HASH.sha256(user!!.name))
        fieldsJSON.put("customfield_10200", user!!.name + " from Android")
        fieldsJSON.put("customfield_10201", user!!.email)
        val issueTypeJSON = JSONObject()
        issueTypeJSON.put("name", type)
        fieldsJSON.put("issuetype", issueTypeJSON)
        json.put("fields", fieldsJSON)

        AndroidNetworking.post(TICKET_BASE_PATH)
                .addJSONObjectBody(json)
                .addHeaders("Authorization", JIRA_AUTH)
                .addHeaders("Content-Type", CONTENT_TYPE)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String?) {
                        //UserInfo.ticketSubmitted = true
                        success()
                    }

                    override fun onError(anError: ANError?) {
                        println(anError)
                        failure()
                    }
                })

    }

    fun withdrawTicket(id: String) {
        val json = JSONObject()
        val transitionJSON = JSONObject()
        transitionJSON.put("id", "11")
        json.put("transition", transitionJSON)
        AndroidNetworking.post(TICKET_BASE_PATH + id + "/transitions")
                .addJSONObjectBody(json)
                .addHeaders("Authorization", JIRA_AUTH)
                .addHeaders("Content-Type", CONTENT_TYPE)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String?) {
                    }

                    override fun onError(anError: ANError?) {
                        println(anError)
                    }
                })
    }
}
