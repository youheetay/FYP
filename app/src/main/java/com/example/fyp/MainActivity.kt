package com.example.fyp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.fyp.fragments.AccountFragment
import com.example.fyp.fragments.DashBoardFragment
import com.example.fyp.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import org.checkerframework.checker.nullness.qual.NonNull

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout : FrameLayout
    private lateinit var dashboardFragment: DashBoardFragment
    private lateinit var accountFragment: AccountFragment
    private lateinit var profileFragment: ProfileFragment
    private lateinit var insight: insight
//    private lateinit var savingFragment: SavingFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle("Welcome")

        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        bottomNavigationView = findViewById(R.id.bottomNavBar)
        frameLayout = findViewById(R.id.main_frame)

        val toggle = ActionBarDrawerToggle(
            this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        var navigationView : NavigationView = findViewById(R.id.naView)
        navigationView.setNavigationItemSelectedListener(this)

        dashboardFragment =  DashBoardFragment()
        accountFragment =  AccountFragment()
        profileFragment =  ProfileFragment()
        insight = insight()
//        savingFragment = SavingFragment()

        setFragment(dashboardFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            handleBottomNavigation(item)
            true
        }

    }

    private fun handleBottomNavigation(item: MenuItem) {
        when (item.itemId) {
            R.id.dashboard -> {
                val toolbar : Toolbar = findViewById(R.id.toolbar)
                toolbar.setTitle("Welcome")
                setFragment(dashboardFragment)
                bottomNavigationView.setItemBackgroundResource(R.color.black)
            }
            R.id.account -> {
                val toolbar : Toolbar = findViewById(R.id.toolbar)
                toolbar.setTitle("Wallet")
                setFragment(accountFragment)
                bottomNavigationView.setItemBackgroundResource(R.color.black)
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
            }

        }
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction : FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }


    override fun onBackPressed() {
        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)

        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(GravityCompat.END)
        }else{
            super.onBackPressed()
        }
    }

    private fun displaySelectedListener(itemId : Int){
        var fragment : Fragment? = when(itemId){
            R.id.dashboard -> {
                DashBoardFragment()
            }
            R.id.account ->
            {
                AccountFragment()
            }
            R.id.saving ->
            {
                SavingFragment()
            }
            R.id.profile ->
            {
                ProfileFragment()
            }
            else -> null
        }

        fragment?.let{
            setFragment(it)
        }

        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        displaySelectedListener(item.itemId)
        return true
    }
}