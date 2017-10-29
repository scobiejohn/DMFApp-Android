package au.com.brightcapital.funds

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class FundFragmentAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getCount():Int {
        return 1
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            DMFFragment.newInstance("", "")
        }
        else {
            BrightCapitalFragment.newInstance("", "")
        }
    }


    /*
    val fragments: MutableList<Fragment> = mutableListOf()

    var last: Int = 0
        set(last) {
            field = last
            notifyDataSetChanged()
        }

    fun addFragment(fragment: Fragment) = fragments.add(fragment)

    fun indexOf(fragment: Fragment): Int = fragments.indexOf(fragment)

    override fun getCount(): Int = if (last < fragments.size) last + 1 else 0

    override fun getItem(position: Int): Fragment? = fragments[position]
    */

}