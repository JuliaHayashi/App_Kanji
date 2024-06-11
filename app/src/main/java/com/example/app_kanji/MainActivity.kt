package com.example.app_kanji

import Pesquisar
import android.content.Intent
import android.net.Uri
import com.example.app_kanji.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(Home())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){
                R.id.home -> replaceFragment(Home())
                R.id.foto -> replaceFragment(Foto())
                R.id.treinar -> replaceFragment(Treinar())
                R.id.pesquisar -> replaceFragment(Pesquisar())
                else -> { }
            }
            true
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        /*binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.home) {
                replaceFragment(Home())
            } else if (menuItem.itemId == R.id.foto) {
                replaceFragment(Foto())
            } else if (menuItem.itemId == R.id.pesquisar) {
                replaceFragment(Treinar())
            }
            // You can add more else-if blocks if needed for additional menu items


            true // Return true to indicate that the item has been selected
        }*/
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configuracoes -> replaceFragment(Configuracoes())
            R.id.email -> enviarEmail()
            R.id.info -> replaceFragment(Info())
            R.id.login -> Toast.makeText(this, "Login!!!", Toast.LENGTH_SHORT).show()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)

        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun enviarEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("kanji.app@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "")
            putExtra(Intent.EXTRA_TEXT, "")
            setPackage("com.google.android.gm")
        }
        startActivity(emailIntent)
    }


    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()

        //        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, Configuracoes()).commit()

    }
}
