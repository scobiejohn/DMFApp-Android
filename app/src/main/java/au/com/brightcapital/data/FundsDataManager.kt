package au.com.brightcapital.data

import au.com.brightcapital.services.DynamoDBManager

object FundsDataManager {

    var dmfHistoryData: ArrayList<DynamoDBManager.DDDMFUserDataHistoryFromS3TableRow> = ArrayList()
    var dmfAssetsData: ArrayList<DynamoDBManager.DDDMFUserDataAssetFromS3TableRow> = ArrayList()

}