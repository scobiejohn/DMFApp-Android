package au.com.dmf

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.text.Html
import android.view.Menu
import au.com.dmf.data.FragmentToActivity
import au.com.dmf.funds.BrightCapitalFragment
import au.com.dmf.funds.DMFFragment
import au.com.dmf.funds.FundsFragment
import au.com.dmf.settings.HtmlFileFragment
import au.com.dmf.settings.SettingsFragment
import au.com.dmf.tasks.TasksFragment
import com.androidnetworking.AndroidNetworking
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),
        FundsFragment.OnFragmentInteractionListener,
        TasksFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        HtmlFileFragment.OnFragmentInteractionListener,
        DMFFragment.OnFragmentInteractionListener,
        BrightCapitalFragment.OnFragmentInteractionListener {

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

}
