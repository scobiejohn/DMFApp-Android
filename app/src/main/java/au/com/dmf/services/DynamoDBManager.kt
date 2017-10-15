package au.com.dmf.services

import android.content.Context
import android.util.Log
import au.com.dmf.model.User
import au.com.dmf.utils.Constants
import com.amazonaws.AmazonServiceException
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.auth.CognitoCredentialsProvider
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import com.amazonaws.ClientConfiguration
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.vicpin.krealmextensions.queryFirst
import com.vicpin.krealmextensions.save
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator
import com.amazonaws.services.dynamodbv2.model.Condition


object DynamoDBManager {

    private val TAG = "DynamoDBManager"

    lateinit var ddb: AmazonDynamoDBClient
    lateinit var mapper: DynamoDBMapper

    fun initClient(context: Context, logins: HashMap<String, String>) {
        val credentials = CognitoCachingCredentialsProvider(context, Constants.COGNITO_IDENTITY_POOL_ID, Constants.COGNITO_REGIONTYPE)
        credentials.withLogins(logins)
        ddb = Region.getRegion(Regions.AP_SOUTHEAST_2) // CRUCIAL
                .createClient(
                        AmazonDynamoDBClient::class.java,
                        credentials,
                        ClientConfiguration()
                )
        mapper = DynamoDBMapper(ddb)
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

    fun getFundDetails(fundName: String, success: (DMFFundsTableRow) -> Unit, failure: () -> Unit) {
        val row = DMFFundsTableRow()
        row.FUND = fundName
        val queryExpression = DynamoDBQueryExpression<DMFFundsTableRow>()
                .withHashKeyValues(row)
        doAsync {
            try {
                val result = mapper.query(DMFFundsTableRow::class.java, queryExpression) as PaginatedList<DMFFundsTableRow>
                success(result.first())
            } catch (ex: Exception) {
                failure()
            }
        }
    }

    fun checkHistoryDataUploadTimestamp(success: () -> Unit, failure: () -> Unit) {
        val user = User().queryFirst()
        val userFileName = user!!.fundFile
        val row = DDDMFUserHistoryUploadedTableRow()
        row.UserFileName = userFileName
        val queryExpression = DynamoDBQueryExpression<DDDMFUserHistoryUploadedTableRow>()
                .withHashKeyValues(row)
        doAsync {
            val result = mapper.query(DDDMFUserHistoryUploadedTableRow::class.java, queryExpression) as PaginatedList<DDDMFUserHistoryUploadedTableRow>
            try {
                val timestamp = result[0].HistoryTimestamp!!.toInt()
                if (user.historyDataTimestamp != timestamp) {
                    user.historyDataTimestamp = timestamp
                    user.save()
                    uiThread {
                        success()
                    }
                    return@doAsync
                }
                uiThread {
                    failure()
                }
            }catch (ex: Exception){
                uiThread {
                    failure()
                }
            }
        }
    }

    fun checkAssetDataUploadTimestamp(success: () -> Unit, failure: () -> Unit) {
        val user = User().queryFirst()
        val userFileName = user!!.fundFile
        val row = DDDMFUserAssetUploadedTableRow()
        row.UserFileName = userFileName
        val queryExpression = DynamoDBQueryExpression<DDDMFUserAssetUploadedTableRow>()
                .withHashKeyValues(row)
        doAsync {
            val result = mapper.query(DDDMFUserAssetUploadedTableRow::class.java, queryExpression) as PaginatedList<DDDMFUserAssetUploadedTableRow>
            try {
                val assetDate = result[0].AssetDate
                println(assetDate)
                println(user.assetDate)
                if (user.assetDate != assetDate) {
                    user.assetDate = assetDate!!
                    user.save()
                    uiThread {
                        success()
                    }
                    return@doAsync
                }
                uiThread {
                    failure()
                }
            }catch (ex: Exception){
                uiThread {
                    failure()
                }
            }
        }
    }

    fun test(success: (ArrayList<DDDMFUserAssetUploadedTableRow>) -> Unit, failure: () -> Unit) {

//        val mapper = DynamoDBMapper(ddb)
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
        val user = User().queryFirst()
        val userFileName = user!!.fundFile

        val row = DDDMFUserDataHistoryFromS3TableRow()
        row.UserFileName = userFileName
        val queryExpression = DynamoDBQueryExpression<DDDMFUserDataHistoryFromS3TableRow>()
                .withHashKeyValues(row)
                .withScanIndexForward(false)
        doAsync {
            val result = mapper.queryPage(DDDMFUserDataHistoryFromS3TableRow::class.java, queryExpression)
            uiThread {
                success(ArrayList(result.results.sortedWith(compareBy( { it.HistoryDate }))))
            }
        }
    }

    fun getUserAssetData(success: (ArrayList<DDDMFUserDataAssetFromS3TableRow>) -> Unit, failure: () -> Unit) {
        val user = User().queryFirst()
        val userFileName = user!!.fundFile
        val assetDate = user!!.assetDate
        val row = DDDMFUserDataAssetFromS3TableRow()
        row.UserFileName = userFileName

        val expressionAttributesNames: HashMap<String,String> = HashMap()
        expressionAttributesNames.put("#AssetDate", "AssetDate")

        val expressionAttributeValues: HashMap<String,AttributeValue> = HashMap()
        expressionAttributeValues.put(":AssetDate", AttributeValue().withS(assetDate))

        val keyCondition = Condition().withComparisonOperator(ComparisonOperator.EQ).withAttributeValueList(AttributeValue().withS(assetDate))
        val queryExpression = DynamoDBQueryExpression<DDDMFUserDataAssetFromS3TableRow>()
                .withHashKeyValues(row)
                .withIndexName("UserFileName-AssetDate-index")
                .withRangeKeyCondition("AssetDate", keyCondition)
                .withConsistentRead(false)

        doAsync {
            try {
                val result = mapper.queryPage(DDDMFUserDataAssetFromS3TableRow::class.java, queryExpression)
                uiThread {
                    success(ArrayList(result.results))
                }
            } catch (ex: AmazonServiceException) {
                println(ex)
                uiThread {
                    failure()
                }
            }

        }

    }

    fun getNotifications(limit: Int, success: (ArrayList<DMFNotificationTableRow>) -> Unit, failure: () -> Unit) {
        val scanExpression = DynamoDBScanExpression()
        scanExpression.limit = limit
        doAsync {
            try {
                val result = mapper.scanPage(DMFNotificationTableRow::class.java, scanExpression).results
                val resultList = ArrayList<DMFNotificationTableRow>()
                result.forEach { row -> resultList.add(row) }

                uiThread {
                    success(resultList)
                }
            } catch (ex: AmazonServiceException) {
                uiThread {
                    failure()
                }
            }
        }
    }

    @DynamoDBTable(tableName = Constants.DMFUSERDATAHISTORYFROMS3TableName)
    class DDDMFUserDataHistoryFromS3TableRow {
        @get:DynamoDBHashKey(attributeName = "UserFileName")
        var UserFileName: String? = ""
        @get:DynamoDBAttribute(attributeName = "HistoryDate")
        var HistoryDate: String? = ""
        @get:DynamoDBAttribute(attributeName = "Id")
        var Id: String? = ""
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
        var HistoryTimestamp: Long? = -1
    }

    @DynamoDBTable(tableName = Constants.DMFUSERDATAASSETFROMS3TableName)
    class DDDMFUserDataAssetFromS3TableRow {
        @get:DynamoDBHashKey(attributeName = "Id")
        var Id: String? = ""
        @get:DynamoDBIndexRangeKey(attributeName = "AssetDate", globalSecondaryIndexName = "UserFileName-AssetDate-index")
        var AssetDate: String? = ""
        @get:DynamoDBIndexHashKey(attributeName = "UserFileName", globalSecondaryIndexName = "UserFileName-AssetDate-index")
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
        var AssetTimestamp: Long? = -1
    }

    @DynamoDBTable(tableName = Constants.DMFUSERDATAHISTORYUPLOADEDTableName)
    class DDDMFUserHistoryUploadedTableRow {
        @get:DynamoDBHashKey(attributeName = "UserFileName")
        var UserFileName: String? = ""
        @get:DynamoDBAttribute(attributeName = "HistoryTimestamp")
        var HistoryTimestamp: Long? = -1
    }

    @DynamoDBTable(tableName = Constants.DMFUSERDATAASSETUPLOADEDTableName)
    class DDDMFUserAssetUploadedTableRow {
        @get:DynamoDBHashKey(attributeName = "UserFileName")
        var UserFileName: String? = ""
        @get:DynamoDBAttribute(attributeName = "UploadTimestamp")
        var UploadTimestamp: Long? = -1
        @get:DynamoDBAttribute(attributeName = "AssetDate")
        var AssetDate: String? = ""
    }

    @DynamoDBTable(tableName = Constants.DMFFUNDSTableName)
    class DMFFundsTableRow {
        @get:DynamoDBHashKey(attributeName = "FUND")
        var FUND: String? = ""
        @get:DynamoDBAttribute(attributeName = "InMarket")
        var InMarket: String? = ""
        @get:DynamoDBAttribute(attributeName = "Investable")
        var Investable: String? = ""
    }

    @DynamoDBTable(tableName = Constants.DMFNOTIFICATIONTableName)
    class DMFNotificationTableRow {
        @get:DynamoDBHashKey(attributeName = "CreatedAt")
        var CreatedAt: Long? = -1
        @get:DynamoDBAttribute(attributeName = "Message")
        var Message: String? = ""
    }

    @DynamoDBTable(tableName = Constants.DMFSUBSCRIPTIONTableName)
    class DMFSubscriptionTableRow {
        @get:DynamoDBHashKey(attributeName = "UserFileName")
        var UserFileName: String? = ""
        @get:DynamoDBAttribute(attributeName = "EndpointArn")
        var EndpointArn: String? = ""
    }
}