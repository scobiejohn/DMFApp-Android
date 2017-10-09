package au.com.dmf.data

object FundsDetail {
    var funds: HashMap<String, FundInfo> = HashMap()
}


class FundInfo(val inMarket: String, val investable: String)