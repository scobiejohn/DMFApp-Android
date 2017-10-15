package au.com.dmf.data

import au.com.dmf.services.DynamoDBManager

object FundsDataManager {

    var dmfHistoryData: ArrayList<DynamoDBManager.DDDMFUserDataHistoryFromS3TableRow> = ArrayList()
    var dmfAssetsData: ArrayList<DynamoDBManager.DDDMFUserDataAssetFromS3TableRow> = ArrayList()

}