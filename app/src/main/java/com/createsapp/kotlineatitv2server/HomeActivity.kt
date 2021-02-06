package com.createsapp.kotlineatitv2server

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import com.createsapp.kotlineatitv2server.common.Common
import com.createsapp.kotlineatitv2server.eventbus.CategoryClick
import com.createsapp.kotlineatitv2server.eventbus.ChangeMenuClick
import com.createsapp.kotlineatitv2server.eventbus.ToastEvent
import com.google.firebase.auth.FirebaseAuth
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private var menuclick: Int = -1
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var navView:NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_category, R.id.nav_food_list
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { item ->

            item.isChecked = true
            drawerLayout!!.closeDrawers()

            when (item.itemId) {
                R.id.nav_sign_out -> {
                    signOut()
                }
                R.id.nav_category -> {
                    if (menuclick != item.itemId)
                        navController.navigate(R.id.nav_category)
                }

            }

            menuclick = item.itemId
            true
        }
    }

    private fun signOut() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Sign out")
            .setMessage("Doy you really want to exit?")
            .setNegativeButton("CANCEL") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("OK") { _, _ ->
                Common.foodSelected = null
                Common.categorySelected = null
                Common.currentServerUser = null
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this@HomeActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onCategoryClick(event: CategoryClick) {
        if (event.isSuccess)
        {
            if (menuclick != R.id.nav_food_list)
            {
                navController.navigate(R.id.nav_food_list)
                menuclick = R.id.nav_food_list
            }
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onChangeMenuEvent(event: ChangeMenuClick)
    {

        if (!event.isFromFoodList)
        {
            //Clear
            navController!!.popBackStack(R.id.nav_category, true)
            navController!!.navigate(R.id.nav_category)
        }

        menuclick = -1
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onToastEvent(event: ToastEvent)
    {
        
        if (event.isUpdate)
        {
            Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
        } else 
        {
            Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show()
        }

        EventBus.getDefault().postSticky(ChangeMenuClick(event.isBackFromFoodList))
    }

}