package au.com.dmf.services

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.beust.klaxon.*
import org.json.JSONArray

object JiraServiceManager {

    const val TICKET_BASE_PATH = "https://stratanow.atlassian.net/rest/api/2/issue/"
    const val TICKET_QUERY_PATH = "https://stratanow.atlassian.net/rest/api/2/search"
    const val JIRA_AUTH = "Basic c29uZ3dhcmUuc3lkQGdtYWlsLmNvbTpSYXltNjc4OQ==" //"Basic Y29udGFjdEBkYXJsaW5nbWFjcm9mdW5kOmRtZmlvdXNlcjE3"
    const val CONTENT_TYPE = "application/json"


    fun getTasks(startAt: Int, callback: (JsonArray<JsonObject>) -> Unit) {

        AndroidNetworking.get(TICKET_QUERY_PATH)
                .addQueryParameter("jql", "project=CS&description~16cedf80ade01c62bdd1ae931d0492330c0b62bf294c08c095ce2fab21a9298d")
                .addQueryParameter("startAt", startAt.toString())
                .addQueryParameter("maxResults", "10")
                .addQueryParameter("fields", "summary,description,status,created,creator,customfield_10200,customfield_10201,transitions")
                .addHeaders("Authorization", JIRA_AUTH)
                .addHeaders("Content-Type", CONTENT_TYPE)
                .setPriority(Priority.LOW)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String?) {

                        println(response)
                        val parser = Parser()
                        val stringBuilder = StringBuilder(response)
                        val json = parser.parse(stringBuilder) as JsonObject
                        val issues = json.array<JsonObject>("issues")
                        val issue = issues?.get(0) as JsonObject
                        println(issue.obj("fields")?.string("summary"))

                        callback(issues)

                    }

                    override fun onError(anError: ANError?) {
                        println(anError)
                    }
                })

                /*
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        callback(response)
                    }

                    override fun onError(anError: ANError) {
                        println("error: " + anError.toString())
                    }
                })
                */
    }
}
