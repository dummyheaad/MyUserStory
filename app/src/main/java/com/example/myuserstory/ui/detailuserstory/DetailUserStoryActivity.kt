package com.example.myuserstory.ui.detailuserstory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.myuserstory.R
import com.example.myuserstory.data.remote.response.DetailStoryResponse
import com.example.myuserstory.databinding.ActivityDetailUserStoryBinding
import com.example.myuserstory.helper.ViewModelFactory
import com.example.myuserstory.ui.main.MainActivity
import com.example.myuserstory.utils.getAddressName
import com.example.myuserstory.utils.withDateFormat

class DetailUserStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserStoryBinding
    private lateinit var detailUserStoryViewModel: DetailUserStoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailUserStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_title)

        val factory = ViewModelFactory.getInstance(this)
        detailUserStoryViewModel = ViewModelProvider(this, factory)[DetailUserStoryViewModel::class.java]

        val userId = intent.getStringExtra(ID).toString()
        val sharedPreferences = getSharedPreferences(MainActivity.MY_PREFS, MODE_PRIVATE)
        val token = sharedPreferences.getString(MainActivity.TOKEN, "").toString()

        detailUserStoryViewModel.displayDetailUserStory(token, userId).observe(this) { detailUser ->
            setDetailUser(detailUser)
        }

        detailUserStoryViewModel.repository.remoteData.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setDetailUser(detail: DetailStoryResponse) {
        val photo = detail.story?.photoUrl
        val name = detail.story?.name
        val timeCreated = detail.story?.createdAt?.withDateFormat()
        val description = detail.story?.description
        val lat = detail.story?.lat
        val lon = detail.story?.lon
        val location = getAddressName(this@DetailUserStoryActivity, lat!!, lon!!)
        Glide.with(this)
            .load(photo)
            .into(binding.ivDetailPhoto)

        binding.apply {
            tvDetailName.text = name
            tvDetailCreated.text = timeCreated
            tvDetailDescription.text = description
            tvDetailLocation.text = location
        }

        if (lon == 0.0 && lat == 0.0) {
            binding.tvDetailLocation.text = getString(R.string.no_detail_location)
            binding.ivDetailLocationIcon.setImageResource(R.drawable.baseline_do_not_disturb_24)
        }
    }

    companion object {
        const val ID = "id"
    }

    private fun showLoading(isLoading: Boolean) {
        binding.detailProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}