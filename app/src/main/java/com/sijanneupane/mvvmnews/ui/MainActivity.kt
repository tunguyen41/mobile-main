package com.sijanneupane.mvvmnews.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sijanneupane.mvvmnews.R
import com.sijanneupane.mvvmnews.db.ArticleDatabase
import com.sijanneupane.mvvmnews.repository.NewsRepository

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = ArticleDatabase.getInstance(this)
        val repository = NewsRepository(db)
        val factory = NewsViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, factory)[NewsViewModel::class.java]

        // ✅ BẮT BUỘC để tab chuyển được
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.newsNavHostFrag) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
            R.id.bottomNavigationView
        )
        bottomNav.setupWithNavController(navController)
    }
}
