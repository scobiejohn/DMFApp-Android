package au.com.dmf.services

import au.com.dmf.utils.Constants
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList

object DynamoDBManager {

    private val TAG = "DynamoDBManager"


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