package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout : FrameLayout
    //        private lateinit var dashboardFragment: DashBoardFragment
//        private lateinit var accountFragment: AccountFragment
//        private lateinit var profileFragment: ProfileFragment
    private lateinit var insight: insight
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        val toolbar : Toolbar = findViewById(R.id.toolbar)
//        toolbar.setTitle("Expense Manager")
//
//        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        bottomNavigationView = findViewById(R.id.bottomNavBar)
//        frameLayout = findViewById(R.id.main_frame)
//
//        val toggle = ActionBarDrawerToggle(
//            this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close
//        )
//
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()

//        var navigationView : NavigationView = findViewById(R.id.bottomNavBar)
//        navigationView.setNavigationItemSelectedListener(this)

//        dashboardFragment =  DashBoardFragment()
//        accountFragment =  AccountFragment()
//        profileFragment =  ProfileFragment()
        insight = insight()

//        setFragment(dashboardFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            handleBottomNavigation(item)
            true
        }

    }

    private fun handleBottomNavigation(item: MenuItem) {
        when (item.itemId) {
            R.id.dashboard -> {
//                setFragment(dashboardFragment)
//                bottomNavigationView.setItemBackgroundResource(R.color.dashboard_color)
            }
            R.id.account -> {
//                setFragment(accountFragment)
//                bottomNavigationView.setItemBackgroundResource(R.color.account_color)
            }
            R.id.saving -> {
                val intent = Intent(this, insight::class.java)
                startActivity(intent)
                true // Return true to indicate that the item click is handled
            }
            R.id.profile -> {
//                setFragment(profileFragment)
//                bottomNavigationView.setItemBackgroundResource(R.color.profile_color)
            }

        }
    }
//
//    private fun setFragment(fragment: Fragment) {
//        val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.main_frame, fragment)
//        fragmentTransaction.commit()
//    }
//
//
//    override fun onBackPressed() {
//        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
//
//        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
//            drawerLayout.closeDrawer(GravityCompat.END)
//        }else{
//            super.onBackPressed()
//        }
//    }
//
//    private fun displaySelectedListener(itemId : Int){
//        var fragment : Fragment? = when(itemId){
//            R.id.dashboard -> {
//                DashBoardFragment()
//            }
//            R.id.account ->
//            {
//                AccountFragment()
//            }
//            R.id.saving ->
//            {
//                SavingFragment()
//            }
//            R.id.profile ->
//            {
//                ProfileFragment()
//            }
//            else -> null
//        }
//
//        fragment?.let{
//            setFragment(it)
//        }
//
//        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
//        drawerLayout.closeDrawer(GravityCompat.START)
//    }
//
//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        displaySelectedListener(item.itemId)
//        return true
//    }
}