package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager2.widget.ViewPager2
import com.example.fyp.fragments.AccountFragment
import com.example.fyp.fragments.DashBoardFragment
import com.example.fyp.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class Insights : Fragment() {

    private lateinit var tabLayout1: TabLayout
    private lateinit var viewPagerHome: ViewPager2
    private lateinit var insightViewPagerAdapter: insightViewPagerAdapter
    private lateinit var frameLayout: FrameLayout
    private lateinit var dashboardFragment: DashBoardFragment
    private lateinit var accountFragment: AccountFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var insights: Insights

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_insight)
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_insights, container, false)
//        val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)

        tabLayout1 = rootView.findViewById(R.id.tab_layout_Home)
        viewPagerHome = rootView.findViewById(R.id.view_pager2)
        insightViewPagerAdapter = insightViewPagerAdapter(requireActivity())
        viewPagerHome.setAdapter(insightViewPagerAdapter)
        dashboardFragment = DashBoardFragment()
        accountFragment = AccountFragment()
        profileFragment = ProfileFragment()
        insights = Insights()

//        val bottomNavigationView = rootView.findViewById<BottomNavigationView>(R.id.bottomNavBar)
        //tabLayout.setupWithViewPager(viewPager2)

        tabLayout1.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPagerHome.setCurrentItem(tab.getPosition())
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        viewPagerHome.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout1.getTabAt(position)?.select()
            }
        })

//        // Set up a listener for item clicks
//        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
//            // Inside your bottomNavigationView.setOnNavigationItemSelectedListener block
//            when (menuItem.itemId) {
//                R.id.dashboard -> {
//                    Log.d("Insight", "Setting fragment: $dashboardFragment")
//                    val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
//                    toolbar.setTitle("Welcome")
//                    setFragment(dashboardFragment)
//                    true
//                }
//
//                R.id.account -> {
//                    Log.d("Insight", "Setting fragment: $accountFragment")
//                    val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
//                    toolbar.setTitle("Wallet")
//                    setFragment(accountFragment)
//                    true
//                }
//
//                R.id.saving -> {
//                    val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
//                    toolbar.setTitle("Insight")
//                    setFragment(insights)
//
////                    val intent = Intent(this, insight::class.java)
////                    startActivity(intent)
//                    true // Return true to indicate that the item click is handled
//                }
//
//                R.id.profile -> {
//                    val toolbar: Toolbar = rootView.findViewById(R.id.toolbar)
//                    toolbar.setTitle("Profile")
//                    setFragment(profileFragment)
//                    true
//                }
//
//                else -> false // Return false for items that are not handled
//            }
//
//        }

        return rootView
    }


    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = childFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }

}