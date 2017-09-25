package au.com.dmf.utils

import com.amazonaws.regions.Regions

class Constants {

    companion object {
        const val CognitoIdentityUserPoolId = "ap-southeast-2_fAJM772JL"
        const val CognitoIdentityUserPoolAppClientId = "2o0od2g1q958ofo7h5dp8ssrdt"
        const val CognitoIdentityUserPoolAppClientSecret = "9df9jh9d5mgqc6mij3trrqg3n9tu6er44vdeiei6j2k98p8t02"

        const val AWSCognitoUserPoolsSignInProviderKey = "UserPool"

        @JvmField val COGNITO_REGIONTYPE = Regions.AP_SOUTHEAST_2
        const val COGNITO_IDENTITY_POOL_ID = "ap-southeast-2:c1b9eed9-990a-425e-9f64-c6727c5ba3c9"

        const val AWS_MOBILE_ANALYTICS_ID = "427bc5c1bbf84ea9a12d4256d10676f4"
        //static let AWS_NOTIFICATION_TOPIC_ARN = "arn:aws:sns:ap-southeast-2:821002364208:DMF_DEV_NOTIFICATION"
        const val AWS_NOTIFICATION_TOPIC_ARN = "arn:aws:sns:ap-southeast-2:821002364208:DMF_PROD_NOTIFICATION"
        //static let AWS_SNS_APP_ARN = "arn:aws:sns:ap-southeast-2:821002364208:app/APNS_SANDBOX/DMF_DEV"
        const val AWS_SNS_APP_ARN = "arn:aws:sns:ap-southeast-2:821002364208:app/APNS/DMF_PROD"

        const val DEVICE_TOKEN_KEY = "DeviceToken"
        const val COGNITO_DEVICE_TOKEN_KEY = "CognitoDeviceToken"
        const val COGNITO_PUSH_NOTIF = "CognitoPushNotification"


        const val AWSDynamoDBObjectMapperKey = "SP2DynamoDBObjectMapper"

        const val DMFUSERDATAHISTORYFROMS3TableName = "DMF_USER_DATA_CAPITAL_FROM_S3"
        const val DMFUSERDATAASSETFROMS3TableName = "DMF_USER_DATA_ASSET_FROM_S3"
        const val DMFUSERDATAASSETUPLOADEDTableName = "DMF_USER_ASSET_UPLOADED"
        const val DMFUSERDATAHISTORYUPLOADEDTableName = "DMF_USER_HISTORY_UPLOADED"
        const val DMFNOTIFICATIONTableName = "DMF_NOTIFICATION"
        const val DMFSUBSCRIPTIONTableName = "DMF_SUBSCRIPTION"


        const val TASK_TYPE_MULTIPLIER = "Multiplier"
        const val TASK_TYPE_REDEEM = "Redeem"
        const val TASK_TYPE_TRANSFER = "Transfer"
        const val TASK_TYPE_OTHER = "Other"

        const val TASK_STATUS_OPEN = "Open"
        const val TASK_STATUS_DONE = "Done"
        const val TASK_STATUS_RESOLVED = "Resolve"
        const val TASK_STATUS_CLOSED = "Closed"
        const val TASK_STATUS_INPROGRESS = "In Progress"
        const val TASK_STATUS_WITHDRAWN = "Withdrawn"
        const val TASK_STATUS_OTHER = "Other"

    }

}