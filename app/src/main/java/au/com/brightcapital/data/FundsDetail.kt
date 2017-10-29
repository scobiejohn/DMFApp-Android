package au.com.brightcapital.data

object FundsDetail {
    var funds: HashMap<String, FundInfo> = HashMap()
    var fundUpdated = false
}


class FundInfo(val inMarket: String, val investable: String)