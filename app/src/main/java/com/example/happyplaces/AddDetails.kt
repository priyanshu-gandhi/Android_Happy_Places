package com.example.happyplaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.databinding.ActivityAddDetailsBinding
import com.example.happyplaces.databinding.ActivityAddPlacesBinding

class AddDetails : AppCompatActivity() {

   private var binding:ActivityAddDetailsBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =  ActivityAddDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarAddPlace)

        if(supportActionBar != null)
        {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding?.detailsLocation?.setText((intent.getStringExtra("title")))
        binding?.detailsDesc?.setText(intent.getStringExtra("desc"))



    }
}