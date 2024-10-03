package com.example.app_kanji

import com.example.app_kanji.Pesquisar.Pesquisar
import android.content.Intent
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
import com.example.app_kanji.Treinar.Treinar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

    if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Verifica se há um fragmento a ser carregado a partir do Intent
        intent.getStringExtra("fragment")?.let { fragmentName ->
            when (fragmentName) {
                "treinar" -> {
                    replaceFragment(Treinar())
                    binding.bottomNavigationView.selectedItemId = R.id.treinar // Seleciona o item "Treinar"
                }
                else -> {
                   // replaceFragment(Home())
                    //binding.bottomNavigationView.selectedItemId = R.id.home // Seleciona o item "Home"
                    replaceFragment(Foto())
                    binding.bottomNavigationView.selectedItemId = R.id.foto // Seleciona o item "Home"
                }
            }
        } ?: run {
            //replaceFragment(Home()) // Caso nenhum extra esteja presente
            //binding.bottomNavigationView.selectedItemId = R.id.home // Seleciona o item "Home" como padrão
            replaceFragment(Foto())
            binding.bottomNavigationView.selectedItemId = R.id.foto
        }

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                //R.id.home -> {
               //     replaceFragment(Home())
                //    true
                //}
                R.id.foto -> {
                    replaceFragment(Foto())
                    true
                }
                R.id.treinar -> {
                    replaceFragment(Treinar())
                    true
                }
                R.id.pesquisar -> {
                    replaceFragment(Pesquisar())
                    true
                }
                else -> false
            }
        }


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
            //R.id.configuracoes -> replaceFragment(Configuracoes())
            R.id.email -> enviarEmail()
            R.id.info -> replaceFragment(Info())
            R.id.login -> logout()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    override fun onBackPressed() {
        super.onBackPressed()
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

        // Atualiza o título da Toolbar com base no fragmento
        val title = when (fragment) {
            is Home -> "Home"
            is Foto -> "Foto"
            is Treinar -> "Treinar"
            is Pesquisar -> "Pesquisar"
            else -> "App Kanji" // Título padrão
        }
        supportActionBar?.title = title // Atualiza o título da Toolbar
    }
}
