package com.aftab.abentseller.Adapters.Pager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.aftab.abentseller.Fragments.Home.ActiveOrderFragment
import com.aftab.abentseller.Fragments.Home.CanceledOrderFragment
import com.aftab.abentseller.Fragments.Home.DeliveredOrderFragment
@Suppress("deprecation")
class OrderPager(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ActiveOrderFragment()
            }
            1 -> {
                DeliveredOrderFragment()
            }
            /*2 -> {
                CanceledOrderFragment()
            }*/

            else -> ActiveOrderFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }
}