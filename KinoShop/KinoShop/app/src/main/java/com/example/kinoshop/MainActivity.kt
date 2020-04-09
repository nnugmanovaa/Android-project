package com.example.kinoshop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.kinoshop.api.Api
import com.example.kinoshop.model.Account
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val api = Api()
    val apiService = api.serviceInitialize()
    var isSignIn = false
    var sessionId = ""
    var account: Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTheme(R.style.AppTheme)
        val navController = findNavController(R.id.hostFragment)
        navigationView.setupWithNavController(navController)
    }
}
