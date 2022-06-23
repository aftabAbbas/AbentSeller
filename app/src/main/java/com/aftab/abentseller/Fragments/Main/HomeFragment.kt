package com.aftab.abentseller.Fragments.Main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aftab.abentseller.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayout
import com.aftab.abentseller.Adapters.Pager.OrderPager

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var orderPager: OrderPager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        clickListeners()
        setPager()

        return binding.root
    }

    private fun clickListeners() {


    }

    private fun setPager() {

        orderPager = OrderPager(childFragmentManager)
        binding.vpOrder.adapter = orderPager

        binding.vpOrder.offscreenPageLimit = 3

        binding.vpOrder.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tlOrder))

        binding.tlOrder.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {

                        binding.vpOrder.currentItem = 0

                    }
                    1 -> {

                        binding.vpOrder.currentItem = 1

                    }
                    2 -> {

                        binding.vpOrder.currentItem = 2

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })


    }

}