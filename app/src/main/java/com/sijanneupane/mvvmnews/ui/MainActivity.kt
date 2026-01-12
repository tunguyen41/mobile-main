package com.sijanneupane.mvvmnews.ui

import android.os.Bundle
import android.view.View // <--- QUAN TRỌNG: Cần import cái này để dùng View.GONE
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sijanneupane.mvvmnews.R
import com.sijanneupane.mvvmnews.db.ArticleDatabase
import com.sijanneupane.mvvmnews.repository.NewsRepository

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setup ViewModel
        val db = ArticleDatabase(this)
        val repository = NewsRepository(db)
        val factory = NewsViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        // 2. Setup Navigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFrag) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(navController)

        // --- 3. ĐOẠN CODE MỚI ĐỂ ẨN MENU ---
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if(destination.id == R.id.loginFragment || destination.id == R.id.registerFragment) {
                // Nếu đang ở Login hoặc Register thì ẨN menu đi
                bottomNav.visibility = View.GONE
            } else {
                // Các màn hình khác thì HIỆN menu lên
                bottomNav.visibility = View.VISIBLE
            }
        }
    }
}