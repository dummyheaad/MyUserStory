package com.example.myuserstory.ui.main

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myuserstory.R
import com.example.myuserstory.adapter.LoadingStateAdapter
import com.example.myuserstory.adapter.UserStoryAdapter
import com.example.myuserstory.databinding.ActivityMainBinding
import com.example.myuserstory.helper.ViewModelFactory
import com.example.myuserstory.ui.addnewuserstory.AddNewUserStoryActivity
import com.example.myuserstory.ui.maps.MapsActivity
import com.example.myuserstory.ui.signin.SignInActivity
import com.example.myuserstory.ui.signin.SignInViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var signInViewModel: SignInViewModel

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE)
        editor = sharedPreferences.edit()

        supportActionBar?.title = getString(R.string.dashboard_title)

        val factory = ViewModelFactory.getInstance(this)
        signInViewModel = ViewModelProvider(this, factory)[SignInViewModel::class.java]
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        val adapter = UserStoryAdapter()

        showLoading(true)

        signInViewModel.getUser().observe(this) { user ->
            if (user.userId.isEmpty()) {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                editor.putString(TOKEN, user.token)
                editor.apply()

                binding.rvListUserStory.adapter = adapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        adapter.retry()
                    }
                )
                mainViewModel.getAllStory(user.token).observe(this) {
                    Log.e("List", it.toString())
                    adapter.submitData(lifecycle, it)
                    showLoading(false)
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvListUserStory.layoutManager = layoutManager

        binding.fabAddNewStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddNewUserStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.language_setting -> {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            R.id.btn_sign_out -> {
                editor.clear()
                editor.apply()

                signInViewModel.signout()

                Toast.makeText(this@MainActivity, getString(R.string.sign_out_status_success), Toast.LENGTH_SHORT).show()
            }
            R.id.btn_maps -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.mainProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val MY_PREFS = "MY_PREFS"
        const val TOKEN = "TOKEN"
    }
}