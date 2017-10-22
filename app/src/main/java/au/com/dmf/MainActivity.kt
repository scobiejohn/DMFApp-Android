package au.com.dmf

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.text.Html
import android.widget.Toast
import au.com.dmf.data.FragmentToActivity
import au.com.dmf.data.FundInfo
import au.com.dmf.data.FundsDetail
import au.com.dmf.events.GetFundStateEvent
import au.com.dmf.funds.BrightCapitalFragment
import au.com.dmf.funds.DMFFragment
import au.com.dmf.funds.FundsFragment
import au.com.dmf.services.DynamoDBManager
import au.com.dmf.settings.HtmlFileFragment
import au.com.dmf.settings.SettingsFragment
import au.com.dmf.tasks.TasksFragment
import com.androidnetworking.AndroidNetworking
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.android.synthetic.main.activity_main.*
import work.wanghao.rxbus2.RxBus
import android.graphics.Color
import android.net.Uri
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog


class MainActivity : AppCompatActivity(),
        FundsFragment.OnFragmentInteractionListener,
        TasksFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        HtmlFileFragment.OnFragmentInteractionListener,
        DMFFragment.OnFragmentInteractionListener,
        BrightCapitalFragment.OnFragmentInteractionListener {

    private var doubleBackToExitPressedOnce = false

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onFragmentInteraction(fta: FragmentToActivity) {
        supportActionBar?.title = Html.fromHtml("<font color='#ffffff'>" + fta.name + "</font>")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content, FundsFragment.newInstance("", ""))
        transaction.commit()

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        AndroidNetworking.initialize(applicationContext)

        val extras = intent.extras
        val loginKey = extras.getString("loginKey")
        val loginValue = extras.getString("loginValue")
        val logins = HashMap<String, String>()
        logins.put(loginKey, loginValue)

        DynamoDBManager.initClient(this, logins)

        DynamoDBManager.getFundDetails("Darling Macro Fund", {row ->
            val fundInfo = FundInfo(row.InMarket!!, row.Investable!!)
            FundsDetail.funds.put("Darling Macro Fund", fundInfo)
            RxBus.Companion.get().post(GetFundStateEvent())
        }, {})

        if (DynamoDBManager.needRegisterFCMToken) {
            DynamoDBManager.needRegisterFCMToken = false
            DynamoDBManager.subscribeToTopic()
        }

        checkAppVersion()
    }

    private fun checkAppVersion() {
        if (FirebaseApp.getApps(applicationContext).isEmpty()) {
            FirebaseApp.initializeApp(applicationContext, FirebaseOptions.fromResource(applicationContext))
        }
        val config = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        config.setConfigSettings(configSettings)


        config.fetch(0)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@MainActivity, "Fetch Succeeded",
                                Toast.LENGTH_SHORT).show()

                        // After config data is successfully fetched, it must be activated before newly fetched
                        // values are returned.
                        config.activateFetched()
                    } else {
                        Toast.makeText(this@MainActivity, "Fetch Failed",
                                Toast.LENGTH_SHORT).show()
                    }

                    val playStoreVersionName = FirebaseRemoteConfig.getInstance().getString(
                            "android_latest_version_name")
                    val playStoreVersionCode = FirebaseRemoteConfig.getInstance().getString(
                            "android_latest_version_code")
                    println(playStoreVersionName)
                    println(playStoreVersionCode)

                    val pInfo = this.packageManager.getPackageInfo(packageName, 0)
                    val currentAppVersionCode = pInfo.versionCode.toString()
                    val currentAppVersionName = pInfo.versionName

                    if (playStoreVersionCode != currentAppVersionCode) {
                        showAppVersionReminder()
                    }
                }
    }

    private fun showAppVersionReminder() {
        MaterialDialog.Builder(this)
                .title("Reminder")
                .content("A new version of DMF is available on Google Play Store.")
                .positiveText("Update")
                .negativeColor(Color.GRAY)
                .negativeText("Cancel")
                .onAny { _, which ->
                    if (which == DialogAction.POSITIVE) {
                        val appPackageName = "au.com.dmf"
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
                        } catch (err: Exception) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)))
                        }
                    }
                }
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment?

        when (item.itemId) {
            R.id.navigation_funds -> {
                println("FUNDS")
                val fundsTransaction = supportFragmentManager.beginTransaction()
                selectedFragment = FundsFragment.newInstance("", "")
                fundsTransaction.replace(R.id.content, selectedFragment)
                fundsTransaction.commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_tasks -> {
                println("TASKS")

                val tasksTransaction = supportFragmentManager.beginTransaction()
                selectedFragment = TasksFragment.newInstance("", "")
                tasksTransaction.replace(R.id.content, selectedFragment)
                tasksTransaction.commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_settings -> {
                println("SETTINGS")

                val settingsTransaction = supportFragmentManager.beginTransaction()
                selectedFragment = SettingsFragment.newInstance("", "")
                settingsTransaction.replace(R.id.content, selectedFragment)
                settingsTransaction.commit()

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            if (!doubleBackToExitPressedOnce) {
                doubleBackToExitPressedOnce = true
                Toast.makeText(this,"Press BACK again to exit app.", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    doubleBackToExitPressedOnce = false
                }, 2000)
            } else {
                super.onBackPressed()
            }
        }
    }

}
