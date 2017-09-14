package au.com.dmf.services

import android.content.Context
import android.util.Log
import au.com.dmf.utils.Constants
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.auth.CognitoCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import com.amazonaws.ClientConfiguration



object DynamoDBManager {

    private val TAG = "DynamoDBManager"

    var ddb: AmazonDynamoDBClient? = null

    fun initClient(context: Context, logins: HashMap<String, String>) {
        val credentials = CognitoCachingCredentialsProvider(context, Constants.COGNITO_IDENTITY_POOL_ID, Constants.COGNITO_REGIONTYPE)
        credentials.withLogins(logins)
        ddb = Region.getRegion(Regions.AP_SOUTHEAST_2) // CRUCIAL
                .createClient(
                        AmazonDynamoDBClient::class.java,
                        credentials,
                        ClientConfiguration()
                )
    }

    fun wipeCredentialsOnAuthError(ex: AmazonServiceException): Boolean {
        Log.e(TAG, "Error: wipeCredentialsOnAuthError called " + ex)
        // STS
        // http://docs.amazonwebservices.com/STS/latest/APIReference/CommonErrors.html
        if (ex.errorCode == "IncompleteSignature"
                || ex.errorCode == "InternalFailure"
                || ex.errorCode == "InvalidClientTokenId"
                || ex.errorCode == "OptInRequired"
                || ex.errorCode == "RequestExpired"
                || ex.errorCode == "ServiceUnavailable"

                // DynamoDB
                // http://docs.amazonwebservices.com/amazondynamodb/latest/developerguide/ErrorHandling.html#APIErrorTypes
                || ex.errorCode == "AccessDeniedException"
                || ex.errorCode == "IncompleteSignatureException"
                || ex.errorCode == "MissingAuthenticationTokenException"
                || ex.errorCode == "ValidationException"
                || ex.errorCode == "InternalFailure"
                || ex.errorCode == "InternalServerError"){

            return true
        }

        return false
    }

    fun test(success: (ArrayList<DDDMFUserAssetUploadedTableRow>) -> Unit, failure: () -> Unit) {

        val mapper = DynamoDBMapper(ddb)
        val scanExpression = DynamoDBScanExpression()

        doAsync {
            try {
                val result = mapper.scan(DDDMFUserAssetUploadedTableRow::class.java, scanExpression)
                val resultList = ArrayList<DDDMFUserAssetUploadedTableRow>()
                result.forEach { row -> resultList.add(row) }

                uiThread {
                    success(resultList)
                }
            } catch (ex: AmazonServiceException) {
                wipeCredentialsOnAuthError(ex)
                uiThread {
                    failure()
                }
            }
        }

    }

    fun getUserHistoryData(success: (ArrayList<DDDMFUserDataHistoryFromS3TableRow>) -> Unit, failure: () -> Unit) {

    }


    @DynamoDBTable(tableName = "DMFUSERDATAHISTORYFROMS3TableName")
    class DDDMFUserDataHistoryFromS3TableRow {
        @get:DynamoDBHashKey(attributeName = "Id")
        var Id: String? = ""
        @get:DynamoDBAttribute(attributeName = "HistoryDate")
        var HistoryDate: String? = ""
        @get:DynamoDBAttribute(attributeName = "UserFileName")
        var UserFileName: String? = ""
        @get:DynamoDBAttribute(attributeName = "Gross")
        var Gross: String? = ""
        @get:DynamoDBAttribute(attributeName = "Net")
        var Net: String? = ""
        @get:DynamoDBAttribute(attributeName = "FeeT")
        var FeeT: String? = ""
        @get:DynamoDBAttribute(attributeName = "FeeM")
        var FeeM: String? = ""
        @get:DynamoDBAttribute(attributeName = "FeeP")
        var FeeP: String? = ""
        @get:DynamoDBAttribute(attributeName = "Capital")
        var Capital: String? = ""
        @get:DynamoDBAttribute(attributeName = "Risk")
        var Risk: String? = ""
        @get:DynamoDBAttribute(attributeName = "HistoryTimestamp")
        var HistoryTimestamp: Int? = -1
    }

    @DynamoDBTable(tableName = "DMFUSERDATAASSETFROMS3TableName")
    class DDDMFUserDataAssetFromS3TableRow {
        @get:DynamoDBHashKey(attributeName = "Id")
        var Id: String? = ""
        @get:DynamoDBAttribute(attributeName = "AssetDate")
        var AssetDate: String? = ""
        @get:DynamoDBAttribute(attributeName = "UserFileName")
        var UserFileName: String? = ""
        @get:DynamoDBAttribute(attributeName = "Asset")
        var Asset: String? = ""
        @get:DynamoDBAttribute(attributeName = "Code")
        var Code: String? = ""
        @get:DynamoDBAttribute(attributeName = "Futs")
        var Futs: String? = ""
        @get:DynamoDBAttribute(attributeName = "Price")
        var Price: String? = ""
        @get:DynamoDBAttribute(attributeName = "AudExposure")
        var AudExposure: String? = ""
        @get:DynamoDBAttribute(attributeName = "AssetName")
        var AssetName: String? = ""
        @get:DynamoDBAttribute(attributeName = "AssetTimestamp")
        var AssetTimestamp: Int? = -1
    }

    @DynamoDBTable(tableName = Constants.DMFUSERDATAHISTORYUPLOADEDTableName)
    class DDDMFUserHistoryUploadedTableRow {
        @get:DynamoDBHashKey(attributeName = "UserFileName")
        var UserFileName: String? = ""
        @get:DynamoDBAttribute(attributeName = "HistoryTimestamp")
        var HistoryTimestamp: Int? = -1
    }

    @DynamoDBTable(tableName = Constants.DMFUSERDATAASSETUPLOADEDTableName)
    class DDDMFUserAssetUploadedTableRow {
        @get:DynamoDBHashKey(attributeName = "UserFileName")
        var UserFileName: String? = ""
        @get:DynamoDBAttribute(attributeName = "HistoryTimestamp")
        var HistoryTimestamp: Int? = -1
        @get:DynamoDBAttribute(attributeName = "AssetDate")
        var AssetDate: String? = ""
    }

}