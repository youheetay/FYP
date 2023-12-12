package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class insight : AppCompatActivity() {

    private lateinit var tabLayout1 : TabLayout
    private lateinit var viewPagerHome : ViewPager2
    private lateinit var insightViewPagerAdapter : insightViewPagerAdapter
    private lateinit var frameLayout : FrameLayout
    private lateinit var dashboardFragment: DashBoardFragment
    private lateinit var accountFragment: AccountFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insight)

        val toolbar : Toolbar = findViewById(R.id.toolbar)

        tabLayout1 = findViewById(R.id.tab_layout_Home)
        viewPagerHome = findViewById(R.id.view_pager2)
        insightViewPagerAdapter =  insightViewPagerAdapter(this)
        viewPagerHome.setAdapter(insightViewPagerAdapter)
        dashboardFragment =  DashBoardFragment()
        accountFragment =  AccountFragment()
        profileFragment =  ProfileFragment()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavBar)
        //tabLayout.setupWithViewPager(viewPager2)

        tabLayout1.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

            override fun onTabSelected(tab: TabLayout.Tab){
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

        // Set up a listener for item clicks
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.dashboard -> {
                    val toolbar : Toolbar = findViewById(R.id.toolbar)
                    toolbar.setTitle("Welcome")
                    setFragment(dashboardFragment)
                  true
                }
                R.id.account -> {
                    val toolbar : Toolbar = findViewById(R.id.toolbar)
                    toolbar.setTitle("Wallet")
                    setFragment(accountFragment)
                    bottomNavigationView.setItemBackgroundResource(R.color.black)
                    true
                }
                R.id.saving -> {
//                val toolbar : Toolbar = findViewById(R.id.toolbar)
//                toolbar.setTitle("Saving Plan")
//                setFragment(savingFragment)
//                bottomNavigationView.setItemBackgroundResource(R.color.black)
                    val intent = Intent(this, insight::class.java)
                    startActivity(intent)
                    true // Return true to indicate that the item click is handled
                }
                R.id.profile -> {val toolbar : Toolbar = findViewById(R.id.toolbar)
                    toolbar.setTitle("Profile")
                    setFragment(profileFragment)
                    bottomNavigationView.setItemBackgroundResource(R.color.black)
                    true
                }
                else -> false // Return false for items that are not handled
            }
        }


    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }
}