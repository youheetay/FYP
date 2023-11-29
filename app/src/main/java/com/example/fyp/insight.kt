package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class insight : AppCompatActivity() {

    private lateinit var tabLayout1 : TabLayout
    private lateinit var viewPagerHome : ViewPager2
    private lateinit var insightViewPagerAdapter : insightViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insight)

        tabLayout1 = findViewById(R.id.tab_layout_Home)
        viewPagerHome = findViewById(R.id.view_pager2)
        insightViewPagerAdapter =  insightViewPagerAdapter(this)
        viewPagerHome.setAdapter(insightViewPagerAdapter)

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
//        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.home -> {
//                    val intent = Intent(this, HomeActivity::class.java)
//                    startActivity(intent)
//                    true // Return true to indicate that the item click is handled
//                }
//
//                R.id.create -> {
//                    val intent = Intent(this, UserActivity::class.java)
//                    startActivity(intent)
//                    true // Return true to indicate that the item click is handled
//                }
//
//                R.id.profile -> {
//                    val intent = Intent(this, ProfileActivity::class.java)
//                    startActivity(intent)
//                    true // Return true to indicate that the item click is handled
//                }
//
//                else -> false // Return false for items that are not handled
//            }
//        }
    }
}